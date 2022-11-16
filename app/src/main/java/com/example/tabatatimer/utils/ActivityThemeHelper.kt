package com.example.tabatatimer.utils

import android.content.Context
import androidx.preference.PreferenceManager

//class ActivityThemeHelper {
//    companion object {
//        fun setActivityTheme(context: Context) {
//            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//            val nightModeEnabled = preferences.getBoolean(Constants.NIGHT_MODE_PREFERENCE, false)
//            val fontSize = preferences.getString(Constants.FONT_SIZE_PREFERENCE, "1")
//
//            if (nightModeEnabled) {
//                when (fontSize) {
//                    "0" -> context.setTheme(R.style.DarkTheme_SmallFont)
//                    "1" -> context.setTheme(R.style.DarkTheme_MediumFont)
//                    else -> context.setTheme(R.style.DarkTheme_LargeFont)
//                }
//            }
//            else {
//                when (fontSize) {
//                    "0" -> context.setTheme(R.style.LightTheme_SmallFont)
//                    "1" -> context.setTheme(R.style.LightTheme_MediumFont)
//                    else -> context.setTheme(R.style.LightTheme_LargeFont)
//                }
//            }
//        }
//    }
//}