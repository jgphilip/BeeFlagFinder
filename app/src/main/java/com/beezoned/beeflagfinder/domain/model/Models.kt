package com.beezoned.beeflagfinder.domain.model

enum class Continent(val displayName: String) {
    AFRICA("Africa"),
    ASIA("Asia"),
    EUROPE("Europe"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America"),
    OCEANIA("Oceania");

    companion object {
        fun fromDisplayName(name: String): Continent? = entries.firstOrNull { it.displayName == name }
    }
}

enum class GameMode { RANDOM, CONTINENT, ALPHABETICAL }

data class Country(
    val code: String,
    val name: String,
    val continent: Continent,
    val clues: List<String> = emptyList(),
)
