package com.app.core.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri

object ImplicitIntentHelper {

    fun openMaps(context: Context, latitude: Double, longitude: Double) {
        val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val webIntent =
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://maps.google.com/?q=$latitude,$longitude")
                    )
            context.startActivity(webIntent)
        }
    }
    
    fun shareSurvey(context: Context, title: String, description: String, latitude: Double, longitude: Double) {
        val shareText = """
            Survey Detail:
            Title: $title
            Description: $description
            Location: $latitude, $longitude
            Google Maps: https://maps.google.com/?q=$latitude,$longitude
        """.trimIndent()
        
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        val shareIntent = Intent.createChooser(sendIntent, "Share Survey via")
        context.startActivity(shareIntent)
    }

    fun shareToWhatsApp(context: Context, title: String, description: String, latitude: Double, longitude: Double) {
        val shareText = """
            *Survey Detail*
            *Title:* $title
            *Description:* $description
            *Location:* $latitude, $longitude
            *Maps:* https://maps.google.com/?q=$latitude,$longitude
        """.trimIndent()
        
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            setPackage("com.whatsapp")
        }
        
        try {
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            // Fallback to generic share if WhatsApp not installed
            shareSurvey(context, title, description, latitude, longitude)
        }
    }
}
