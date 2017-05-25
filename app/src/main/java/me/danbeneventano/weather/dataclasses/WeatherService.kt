package me.danbeneventano.weather.dataclasses

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface WeatherService {
    @GET("{request}")
    fun getWeather(@Path("request") params: String, @QueryMap query: Map<String, String?> = emptyMap()): Observable<WeatherResponse>
}