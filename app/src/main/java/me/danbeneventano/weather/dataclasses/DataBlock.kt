package me.danbeneventano.weather.dataclasses

data class DataBlock(val summary: String? = null,
                     val data: List<DataPoint?>,
                     val icon: String? = null)