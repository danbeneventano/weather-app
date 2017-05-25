package me.danbeneventano.weather

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import me.danbeneventano.weather.dataclasses.WeatherRequest
import me.danbeneventano.weather.dataclasses.WeatherResponse


class WeatherViewModel : ViewModel() {
    private val injector = KodeinInjector()

    private val application: Application by injector.instance()
    private val weatherRepository: WeatherRepository by injector.instance()
    private val locationManager: LocationManager by injector.instance()
    private val geocoder: Geocoder by injector.instance()

    lateinit var weather: LiveData<WeatherResponse>
    private lateinit var locationInfo: LocationInfo
    var location: LiveData<LocationInfo> = MutableLiveData()

    fun inject(kodein: Kodein) {
        injector.inject(kodein)
        val loc: Location? = getLastKnownLocation()
        val lat: Double
        val long: Double
        val address: Address

        if (loc != null) {
            lat = loc.latitude
            long = loc.longitude
            address = geocoder.getFromLocation(lat, long, 1).first()
        } else {
            address = geocoder.getFromLocationName("94039", 1).first()
            lat = address.latitude
            long = address.longitude
        }

        locationInfo = LocationInfo(lat, long, address)
        (location as MutableLiveData).value = locationInfo
    }

    fun init(params: WeatherRequest, query: Map<String, String?> = emptyMap()) {
        weather = weatherRepository.getWeather(params, query)
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }
}

fun Application.hasPermission(permission: String): Boolean = this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED