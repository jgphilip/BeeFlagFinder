package com.beezoned.beeflagfinder.data

import android.content.Context
import com.beezoned.beeflagfinder.domain.model.Continent
import com.beezoned.beeflagfinder.domain.model.Country
import com.beezoned.beeflagfinder.domain.repository.CountryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class AssetCountryDataSource(private val context: Context) {
    fun load(): List<Country> {
        val text = context.assets.open(COUNTRIES_FILE).bufferedReader().use { it.readText() }
        val array = JSONArray(text)
        return (0 until array.length()).mapNotNull { i ->
            val o = array.getJSONObject(i)
            val continent = Continent.fromDisplayName(o.getString("continent")) ?: return@mapNotNull null
            Country(o.getString("code"), o.getString("name"), continent)
        }
    }

    private companion object {
        const val COUNTRIES_FILE = "countries.json"
    }
}

class CountryRepositoryImpl(private val dataSource: AssetCountryDataSource) : CountryRepository {
    private var cache: List<Country>? = null

    override suspend fun getCountries(): List<Country> =
        cache ?: withContext(Dispatchers.IO) { dataSource.load() }.also { cache = it }
}
