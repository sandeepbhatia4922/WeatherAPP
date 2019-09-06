package com.dossiersl.rxkotlinmvvm.model


data class CurrentWeatherResponse(
    val location: Location,
    val current: Current
)