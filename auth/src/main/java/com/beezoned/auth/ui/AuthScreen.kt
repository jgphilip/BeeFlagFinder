package com.beezoned.auth.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beezoned.auth.AuthConfig
import com.beezoned.auth.R
import com.beezoned.auth.data.FacebookLoginHelper
import com.beezoned.auth.data.GoogleSignInHelper
import com.beezoned.auth.domain.AuthError
import com.beezoned.auth.domain.AuthException
import kotlinx.coroutines.launch

private val FacebookBlue = Color(0xFF1877F2)

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    config: AuthConfig,
    style: AuthStyle,
    modifier: Modifier = Modifier,
) {
    val form by viewModel.form.collectAsState()
    val signUp = form.mode == AuthMode.SIGN_UP
    val titleColor = style.contentColor ?: MaterialTheme.colorScheme.onBackground
    val backgroundModifier = if (style.background != null) {
        Modifier.background(style.background)
    } else {
        Modifier.background(MaterialTheme.colorScheme.background)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(backgroundModifier)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        style.logo?.invoke()
        Text(
            stringResource(if (signUp) R.string.auth_title_sign_up else R.string.auth_title_sign_in),
            color = titleColor,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
        Text(
            style.subtitle ?: stringResource(R.string.auth_subtitle),
            color = titleColor.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (config.googleWebClientId != null) GoogleButton(viewModel, config.googleWebClientId, form.loading)
                if (config.facebookEnabled) FacebookButton(viewModel, form.loading)
                if (config.emailEnabled) {
                    if (config.googleWebClientId != null || config.facebookEnabled) {
                        OrDivider()
                    }
                    EmailForm(viewModel, form, style)
                }
            }
        }
    }
}

@Composable
private fun GoogleButton(viewModel: AuthViewModel, webClientId: String, loading: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val helper = remember(webClientId) { GoogleSignInHelper(webClientId) }
    OutlinedButton(
        onClick = {
            viewModel.onSocialStarted()
            scope.launch {
                try {
                    viewModel.signInWithGoogle(helper.getIdToken(context))
                } catch (e: AuthException) {
                    viewModel.onSocialFailure(e.error)
                }
            }
        },
        enabled = !loading,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.auth_continue_google), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FacebookButton(viewModel: AuthViewModel, loading: Boolean) {
    val helper = remember { FacebookLoginHelper() }
    val launcher = rememberLauncherForActivityResult(helper.createContract()) {}
    DisposableEffect(helper) {
        helper.register(
            onToken = viewModel::signInWithFacebook,
            onFailure = viewModel::onSocialFailure,
        )
        onDispose { helper.unregister() }
    }
    Button(
        onClick = {
            viewModel.onSocialStarted()
            launcher.launch(helper.permissions)
        },
        enabled = !loading,
        colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue, contentColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.auth_continue_facebook), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OrDivider() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider()
        Text(
            stringResource(R.string.auth_or_email),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun EmailForm(viewModel: AuthViewModel, form: AuthFormState, style: AuthStyle) {
    val signUp = form.mode == AuthMode.SIGN_UP
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = form.email,
        onValueChange = viewModel::onEmailChange,
        label = { Text(stringResource(R.string.auth_email)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = form.password,
        onValueChange = viewModel::onPasswordChange,
        label = { Text(stringResource(R.string.auth_password)) },
        singleLine = true,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            TextButton(onClick = { showPassword = !showPassword }) {
                Text(stringResource(if (showPassword) R.string.auth_hide_password else R.string.auth_show_password))
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )

    form.error?.let {
        Text(stringResource(it.messageRes()), color = MaterialTheme.colorScheme.error)
    }
    if (form.resetSent) {
        Text(stringResource(R.string.auth_reset_sent), color = MaterialTheme.colorScheme.primary)
    }

    val primary = style.primaryButton?.firstOrNull()
    Button(
        onClick = viewModel::submitEmail,
        enabled = !form.loading,
        colors = if (primary != null) ButtonDefaults.buttonColors(containerColor = primary) else ButtonDefaults.buttonColors(),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (form.loading) {
            CircularProgressIndicator(Modifier.padding(2.dp), strokeWidth = 2.dp, color = Color.White)
        } else {
            Text(stringResource(if (signUp) R.string.auth_sign_up else R.string.auth_sign_in), fontWeight = FontWeight.Bold)
        }
    }
    if (!signUp) {
        TextButton(onClick = viewModel::resetPassword, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.auth_forgot_password))
        }
    }
    TextButton(onClick = viewModel::toggleMode, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(if (signUp) R.string.auth_have_account else R.string.auth_no_account))
    }
}

private fun AuthError.messageRes(): Int = when (this) {
    AuthError.INVALID_EMAIL -> R.string.auth_error_invalid_email
    AuthError.WEAK_PASSWORD -> R.string.auth_error_weak_password
    AuthError.EMAIL_IN_USE -> R.string.auth_error_email_in_use
    AuthError.INVALID_CREDENTIALS -> R.string.auth_error_invalid_credentials
    AuthError.USER_NOT_FOUND -> R.string.auth_error_user_not_found
    AuthError.ACCOUNT_EXISTS_WITH_OTHER_PROVIDER -> R.string.auth_error_other_provider
    AuthError.NETWORK -> R.string.auth_error_network
    AuthError.NOT_CONFIGURED -> R.string.auth_error_not_configured
    AuthError.CANCELLED, AuthError.UNKNOWN -> R.string.auth_error_unknown
}
