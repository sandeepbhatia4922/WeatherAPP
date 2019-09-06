package com.dossiersl.rxkotlinmvvm.retrofit

import com.dossiersl.rxkotlinmvvm.model.CurrentWeatherResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current.json")
    fun currentWeather(
        @Query("key") key: String,
        @Query("q") q: String
    ): Observable<CurrentWeatherResponse>


}