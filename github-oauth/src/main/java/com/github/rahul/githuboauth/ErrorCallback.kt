package com.github.rahul.githuboauth

import android.support.annotation.WorkerThread

interface ErrorCallback {

    @WorkerThread
    fun onError(error: Exception)
}