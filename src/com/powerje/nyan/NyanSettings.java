package com.powerje.nyan;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class NyanSettings extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        getPreferenceManager().setSharedPreferencesName(
                NyanPaper.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.nyan_settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                this);

        int c = Color.argb(200, 50, 50, 50);
        getListView().setBackgroundColor(c);
        getListView().setCacheColorHint(c);
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    }

}
