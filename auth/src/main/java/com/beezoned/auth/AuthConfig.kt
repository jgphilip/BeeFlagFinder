package com.beezoned.auth

import android.content.Context

/**
 * Which sign-in methods the host app offers.
 * [googleWebClientId] is the Web OAuth client ID from Firebase (generated as `default_web_client_id`).
 */
data class AuthConfig(
    val googleWebClientId: String? = null,
    val facebookEnabled: Boolean = false,
    val emailEnabled: Boolean = true,
) {
    companion object {
        /** Reads the Google web client ID and Facebook app ID from the host app's resources. */
        fun fromResources(context: Context): AuthConfig {
            val res = context.resources
            fun string(name: String): String? =
                res.getIdentifier(name, "string", context.packageName)
                    .takeIf { it != 0 }
                    ?.let { res.getString(it) }
                    ?.takeIf { it.isNotBlank() }

            val facebookId = string("facebook_app_id")
            return AuthConfig(
                googleWebClientId = string("default_web_client_id"),
                facebookEnabled = facebookId != null && facebookId != "0",
            )
        }
    }
}
