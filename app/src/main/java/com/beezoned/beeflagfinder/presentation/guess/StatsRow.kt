package com.beezoned.beeflagfinder.presentation.guess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezoned.beeflagfinder.presentation.components.GameColors

@Composable
fun StatsRow(score: Int, streak: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatChip("⭐ Score $score", Modifier.weight(1f))
        StatChip("🔥 Streak $streak", Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(text: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier.clip(shape).background(Color.Black.copy(alpha = 0.25f)).padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(text, color = GameColors.Gold, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}
