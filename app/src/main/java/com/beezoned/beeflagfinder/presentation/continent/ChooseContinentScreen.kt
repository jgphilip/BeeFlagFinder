package com.beezoned.beeflagfinder.presentation.continent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.beezoned.beeflagfinder.domain.model.Continent
import com.beezoned.beeflagfinder.presentation.components.GameBackground
import com.beezoned.beeflagfinder.presentation.components.GameButton
import com.beezoned.beeflagfinder.presentation.components.GameTopBar

@Composable
fun ChooseContinentScreen(
    onSelect: (Continent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GameBackground(modifier) {
        Column(Modifier.fillMaxSize()) {
            GameTopBar("Choose a continent", onBack)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(Continent.entries) { continent ->
                    val style = continent.style()
                    GameButton(
                        title = continent.displayName,
                        colors = style.colors,
                        onClick = { onSelect(continent) },
                        icon = style.icon,
                    )
                }
            }
        }
    }
}

private class ContinentStyle(val icon: String, val colors: List<Color>)

private fun Continent.style(): ContinentStyle = when (this) {
    Continent.AFRICA -> ContinentStyle("🦁", listOf(Color(0xFFFFB300), Color(0xFFFF6F00)))
    Continent.ASIA -> ContinentStyle("🐼", listOf(Color(0xFFFF5252), Color(0xFFC2185B)))
    Continent.EUROPE -> ContinentStyle("🏰", listOf(Color(0xFF42A5F5), Color(0xFF1565C0)))
    Continent.NORTH_AMERICA -> ContinentStyle("🦅", listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)))
    Continent.SOUTH_AMERICA -> ContinentStyle("🌴", listOf(Color(0xFF00E5A0), Color(0xFF00897B)))
    Continent.OCEANIA -> ContinentStyle("🦘", listOf(Color(0xFF26C6DA), Color(0xFF7C4DFF)))
}
