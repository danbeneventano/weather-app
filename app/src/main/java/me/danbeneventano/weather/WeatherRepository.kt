package me.danbeneventano.weather

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.danbeneventano.weather.dataclasses.WeatherRequest
import me.danbeneventano.weather.dataclasses.WeatherResponse
import me.danbeneventano.weather.dataclasses.WeatherService

class WeatherRepository(val weatherService: WeatherService) {
    fun getWeather(params: WeatherRequest, query: Map<String, String?> = emptyMap()): LiveData<WeatherResponse> {
        val data = MutableLiveData<WeatherResponse>()
        weatherService.getWeather(params.toString(), query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data.value = it }
        return data
    }
}