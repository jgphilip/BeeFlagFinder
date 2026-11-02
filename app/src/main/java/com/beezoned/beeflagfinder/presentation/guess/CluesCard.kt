package com.beezoned.beeflagfinder.presentation.guess

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun CluesCard(clues: List<String>, outOfClues: Boolean, modifier: Modifier = Modifier) {
    if (clues.isEmpty() && !outOfClues) return
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.3f))
            .border(2.dp, GameColors.Gold.copy(alpha = 0.7f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        clues.forEachIndexed { index, clue ->
            Text(
                "💡 Clue ${index + 1}: $clue",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = if (index == clues.lastIndex && !outOfClues) FontWeight.Bold else FontWeight.Normal,
            )
        }
        if (outOfClues) {
            Text(
                "🚫 You have run out of clues, now you are on your own!",
                color = GameColors.Gold,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}
