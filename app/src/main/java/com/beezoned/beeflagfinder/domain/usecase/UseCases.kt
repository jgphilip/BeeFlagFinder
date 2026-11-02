package com.beezoned.beeflagfinder.domain.usecase

import com.beezoned.beeflagfinder.domain.model.Continent
import com.beezoned.beeflagfinder.domain.model.Country
import com.beezoned.beeflagfinder.domain.model.GameMode
import com.beezoned.beeflagfinder.domain.repository.CountryRepository
import java.text.Normalizer
import kotlin.random.Random

/** Returns the pool of countries for a game, sorted alphabetically by name. */
class GetCountriesUseCase(private val repository: CountryRepository) {
    suspend operator fun invoke(mode: GameMode, continent: Continent? = null): List<Country> {
        val all = repository.getCountries().sortedBy { it.name }
        return if (mode == GameMode.CONTINENT && continent != null) {
            all.filter { it.continent == continent }
        } else {
            all
        }
    }
}

/** Picks the next country: sequential for A-Z, random (never repeating the current one) otherwise. */
class GetNextCountryUseCase(private val random: Random = Random.Default) {
    operator fun invoke(pool: List<Country>, current: Country?, mode: GameMode): Country? {
        if (pool.isEmpty()) return null
        if (mode == GameMode.ALPHABETICAL) {
            val index = pool.indexOf(current)
            return pool[(index + 1) % pool.size]
        }
        val candidates = if (pool.size > 1) pool.filter { it != current } else pool
        return candidates[random.nextInt(candidates.size)]
    }
}

/** Case, accent, punctuation and surrounding-whitespace insensitive comparison. */
class CheckAnswerUseCase {
    operator fun invoke(country: Country, answer: String): Boolean {
        val expected = normalize(country.name)
        val given = normalize(answer)
        return given.isNotEmpty() && given == expected
    }

    private fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase()
            .replace("&", "and")
            .replace(Regex("[^a-z0-9]+"), " ")
            .trim()
}
