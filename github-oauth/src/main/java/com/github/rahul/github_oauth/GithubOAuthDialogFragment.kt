package com.github.rahul.github_oauth

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.fragment_github_oauth.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GithubOAuthDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class GithubOAuthDialogFragment : DialogFragment() {
    private var mListener: OnFragmentInteractionListener? = null

    var CLIENT_ID = ""
    var CLIENT_SECRET = ""
    var debug = true
    private val TAG = "github-oauth"
    var mSuccessCallback: SuccessCallback? = null
    var mErrorCallback: ErrorCallback? = null

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CLIENT_ID = arguments!!.getString("id", "")
        CLIENT_SECRET = arguments!!.getString("secret", "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_github_oauth, container, false)
        setupWebview(view)
        buildURL(view)
        return view
    }

    private fun buildURL(view: View) {
        var url_load = "${GithubAuthenticator.GITHUB_URL}?client_id=$CLIENT_ID"
        view.webView.loadUrl(url_load)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview(view: View) {
        view.webView.settings.javaScriptEnabled = true
        view.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(webview: WebView?, url: String?) {
                super.onPageFinished(webview, url)
                view.progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                super.shouldOverrideUrlLoading(webView, url)
                view.progressBar.visibility = View.VISIBLE
                // Try catch to allow in app browsing without crashing.
                try {
                    val code = Uri.parse(url).getQueryParameter("code")
                    if (code != null) {
                        view.webView.visibility = View.GONE
                        getOAuthTokenFromCode(code)
                        if (debug) {
                            Log.d(TAG, "code fetched is: $code")
                        }
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (e: ArrayIndexOutOfBoundsException) {
                    e.printStackTrace()
                }

                return false
            }
        }

    }

    fun getOAuthTokenFromCode(code: String) {
        val client = OkHttpClient()
        val url = HttpUrl.parse(GithubAuthenticator.GITHUB_OAUTH).newBuilder()
        url.addQueryParameter("client_id", CLIENT_ID)
        url.addQueryParameter("client_secret", CLIENT_SECRET)
        url.addQueryParameter("code", code)

        val url_oauth = url.build().toString()

        val request = Request.Builder()
                .header("Accept", "application/json")
                .url(url_oauth)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request?, exp: IOException) {
                //finishActivityWithResult(Constants.ERROR, null)
                mErrorCallback!!.onError(exp)
                if (debug) {
                    Log.d(TAG, "IOException: " + exp.message)
                }
            }

            @Throws(IOException::class)
            override fun onResponse(response: Response) {

                if (response.isSuccessful) {
                    val JsonData = response.body().string()
                    if (debug) {
                        Log.d(TAG, "response is: $JsonData")
                    }
                    try {
                        val jsonObject = JSONObject(JsonData)
                        val authToken = jsonObject.getString("access_token")
                        mSuccessCallback!!.onSuccess(authToken)
                        if (debug) {
                            Log.d(TAG, "token is: $authToken")
                        }

                    } catch (exp: JSONException) {
                        mErrorCallback!!.onError(exp)
                        // finishActivityWithResult(Constants.ERROR, null)
                        if (debug) {
                            Log.d(TAG, "json exception: " + exp.message)
                        }
                    }

                } else {
                    mErrorCallback!!.onError(Exception(response.message()))
                    // finishActivityWithResult(Constants.ERROR, null)
                    if (debug) {
                        Log.d(TAG, "onResponse: not success: " + response.message())
                    }
                }
            }
        })
    }


    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }



    override fun onDetach() {
        super.onDetach()
        mErrorCallback = null
        mSuccessCallback = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        fun newInstance(args: Bundle): GithubOAuthDialogFragment {
            val f = GithubOAuthDialogFragment()
            f.arguments = args
            return f
        }
    }
}