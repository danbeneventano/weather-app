package me.danbeneventano.weather

import android.location.Address

data class LocationInfo(val latitude: Double,
                        val longitude: Double,
                        val address: Address?)