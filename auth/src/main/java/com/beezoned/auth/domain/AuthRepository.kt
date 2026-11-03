package com.beezoned.auth.domain

import kotlinx.coroutines.flow.Flow

/** Provider-agnostic authentication contract. Social providers hand in their token. */
interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun signUpWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun signInWithFacebook(accessToken: String): Result<AuthUser>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    fun signOut()
}
