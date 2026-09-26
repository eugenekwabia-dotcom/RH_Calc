package com.ekwabia.rhcalc

import android.app.Application
import com.ekwabia.rhcalc.theme.ThemePreferences

class RhCalcApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemePreferences.applySaved(this)
    }
}
