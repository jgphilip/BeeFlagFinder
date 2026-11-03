package com.beezoned.auth.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.beezoned.auth.domain.AuthError
import com.beezoned.auth.domain.AuthException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

internal class GoogleSignInHelper(private val webClientId: String) {

    /** Shows the Google account sheet and returns a Google ID token. [context] must be an Activity. */
    suspend fun getIdToken(context: Context): String {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build())
            .build()
        try {
            val credential = CredentialManager.create(context).getCredential(context, request).credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return GoogleIdTokenCredential.createFrom(credential.data).idToken
            }
            throw AuthException(AuthError.UNKNOWN)
        } catch (e: GetCredentialCancellationException) {
            throw AuthException(AuthError.CANCELLED, e)
        } catch (e: GetCredentialException) {
            throw AuthException(AuthError.UNKNOWN, e)
        }
    }
}
