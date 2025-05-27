package com.example.spacedodge

import android.app.Application
import com.example.spacedodge.Utilities.SignalManager


class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}