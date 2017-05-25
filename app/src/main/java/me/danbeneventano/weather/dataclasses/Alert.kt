package me.danbeneventano.weather.dataclasses

data class Alert(val title: String,
                 val time: Long,
                 val regions: List<String>,
                 val severity: String,
                 val expires: Long?,
                 val description: String,
                 val uri: String)