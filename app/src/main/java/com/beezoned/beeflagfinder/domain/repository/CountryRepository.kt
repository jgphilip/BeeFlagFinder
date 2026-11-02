package com.beezoned.beeflagfinder.domain.repository

import com.beezoned.beeflagfinder.domain.model.Country

interface CountryRepository {
    suspend fun getCountries(): List<Country>
}
