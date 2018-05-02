package com.github.rahul.githuboauth

import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity
import java.util.*

class GithubAuthenticator(private val context: Context) {
    private var authFragment: GithubOAuthDialogFragment? = null
    /**
     * This method will execute the instance created. The activity of login will be launched and
     * it will return a result after finishing its execution. The result will be one of the callback
     * [SuccessCallback] or [ErrorCallback]
     * client_id, client_secret are required
     */
    fun authenticate() {
        authFragment?.show((context as AppCompatActivity).supportFragmentManager, "Auth")
    }

    companion object {
        const val GITHUB_URL = "https://github.com/login/oauth/authorize"
        const val GITHUB_OAUTH = "https://github.com/login/oauth/access_token"
        fun builder(context: Context): Builder {
            return Builder(context)
        }
    }

    class Builder(private val context: Context) {

        private var mClientId: String? = null
        private var mClientSecret: String? = null
        private var mIsDebug: Boolean = false
        private var mScopeList: ArrayList<String>? = null
            set(scopeList) {
                field = ArrayList()
                field = scopeList
            }
        private var mHasScopes = false
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
            if (mScopeList != null && mScopeList!!.isNotEmpty()) {
                mHasScopes = true
            }
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
         * Callback of success process
         *
         * @param callback Callback
         * @see SuccessCallback
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
         *
         * @see ErrorCallback
         */
        @MainThread
        fun onError(callback: ErrorCallback?): Builder {
            mErrorCallback = callback
            return this
        }

        /**
         * Generate a comma separated list of scopes out of the
         *
         * @param scopeList list of scopes as defined
         * @return comma separated list of scopes
         */
        private fun getScopeFromList(scopeList: ArrayList<String>?): String {
            var scopeString = ""

            for (scope in scopeList!!) {
                if (scopeString != "") {
                    scopeString += ","
                }

                scopeString += scope
            }
            return scopeString
        }

        fun build(): GithubAuthenticator {
            val args = Bundle()
            args.putString("id", mClientId)
            args.putString("secret", mClientSecret)
            if (mHasScopes) {
                args.putString("scope", getScopeFromList(mScopeList))
            }
            args.putBoolean("debug", mIsDebug)

            val authFragment = GithubOAuthDialogFragment.newInstance(args)
            authFragment.mSuccessCallback = mSuccessCallback
            authFragment.mErrorCallback = mErrorCallback
            val authenticator = GithubAuthenticator(context)
            authenticator.authFragment = authFragment
            return authenticator
        }

    }
}
