package com.beezoned.beeflagfinder.presentation.guess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beezoned.beeflagfinder.domain.model.Continent
import com.beezoned.beeflagfinder.domain.model.Country
import com.beezoned.beeflagfinder.domain.model.GameMode
import com.beezoned.beeflagfinder.domain.usecase.CheckAnswerUseCase
import com.beezoned.beeflagfinder.domain.usecase.GetCountriesUseCase
import com.beezoned.beeflagfinder.domain.usecase.GetNextCountryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AnswerResult { NONE, CORRECT, WRONG }

data class GuessUiState(
    val loading: Boolean = true,
    val country: Country? = null,
    val answer: String = "",
    val result: AnswerResult = AnswerResult.NONE,
    val score: Int = 0,
    val streak: Int = 0,
)

class GuessViewModel(
    private val mode: GameMode,
    private val continent: Continent?,
    private val getCountries: GetCountriesUseCase,
    private val getNextCountry: GetNextCountryUseCase,
    private val checkAnswer: CheckAnswerUseCase,
) : ViewModel() {

    val title: String = when (mode) {
        GameMode.RANDOM -> "Random"
        GameMode.CONTINENT -> continent?.displayName ?: "Continent"
        GameMode.ALPHABETICAL -> "A-Z"
    }

    private val _state = MutableStateFlow(GuessUiState())
    val state: StateFlow<GuessUiState> = _state.asStateFlow()

    private var pool: List<Country> = emptyList()

    init {
        viewModelScope.launch {
            pool = getCountries(mode, continent)
            _state.update { it.copy(loading = false, country = getNextCountry(pool, null, mode)) }
        }
    }

    fun onAnswerChange(text: String) {
        _state.update { it.copy(answer = text, result = AnswerResult.NONE) }
    }

    fun submit() {
        val current = _state.value
        val country = current.country ?: return
        if (current.answer.isBlank() || current.result == AnswerResult.CORRECT) return
        if (checkAnswer(country, current.answer)) {
            _state.update {
                it.copy(result = AnswerResult.CORRECT, score = it.score + 1, streak = it.streak + 1)
            }
        } else {
            _state.update { it.copy(result = AnswerResult.WRONG, streak = 0) }
        }
    }

    fun next() {
        _state.update {
            it.copy(
                country = getNextCountry(pool, it.country, mode),
                answer = "",
                streak = if (it.result == AnswerResult.CORRECT) it.streak else 0,
                result = AnswerResult.NONE,
            )
        }
    }

    class Factory(
        private val mode: GameMode,
        private val continent: Continent?,
        private val getCountries: GetCountriesUseCase,
        private val getNextCountry: GetNextCountryUseCase,
        private val checkAnswer: CheckAnswerUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GuessViewModel(mode, continent, getCountries, getNextCountry, checkAnswer) as T
    }
}
