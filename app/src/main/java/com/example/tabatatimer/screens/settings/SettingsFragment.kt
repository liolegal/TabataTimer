package com.example.tabatatimer.screens.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.tabatatimer.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}