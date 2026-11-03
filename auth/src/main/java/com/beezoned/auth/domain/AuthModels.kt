package com.beezoned.auth.domain

enum class AuthProvider { EMAIL, GOOGLE, FACEBOOK }

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val provider: AuthProvider?,
)

sealed interface AuthState {
    data object Loading : AuthState
    data object SignedOut : AuthState
    data class SignedIn(val user: AuthUser) : AuthState
}

enum class AuthError {
    INVALID_EMAIL,
    WEAK_PASSWORD,
    EMAIL_IN_USE,
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    ACCOUNT_EXISTS_WITH_OTHER_PROVIDER,
    NETWORK,
    NOT_CONFIGURED,
    CANCELLED,
    UNKNOWN,
}

class AuthException(val error: AuthError, cause: Throwable? = null) : Exception(error.name, cause)
