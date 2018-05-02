package com.github.rahul.githuboauth

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
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


class GithubOAuthDialogFragment : DialogFragment() {
    private var CLIENT_ID = ""
    private var CLIENT_SECRET = ""
    private var debug = false
    private var scope = ""
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
        scope = arguments!!.getString("scope", "")
        debug = arguments!!.getBoolean("debug", false)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_github_oauth, container, false)
        setupWebview(view)
        buildURL(view)
        return view
    }

    private fun buildURL(view: View) {
        var urlToLoad = "${GithubAuthenticator.GITHUB_URL}?client_id=$CLIENT_ID"
        if (scope.isNotEmpty()) {
            urlToLoad += "&scope=$scope"
        }
        view.webView.loadUrl(urlToLoad)
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
                        getOAuthTokenFromCode(view, code)
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

    fun getOAuthTokenFromCode(view: View, code: String) {
        view.progressView.visibility = View.VISIBLE
        val client = OkHttpClient()
        val url = HttpUrl.parse(GithubAuthenticator.GITHUB_OAUTH).newBuilder()
        url.addQueryParameter("client_id", CLIENT_ID)
        url.addQueryParameter("client_secret", CLIENT_SECRET)
        url.addQueryParameter("code", code)

        val urlOauth = url.build().toString()

        val request = Request.Builder()
                .header("Accept", "application/json")
                .url(urlOauth)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request?, exp: IOException) {
                if (mErrorCallback != null) {
                    mErrorCallback!!.onError(exp)
                }
                if (debug) {
                    Log.d(TAG, "IOException: " + exp.message)
                }
                dismiss()
            }

            @Throws(IOException::class)
            override fun onResponse(response: Response) {

                if (response.isSuccessful) {
                    val jsonData = response.body().string()
                    if (debug) {
                        Log.d(TAG, "response is: $jsonData")
                    }
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val authToken = jsonObject.getString("access_token")
                        if (mSuccessCallback != null) {
                            mSuccessCallback!!.onSuccess(authToken)
                        }
                        if (debug) {
                            Log.d(TAG, "token is: $authToken")
                        }

                    } catch (exp: JSONException) {
                        if (mErrorCallback != null) {
                            mErrorCallback!!.onError(exp)
                        }
                        if (debug) {
                            Log.d(TAG, "json exception: " + exp.message)
                        }
                    }

                } else {
                    if (mErrorCallback != null) {
                        mErrorCallback!!.onError(Exception(response.message()))
                    }
                    if (debug) {
                        Log.d(TAG, "onResponse: not success: " + response.message())
                    }
                }
                dismiss()
            }
        })
    }


    override fun onDetach() {
        super.onDetach()
        mErrorCallback = null
        mSuccessCallback = null
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
