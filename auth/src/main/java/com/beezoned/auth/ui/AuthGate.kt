package com.beezoned.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beezoned.auth.AuthModule
import com.beezoned.auth.R
import com.beezoned.auth.domain.AuthState
import com.beezoned.auth.domain.AuthUser
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator

/**
 * Shows the sign-up / sign-in screen until a user is signed in, then renders [content].
 * [content] receives the signed-in user and a sign-out callback.
 */
@Composable
fun AuthGate(
    module: AuthModule,
    modifier: Modifier = Modifier,
    style: AuthStyle = AuthStyle(),
    content: @Composable (user: AuthUser, signOut: () -> Unit) -> Unit,
) {
    if (!module.isFirebaseConfigured) {
        NotConfiguredScreen(modifier)
        return
    }

    val viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(module.repository))
    val state by viewModel.authState.collectAsState()

    when (val s = state) {
        AuthState.Loading -> Box(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        AuthState.SignedOut -> AuthScreen(viewModel, module.config, style, modifier)

        is AuthState.SignedIn -> content(s.user) {
            viewModel.signOut()
            runCatching { com.facebook.login.LoginManager.getInstance().logOut() }
        }
    }
}

@Composable
private fun NotConfiguredScreen(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.auth_not_configured_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(stringResource(R.string.auth_not_configured_body), textAlign = TextAlign.Center)
    }
}
