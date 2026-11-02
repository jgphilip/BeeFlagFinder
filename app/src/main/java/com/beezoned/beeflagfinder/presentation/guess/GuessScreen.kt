package com.beezoned.beeflagfinder.presentation.guess

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beezoned.beeflagfinder.presentation.components.GameBackground
import com.beezoned.beeflagfinder.presentation.components.GameButton
import com.beezoned.beeflagfinder.presentation.components.GameColors
import com.beezoned.beeflagfinder.presentation.components.GameTopBar

@Composable
fun GuessScreen(viewModel: GuessViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val country = state.country

    GameBackground(modifier) {
        Column(Modifier.fillMaxSize()) {
            GameTopBar(viewModel.title, onBack)
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
                country == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No flags available.", color = Color.White)
                }
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    StatsRow(state.score, state.streak)
                    FlagCard(country)
                    OutlinedTextField(
                        value = state.answer,
                        onValueChange = viewModel::onAnswerChange,
                        placeholder = { Text("Which country is this?") },
                        singleLine = true,
                        enabled = state.result != AnswerResult.CORRECT,
                        shape = RoundedCornerShape(20.dp),
                        textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White.copy(alpha = 0.8f),
                            focusedBorderColor = GameColors.Gold,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = GameColors.PurpleDark,
                            unfocusedTextColor = GameColors.PurpleDark,
                            disabledTextColor = GameColors.PurpleDark,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { viewModel.submit() }),
                        modifier = Modifier.fillMaxWidth().testTag("answer"),
                    )
                    GameButton(
                        title = "Submit",
                        colors = listOf(Color(0xFFFFB300), Color(0xFFFF6F00)),
                        onClick = viewModel::submit,
                        icon = "🚀",
                        showArrow = false,
                        enabled = state.answer.isNotBlank() && state.result != AnswerResult.CORRECT,
                    )
                    ResultBanner(state.result, country.name)
                    val solved = state.result == AnswerResult.CORRECT
                    GameButton(
                        title = if (solved) "Next flag" else "Skip",
                        colors = if (solved) listOf(Color(0xFF42A5F5), Color(0xFF7C4DFF)) else listOf(Color(0xFF7E57C2), Color(0xFF512DA8)),
                        onClick = viewModel::next,
                        icon = if (solved) "➡️" else "⏭️",
                        showArrow = false,
                    )
                }
            }
        }
    }
}
