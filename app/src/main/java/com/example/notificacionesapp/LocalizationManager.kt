package com.example.notificacionesapp

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LocalizationManager {

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        // Guardar preferencia
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putString("language", languageCode)
            .apply()
    }

    fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language", "es") ?: "es"
    }
}