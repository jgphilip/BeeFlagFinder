package com.beezoned.beeflagfinder.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beezoned.beeflagfinder.AppContainer
import com.beezoned.beeflagfinder.domain.model.Continent
import com.beezoned.beeflagfinder.domain.model.GameMode
import com.beezoned.beeflagfinder.presentation.continent.ChooseContinentScreen
import com.beezoned.beeflagfinder.presentation.guess.GuessScreen
import com.beezoned.beeflagfinder.presentation.guess.GuessViewModel
import com.beezoned.beeflagfinder.presentation.home.HomeScreen

private const val HOME = "home"
private const val CONTINENTS = "continents"
private const val GUESS = "guess/{mode}?continent={continent}"

@Composable
fun AppNavHost(container: AppContainer, userLabel: String, onSignOut: () -> Unit) {
    val nav = rememberNavController()

    NavHost(nav, startDestination = HOME) {
        composable(HOME) {
            HomeScreen(
                onRandom = { nav.navigate("guess/${GameMode.RANDOM.name}") },
                onContinent = { nav.navigate(CONTINENTS) },
                onAlphabetical = { nav.navigate("guess/${GameMode.ALPHABETICAL.name}") },
                userLabel = userLabel,
                onSignOut = onSignOut,
            )
        }
        composable(CONTINENTS) {
            ChooseContinentScreen(
                onSelect = { nav.navigate("guess/${GameMode.CONTINENT.name}?continent=${it.name}") },
                onBack = { nav.popBackStack() },
            )
        }
        composable(
            GUESS,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("continent") { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { entry ->
            val mode = GameMode.valueOf(entry.arguments!!.getString("mode")!!)
            val continent = entry.arguments?.getString("continent")?.let { Continent.valueOf(it) }
            val vm: GuessViewModel = viewModel(
                factory = GuessViewModel.Factory(
                    mode, continent, container.getCountries, container.getNextCountry, container.checkAnswer,
                ),
            )
            GuessScreen(vm, onBack = { nav.popBackStack() })
        }
    }
}
