package com.beezoned.beeflagfinder.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezoned.beeflagfinder.presentation.components.GameBackground
import com.beezoned.beeflagfinder.presentation.components.GameButton
import com.beezoned.beeflagfinder.presentation.components.GameColors
import com.beezoned.beeflagfinder.presentation.components.GameTitle

@Composable
fun HomeScreen(
    onRandom: () -> Unit,
    onContinent: () -> Unit,
    onAlphabetical: () -> Unit,
    userLabel: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GameBackground(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                userLabel,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onSignOut) { Text("Sign out", color = GameColors.Gold, fontWeight = FontWeight.Bold) }
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        ) {
            Text("🏁", fontSize = 72.sp)
            GameTitle("GUESS THE FLAG")
            Text("Pick your challenge", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
            GameButton("Random", listOf(Color(0xFFFF9800), Color(0xFFFF5722)), onRandom, icon = "🎲", subtitle = "Any flag, anywhere")
            GameButton("Continent", listOf(Color(0xFF00E5A0), Color(0xFF00B0A0)), onContinent, icon = "🌍", subtitle = "Master one region")
            GameButton("A-Z", listOf(Color(0xFF42A5F5), Color(0xFF7C4DFF)), onAlphabetical, icon = "🔤", subtitle = "Climb the alphabet")
        }
    }
}
