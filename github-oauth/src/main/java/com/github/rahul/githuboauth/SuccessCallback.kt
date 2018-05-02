package com.github.rahul.githuboauth

import android.support.annotation.WorkerThread

interface SuccessCallback {

    @WorkerThread
    fun onSuccess(result: String)
}