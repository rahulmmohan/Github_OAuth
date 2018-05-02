package com.github.rahul.github_oauth

import android.support.annotation.WorkerThread

interface SuccessCallback {

    @WorkerThread
    abstract fun onSuccess(result: String)
}