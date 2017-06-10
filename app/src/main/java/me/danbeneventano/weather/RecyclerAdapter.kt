package me.danbeneventano.weather

import android.content.Context
import android.location.Address
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.weather_card.view.*
import me.danbeneventano.weather.dataclasses.DataBlock
import java.text.SimpleDateFormat
import java.util.*

class RecyclerAdapter(var daily: DataBlock, val ctx: Context,
                      var address: Address?)
    : RecyclerView.Adapter<RecyclerAdapter.WeatherViewHolder>() {

    private val iconMap = mapOf(
            "clear-day" to ctx.getDrawable(R.drawable.weather_clear),
            "clear-night" to ctx.getDrawable(R.drawable.weather_clear_night),
            "rain" to ctx.getDrawable(R.drawable.weather_rain_day),
            "snow" to ctx.getDrawable(R.drawable.weather_big_snow),
            "sleet" to ctx.getDrawable(R.drawable.weather_snow),
            "wind" to ctx.getDrawable(R.drawable.weather_wind),
            "fog" to ctx.getDrawable(R.drawable.weather_fog),
            "cloudy" to ctx.getDrawable(R.drawable.weather_clouds),
            "partly-cloudy-day" to ctx.getDrawable(R.drawable.weather_few_clouds),
            "partly-cloudy-night" to ctx.getDrawable(R.drawable.weather_few_clouds_night)
    )

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val dataPoint = daily.data[position]!!
        holder.apply {
            temp_unit.text = ctx.getString(R.string.temp_unit, ctx.getString(R.string.fahrenheit))
            temp.text = ctx.getString(R.string.temperature, dataPoint.temperatureMax?.toInt())
            wind_unit.text = ctx.getString(R.string.wind_unit, ctx.getString(R.string.mph))
            wind_speed.text = ctx.getString(R.string.wind_speed, dataPoint.windSpeed?.toInt())
            description.text = ctx.getString(R.string.description, dataPoint.summary)
            feels_like.text = ctx.getString(R.string.feels_like, dataPoint.apparentTemperatureMax?.toInt())
            val date = Date(dataPoint.time * 1000L)
            val dayFormat = SimpleDateFormat("EEEE")
            val neatFormat = SimpleDateFormat("MMM d, yyyy")
            day.text = ctx.getString(R.string.day, dayFormat.format(date))
            exact_date.text = ctx.getString(R.string.date, neatFormat.format(date))
            humidity.text = ctx.getString(R.string.humidity, dataPoint.humidity?.toPercentageString())
            chance_of_rain.text = ctx.getString(R.string.chance_of_rain, dataPoint.precipProbability?.toPercentageString())
            if(address?.locality != null) {
                location.text = ctx.getString(R.string.location, "${address?.locality}, ${address?.adminArea}")
            } else {
                location.text = ctx.getString(R.string.location, "${address?.adminArea}")
            }
            weather_icon.setImageDrawable(getIconFromString(dataPoint.icon!!))
            setCardColors(getColorFromDay(date), this)
        }
    }

    override fun getItemCount() = daily.data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.weather_card, parent, false))
    }

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val temp_unit = itemView.temp_unit
        val temp = itemView.temp
        val wind_unit = itemView.wind_unit
        val wind_speed = itemView.wind_speed
        val description = itemView.description
        val feels_like = itemView.feels_like
        val day = itemView.day
        val exact_date = itemView.exact_date
        val humidity = itemView.humidity
        val chance_of_rain = itemView.chance_of_rain
        val location = itemView.location
        val symbols: RelativeLayout = itemView.symbols
        val weather_icon = itemView.weather_icon
    }

    private fun getColorFromDay(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        when (calendar[Calendar.DAY_OF_WEEK]) {
            Calendar.SUNDAY -> return ctx.getColor(R.color.colorSunday)
            Calendar.MONDAY -> return ctx.getColor(R.color.colorMonday)
            Calendar.TUESDAY -> return ctx.getColor(R.color.colorTuesday)
            Calendar.WEDNESDAY -> return ctx.getColor(R.color.colorWednesday)
            Calendar.THURSDAY -> return ctx.getColor(R.color.colorThursday)
            Calendar.FRIDAY -> return ctx.getColor(R.color.colorFriday)
            Calendar.SATURDAY -> return ctx.getColor(R.color.colorSaturday)
            else -> return ctx.getColor(R.color.colorSunday)
        }
    }

    private fun setCardColors(color: Int, holder: WeatherViewHolder) {
        holder.apply {
            day.setTextColor(color)
            exact_date.setTextColor(color)
            symbols.setBackgroundColor(color)
        }
    }

    private fun getIconFromString(string: String) = iconMap.withDefault { ctx.getDrawable(R.drawable.weather_clear) }[string]
}