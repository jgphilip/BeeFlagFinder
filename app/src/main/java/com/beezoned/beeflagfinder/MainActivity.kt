package com.beezoned.beeflagfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.beezoned.auth.ui.AuthGate
import com.beezoned.auth.ui.AuthStyle
import com.beezoned.beeflagfinder.presentation.components.GameColors
import com.beezoned.beeflagfinder.presentation.navigation.AppNavHost
import com.beezoned.beeflagfinder.ui.theme.BeeFlagFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as BeeFlagApp
        val authStyle = AuthStyle(
            background = Brush.verticalGradient(
                listOf(GameColors.PurpleDark, GameColors.PurpleMid, GameColors.PurpleLight),
            ),
            contentColor = Color.White,
            primaryButton = listOf(Color(0xFFFF9800)),
            logo = { Text("🏁", fontSize = 64.sp) },
            subtitle = "Sign up to start guessing flags",
        )
        setContent {
            BeeFlagFinderTheme {
                AuthGate(app.authModule, style = authStyle) { user, signOut ->
                    AppNavHost(
                        container = app.container,
                        userLabel = user.displayName ?: user.email.orEmpty(),
                        onSignOut = signOut,
                    )
                }
            }
        }
    }
}
