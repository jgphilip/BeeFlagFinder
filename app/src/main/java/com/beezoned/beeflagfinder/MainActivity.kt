package com.beezoned.beeflagfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.beezoned.beeflagfinder.presentation.navigation.AppNavHost
import com.beezoned.beeflagfinder.ui.theme.BeeFlagFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as BeeFlagApp).container
        setContent {
            BeeFlagFinderTheme {
                AppNavHost(container)
            }
        }
    }
}
