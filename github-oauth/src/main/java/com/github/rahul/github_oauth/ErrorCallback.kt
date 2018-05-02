package com.github.rahul.github_oauth

import android.support.annotation.WorkerThread

interface ErrorCallback {

    @WorkerThread
    abstract fun onError(error: Exception)
}