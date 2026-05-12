package com.app.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.ActivityNotFoundException
import com.app.features.dashboard.model.Survey

object IntentNavigation {

    /** Share survey information via WhatsApp */
    fun shareSurveyViaWhatsApp(context: Context, survey: Survey) {
        val shareText =
            """
            📍 Survey Report
            📍 Lokasi: ${survey.title}
            📝 Deskripsi: ${survey.description}
            🗺️ Koordinat: ${survey.latitude}, ${survey.longitude}
            📊 Status: ${survey.status.name}
            
            Lihat lokasi di Google Maps:
            https://maps.google.com/?q=${survey.latitude},${survey.longitude}
        """.trimIndent()

        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                setPackage("com.whatsapp")
            }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general share if WhatsApp not installed
            shareSurveyGeneral(context, survey, shareText)
        }
    }

    /** Open survey location in Google Maps */
    fun openSurveyInGoogleMaps(context: Context, survey: Survey) {
        val geoUri =
                Uri.parse(
                        "geo:${survey.latitude},${survey.longitude}?q=${survey.latitude},${survey.longitude}(${Uri.encode(survey.title)})"
                )
        val intent =
                Intent(Intent.ACTION_VIEW, geoUri).apply { setPackage("com.google.android.apps.maps") }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${survey.latitude},${survey.longitude}")))
        }
    }

    /** General share functionality for fallback */
    private fun shareSurveyGeneral(context: Context, survey: Survey, shareText: String) {
        val fallbackIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, "Survey Report: ${survey.title}")
            }
        context.startActivity(Intent.createChooser(fallbackIntent, "Bagikan Survey"))
    }
}
