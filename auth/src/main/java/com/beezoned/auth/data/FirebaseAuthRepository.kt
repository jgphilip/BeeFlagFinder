package com.beezoned.auth.data

import com.beezoned.auth.domain.AuthError
import com.beezoned.auth.domain.AuthException
import com.beezoned.auth.domain.AuthProvider
import com.beezoned.auth.domain.AuthRepository
import com.beezoned.auth.domain.AuthState
import com.beezoned.auth.domain.AuthUser
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(private val auth: FirebaseAuth) : AuthRepository {

    override val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            trySend(fa.currentUser?.let { AuthState.SignedIn(it.toDomain()) } ?: AuthState.SignedOut)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthUser> =
        authenticate { auth.createUserWithEmailAndPassword(email.trim(), password).await().user }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> =
        authenticate { auth.signInWithEmailAndPassword(email.trim(), password).await().user }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> =
        signInWith(GoogleAuthProvider.getCredential(idToken, null))

    override suspend fun signInWithFacebook(accessToken: String): Result<AuthUser> =
        signInWith(FacebookAuthProvider.getCredential(accessToken))

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching { auth.sendPasswordResetEmail(email.trim()).await(); Unit }
            .recoverCatching { throw it.toAuthException() }

    override fun signOut() = auth.signOut()

    private suspend fun signInWith(credential: AuthCredential): Result<AuthUser> =
        authenticate { auth.signInWithCredential(credential).await().user }

    private suspend fun authenticate(block: suspend () -> FirebaseUser?): Result<AuthUser> =
        try {
            val user = block() ?: throw AuthException(AuthError.UNKNOWN)
            Result.success(user.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e.toAuthException())
        }

    private fun FirebaseUser.toDomain() = AuthUser(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        provider = providerData.map { it.providerId }.firstNotNullOfOrNull {
            when (it) {
                GoogleAuthProvider.PROVIDER_ID -> AuthProvider.GOOGLE
                FacebookAuthProvider.PROVIDER_ID -> AuthProvider.FACEBOOK
                EmailAuthProvider.PROVIDER_ID -> AuthProvider.EMAIL
                else -> null
            }
        },
    )

    private fun Throwable.toAuthException(): AuthException {
        if (this is AuthException) return this
        val error = when (this) {
            is FirebaseAuthWeakPasswordException -> AuthError.WEAK_PASSWORD
            is FirebaseAuthUserCollisionException ->
                if (errorCode == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") {
                    AuthError.ACCOUNT_EXISTS_WITH_OTHER_PROVIDER
                } else {
                    AuthError.EMAIL_IN_USE
                }
            is FirebaseAuthInvalidUserException -> AuthError.USER_NOT_FOUND
            is FirebaseAuthInvalidCredentialsException ->
                if (errorCode == "ERROR_INVALID_EMAIL") AuthError.INVALID_EMAIL else AuthError.INVALID_CREDENTIALS
            is FirebaseNetworkException -> AuthError.NETWORK
            is FirebaseAuthException -> AuthError.UNKNOWN
            else -> AuthError.UNKNOWN
        }
        return AuthException(error, this)
    }
}
