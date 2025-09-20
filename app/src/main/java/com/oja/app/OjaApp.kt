package com.oja.app

import android.app.Application
import android.content.Context

class OjaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
