package com.github.rahul.github_oauth

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity

import java.util.ArrayList

class GithubAuthenticator(val context: Context) {
    private var authFragment: GithubOAuthDialogFragment? = null
    /**
     * This method will execute the instance created. The activity of login will be launched and
     * it will return a result after finishing its execution. The result will be one of the constants
     * hold in the class [ResultCode]
     * client_id, client_secret, package name and activity fully qualified are required
     */
    fun authenticate() {
        authFragment?.show((context as AppCompatActivity).supportFragmentManager, "Auth")
    }

    companion object {
        val GITHUB_URL = "https://github.com/login/oauth/authorize"
        val GITHUB_OAUTH = "https://github.com/login/oauth/access_token"
        val TOKEN = "TOKEN"
        val REQUEST_CODE = 1000
        val SUCCESS = -1
        val ERROR = 0
        fun builder(context: Context): Builder {
            return Builder(context)
        }
    }

    class Builder(val context: Context) {

        private var mClientId: String? = null
        private var mClientSecret: String? = null
        private var mIsDebug: Boolean = false
        private var mScopeList: ArrayList<String>? = null
            set(scopeList) {
                field = ArrayList()
                field = scopeList
            }
        private var clearBeforeLaunch: Boolean = false
        private var mSuccessCallback: SuccessCallback? = null
        private var mErrorCallback: ErrorCallback? = null

        fun clientId(clientId: String): Builder {
            mClientId = clientId
            return this
        }

        fun clientSecret(clientSecret: String): Builder {
            mClientSecret = clientSecret
            return this
        }


        fun debug(active: Boolean): Builder {
            mIsDebug = active
            return this
        }

        fun scopeList(scopeList: ArrayList<String>): Builder {
            mScopeList = scopeList
            return this
        }

        /**
         * Whether the app should clear all data (cookies and cache) before launching a new instance of
         * the webView
         *
         * @param clearBeforeLaunch true to clear data
         * @return An instance of this class
         */
        fun clearBeforeLaunch(clearBeforeLaunch: Boolean): Builder {
            this.clearBeforeLaunch = clearBeforeLaunch
            return this
        }

        /**
         * Callback of decoding process
         *
         * @param callback Callback
         * @see DecodeCallback
         */
        @MainThread
        fun onSuccess(callback: SuccessCallback?): Builder {
            mSuccessCallback = callback
            return this
        }

        /**
         * Camera initialization error callback.
         * If not set, an exception will be thrown when error will occur.
         *
         * @param callback Callback
         * @see ErrorCallback.SUPPRESS
         *
         * @see ErrorCallback
         */
        @MainThread
        fun onError(callback: ErrorCallback?): Builder {
            mErrorCallback = callback
            return this
        }

        fun build(): GithubAuthenticator {
            val args = Bundle()
            args.putString("id", mClientId)
            args.putString("secret", mClientSecret)
            val authFragment = GithubOAuthDialogFragment.newInstance(args)
            authFragment.mSuccessCallback = mSuccessCallback
            authFragment.mErrorCallback = mErrorCallback
            val authenticator = GithubAuthenticator(context)
            authenticator.authFragment = authFragment
            return authenticator
        }

    }
}
