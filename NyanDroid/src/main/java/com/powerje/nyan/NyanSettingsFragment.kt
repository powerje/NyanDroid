package com.powerje.nyan

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class NyanSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = getString(R.string.shared_preferences_name)
        setPreferencesFromResource(R.xml.nyan_settings, rootKey)
    }

}