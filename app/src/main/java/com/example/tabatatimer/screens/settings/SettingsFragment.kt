package com.example.tabatatimer.screens.settings

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.example.tabatatimer.R
import com.example.tabatatimer.screens.MainActivity


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
        val button: Preference? = findPreference(getString(R.string.clean_data))
        button?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(getString(R.string.clean_confirmation))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    (requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirmation))
            alert.show()
            true
        }
    }
    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        when (key) {
            activity?.getString(R.string.dark_theme) -> {
                when (prefs?.getBoolean(activity?.getString(R.string.dark_theme), false)) {
                    true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                    else -> {}
                }
            }
            activity?.getString(R.string.language_pref) -> {
                activity?.recreate()
            }
            activity?.getString(R.string.font_scale) -> {
                activity?.recreate()
            }
        }
    }

}






