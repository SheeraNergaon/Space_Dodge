package com.example.myapplicationhw1

import android.app.Application
import com.example.myapplicationhw1.Utilities.SignalManager


class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}