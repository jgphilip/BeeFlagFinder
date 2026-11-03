package com.beezoned.auth.data

import androidx.activity.result.contract.ActivityResultContract
import com.beezoned.auth.domain.AuthError
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

internal class FacebookLoginHelper {
    private val callbackManager = CallbackManager.Factory.create()
    private val loginManager = LoginManager.getInstance()

    val permissions: Collection<String> = listOf("email", "public_profile")

    fun createContract(): ActivityResultContract<Collection<String>, CallbackManager.ActivityResultParameters> =
        loginManager.createLogInActivityResultContract(callbackManager, null)

    fun register(onToken: (String) -> Unit, onFailure: (AuthError) -> Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) = onToken(result.accessToken.token)
            override fun onCancel() = onFailure(AuthError.CANCELLED)
            override fun onError(error: FacebookException) = onFailure(AuthError.UNKNOWN)
        })
    }

    fun unregister() = loginManager.unregisterCallback(callbackManager)

    fun signOut() = loginManager.logOut()
}
