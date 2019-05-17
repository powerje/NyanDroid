package com.powerje.nyan

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceActivity

class NyanSettings : PreferenceActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        preferenceManager.sharedPreferencesName = NyanPaper.SHARED_PREFS_NAME
        addPreferencesFromResource(R.xml.nyan_settings)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(
                this)

        val c = Color.argb(200, 50, 50, 50)
        listView.setBackgroundColor(c)
        listView.cacheColorHint = c
    }

    override fun onDestroy() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(
                this)
        super.onDestroy()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                           key: String) {
    }

}
