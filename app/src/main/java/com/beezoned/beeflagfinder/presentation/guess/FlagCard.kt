package com.beezoned.beeflagfinder.presentation.guess

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.beezoned.beeflagfinder.domain.model.Country
import com.beezoned.beeflagfinder.presentation.components.GameColors

@Composable
fun FlagCard(country: Country, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap = remember(country.code) {
        context.assets.open("flags/${country.code}.png").use { BitmapFactory.decodeStream(it) }
    }
    val outer = RoundedCornerShape(24.dp)
    val inner = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(16.dp, outer, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(outer)
            .background(Brush.verticalGradient(listOf(Color(0xFF3A1580), GameColors.PurpleDark)))
            .border(4.dp, GameColors.Gold, outer)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Flag to guess",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(inner)
                .border(2.dp, Color.White.copy(alpha = 0.35f), inner),
        )
    }
}
