package com.wolf.travelscout

import android.app.Application
import android.content.Context
import com.wolf.travelscout.util.SharedPreferencesUtil

class MainApp: Application(){

    companion object {
        lateinit var appContext: Context
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        application = Application()
        SharedPreferencesUtil.init(applicationContext)
    }
}