package com.beezoned.beeflagfinder.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun GameTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        color = Color.White,
        fontSize = 34.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        textAlign = TextAlign.Center,
        style = TextStyle(shadow = Shadow(GameColors.PurpleDark, Offset(4f, 6f), 12f)),
    )
}
