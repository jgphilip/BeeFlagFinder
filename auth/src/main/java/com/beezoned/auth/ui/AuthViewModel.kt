package com.beezoned.auth.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beezoned.auth.domain.AuthError
import com.beezoned.auth.domain.AuthException
import com.beezoned.auth.domain.AuthRepository
import com.beezoned.auth.domain.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { SIGN_UP, SIGN_IN }

data class AuthFormState(
    val mode: AuthMode = AuthMode.SIGN_UP,
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: AuthError? = null,
    val resetSent: Boolean = false,
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val authState: StateFlow<AuthState> = repository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthState.Loading)

    private val _form = MutableStateFlow(AuthFormState())
    val form: StateFlow<AuthFormState> = _form.asStateFlow()

    fun onEmailChange(value: String) = _form.update { it.copy(email = value, error = null, resetSent = false) }

    fun onPasswordChange(value: String) = _form.update { it.copy(password = value, error = null) }

    fun toggleMode() = _form.update {
        it.copy(
            mode = if (it.mode == AuthMode.SIGN_UP) AuthMode.SIGN_IN else AuthMode.SIGN_UP,
            error = null,
            resetSent = false,
        )
    }

    fun submitEmail() {
        val f = _form.value
        if (f.loading) return
        when {
            !Patterns.EMAIL_ADDRESS.matcher(f.email.trim()).matches() -> return fail(AuthError.INVALID_EMAIL)
            f.password.length < MIN_PASSWORD -> return fail(AuthError.WEAK_PASSWORD)
        }
        perform {
            if (f.mode == AuthMode.SIGN_UP) repository.signUpWithEmail(f.email, f.password).map { }
            else repository.signInWithEmail(f.email, f.password).map { }
        }
    }

    fun signInWithGoogle(idToken: String) = perform { repository.signInWithGoogle(idToken).map { } }

    fun signInWithFacebook(accessToken: String) = perform { repository.signInWithFacebook(accessToken).map { } }

    fun onSocialStarted() = _form.update { it.copy(loading = true, error = null, resetSent = false) }

    /** Called when a social provider failed before producing a token. Cancellations are silent. */
    fun onSocialFailure(error: AuthError) = _form.update {
        it.copy(loading = false, error = error.takeIf { e -> e != AuthError.CANCELLED })
    }

    fun resetPassword() {
        val email = _form.value.email
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return fail(AuthError.INVALID_EMAIL)
        _form.update { it.copy(loading = true, error = null, resetSent = false) }
        viewModelScope.launch {
            repository.sendPasswordReset(email)
                .onSuccess { _form.update { it.copy(loading = false, resetSent = true) } }
                .onFailure { e -> _form.update { it.copy(loading = false, error = e.asError()) } }
        }
    }

    fun signOut() {
        repository.signOut()
        _form.value = AuthFormState()
    }

    private fun perform(block: suspend () -> Result<Unit>) {
        _form.update { it.copy(loading = true, error = null, resetSent = false) }
        viewModelScope.launch {
            block()
                .onSuccess { _form.update { it.copy(loading = false, password = "") } }
                .onFailure { e -> _form.update { it.copy(loading = false, error = e.asError()) } }
        }
    }

    private fun fail(error: AuthError) = _form.update { it.copy(error = error) }

    private fun Throwable.asError(): AuthError =
        (this as? AuthException)?.error?.takeIf { it != AuthError.CANCELLED } ?: AuthError.UNKNOWN

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(repository) as T
    }

    private companion object {
        const val MIN_PASSWORD = 6
    }
}
