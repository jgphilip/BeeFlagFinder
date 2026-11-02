package com.beezoned.beeflagfinder.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameButton(
    title: String,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    subtitle: String? = null,
    showArrow: Boolean = true,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "press")
    val shape = RoundedCornerShape(28.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(if (enabled) 1f else 0.5f)
            .shadow(12.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(Brush.horizontalGradient(colors))
            .border(3.dp, Color.White.copy(alpha = 0.6f), shape)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) { Text(icon, fontSize = 28.sp) }
            Spacer(Modifier.width(16.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title.uppercase(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            if (subtitle != null) {
                Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            }
        }
        if (showArrow) Text("▶", color = Color.White, fontSize = 22.sp)
    }
}
