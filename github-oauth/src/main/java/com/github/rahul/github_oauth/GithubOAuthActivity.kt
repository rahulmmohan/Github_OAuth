package com.github.rahul.github_oauth

import android.annotation.SuppressLint
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.rahul.github_oauth.Constants.GITHUB_URL
import kotlinx.android.synthetic.main.activity_github_oauth.*

class GithubOAuthActivity : AppCompatActivity() {
    var CLIENT_ID = "fbd747cbb00a75eda14f"
    var CLIENT_SECRET = ""
    var debug = true
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
                        //fetchOauthTokenWithCode(code)
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
}
