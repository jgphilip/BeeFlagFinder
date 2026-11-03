package com.beezoned.auth

import android.content.Context
import com.beezoned.auth.data.FirebaseAuthRepository
import com.beezoned.auth.domain.AuthRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

/** Entry point of the library. Create once (e.g. in Application) and pass to [com.beezoned.auth.ui.AuthGate]. */
class AuthModule(context: Context, val config: AuthConfig = AuthConfig.fromResources(context)) {

    /** False until the host app has a google-services.json. */
    val isFirebaseConfigured: Boolean = FirebaseApp.getApps(context.applicationContext).isNotEmpty()

    val repository: AuthRepository by lazy { FirebaseAuthRepository(FirebaseAuth.getInstance()) }
}
