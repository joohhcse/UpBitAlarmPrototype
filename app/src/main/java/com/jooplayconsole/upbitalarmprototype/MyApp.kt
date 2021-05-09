package com.jooplayconsole.upbitalarmprototype

import android.app.Application

class MyApp : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }

}