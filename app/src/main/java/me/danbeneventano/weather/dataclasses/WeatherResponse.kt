package me.danbeneventano.weather.dataclasses

data class WeatherResponse(
        val offset: Int? = null,
        val currently: DataPoint,
        val timezone: String,
        val latitude: Double,
        val daily: DataBlock? = null,
        val hourly: DataBlock? = null,
        val minutely: DataBlock? = null,
        val longitude: Double,
        val alerts: List<Alert>? = null
)
