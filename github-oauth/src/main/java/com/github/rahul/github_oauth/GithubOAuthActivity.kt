package com.github.rahul.github_oauth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.rahul.github_oauth.Constants.ERROR
import com.github.rahul.github_oauth.Constants.GITHUB_OAUTH
import com.github.rahul.github_oauth.Constants.GITHUB_URL
import com.github.rahul.github_oauth.Constants.SUCCESS
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_github_oauth.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class GithubOAuthActivity : AppCompatActivity() {
    var CLIENT_ID = "fbd747cbb00a75eda14f"
    var CLIENT_SECRET = ""
    var debug = false
    private val TAG = "github-oauth"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_oauth)

        setupWebview()
        getBuilderParams()
        buildURL()

    }

    private fun getBuilderParams() {
        if (intent.extras != null) run {
            CLIENT_ID = intent.getStringExtra("id")
            CLIENT_SECRET = intent.getStringExtra("secret")
        }

    }

    private fun buildURL() {
        var url_load = "$GITHUB_URL?client_id=$CLIENT_ID"
        webView.loadUrl(url_load)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                super.shouldOverrideUrlLoading(view, url)
                progressBar.visibility = View.VISIBLE
                // Try catch to allow in app browsing without crashing.
                try {
                    val code = Uri.parse(url).getQueryParameter("code")
                    if (code != null) {
                        webView.visibility = View.GONE
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
        progressBar.visibility = View.VISIBLE
        val client = OkHttpClient()
        val url = HttpUrl.parse(GITHUB_OAUTH).newBuilder()
        url.addQueryParameter("client_id", CLIENT_ID)
        url.addQueryParameter("client_secret", CLIENT_SECRET)
        url.addQueryParameter("code", code)

        val url_oauth = url.build().toString()

        val request = Request.Builder()
                .header("Accept", "application/json")
                .url(url_oauth)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException) {
                finishActivityWithResult(ERROR, null)
                if (debug) {
                    Log.d(TAG, "IOException: " + e.message)
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
                        finishActivityWithResult(SUCCESS, authToken)
                        if (debug) {
                            Log.d(TAG, "token is: $authToken")
                        }

                    } catch (exp: JSONException) {
                        finishActivityWithResult(ERROR, null)
                        if (debug) {
                            Log.d(TAG, "json exception: " + exp.message)
                        }
                    }

                } else {
                    finishActivityWithResult(ERROR, null)
                    if (debug) {
                        Log.d(TAG, "onResponse: not success: " + response.message())
                    }
                }
            }
        })
    }


    /**
     * Finish this activity and returns the result
     *
     * @param resultCode one of the constants from the class ResultCode
     */
    private fun finishActivityWithResult(resultCode: Int, token: String?) {
        if (resultCode == SUCCESS) {
            val intent = Intent()
            intent.putExtra(Constants.TOKEN, token!!)
            setResult(resultCode, intent)
        } else {
            setResult(resultCode)
        }
        finish()
    }

}
