package com.alim.snaploader.Database

import android.content.Context

class SettingsData(private val context: Context) {

    private val DATA_NAME = "SETTINGS_DATA"

    var region: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(REGION, "IN")!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(REGION, value)
            editor.apply()
        }

    companion object {
        private const val REGION = "REGION"
    }

}