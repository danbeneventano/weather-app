package me.danbeneventano.weather

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import com.github.salomonbrys.kodein.*
import me.danbeneventano.weather.dataclasses.WeatherService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApp : Application(), KodeinAware {
    private val application = this

    override val kodein: Kodein by Kodein.lazy {
        bind<LocationManager>() with instance(application.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        bind<Geocoder>() with singleton { Geocoder(application) }
        bind<Retrofit>() with singleton {
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.darksky.net/forecast/b3d0589e5a90ee0dcdddb40751724b7a/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
        bind<WeatherService>() with singleton { instance<Retrofit>().create(WeatherService::class.java) }
        bind<Context>() with instance(application)
        bind<Application>() with instance(application)
        bind<WeatherRepository>() with singleton { WeatherRepository(instance<WeatherService>()) }
        bind<ConnectivityManager>() with instance(application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    }
}