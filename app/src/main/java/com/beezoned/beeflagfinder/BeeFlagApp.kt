package com.beezoned.beeflagfinder

import android.app.Application
import com.beezoned.auth.AuthModule
import com.beezoned.beeflagfinder.data.AssetCountryDataSource
import com.beezoned.beeflagfinder.data.CountryRepositoryImpl
import com.beezoned.beeflagfinder.domain.repository.CountryRepository
import com.beezoned.beeflagfinder.domain.usecase.CheckAnswerUseCase
import com.beezoned.beeflagfinder.domain.usecase.GetCountriesUseCase
import com.beezoned.beeflagfinder.domain.usecase.GetNextCountryUseCase

/** Manual dependency container. */
class AppContainer(app: Application) {
    private val repository: CountryRepository = CountryRepositoryImpl(AssetCountryDataSource(app))
    val getCountries = GetCountriesUseCase(repository)
    val getNextCountry = GetNextCountryUseCase()
    val checkAnswer = CheckAnswerUseCase()
}

class BeeFlagApp : Application() {
    lateinit var container: AppContainer
        private set
    lateinit var authModule: AuthModule
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        authModule = AuthModule(this)
    }
}
