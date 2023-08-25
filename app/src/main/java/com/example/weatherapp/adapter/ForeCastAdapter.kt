package com.example.weatherapp.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapp.R
import com.example.weatherapp.modal.WeatherList
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.Glide


class ForeCastAdapter : RecyclerView.Adapter<ForeCastHolder>() {

    private var listofforecast = listOf<WeatherList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForeCastHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fiveday, parent, false)
        return ForeCastHolder(view)
    }

    override fun getItemCount(): Int {
        return listofforecast.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ForeCastHolder, position: Int) {
        val forecastObject = listofforecast[position]
        val icon = forecastObject.weather.firstOrNull()?.icon ?: ""

        for (i in forecastObject.weather) {
            holder.description.text = i.description!!
        }

        val windDirectionResource = getWindDirectionResource(forecastObject.wind?.deg)
        Glide.with(holder.itemView.context)
            .load(windDirectionResource)
            .into(holder.windDirectionIcon)


        //humidity, wind speed, temperature
        holder.humiditiy.text = forecastObject.main!!.humidity.toString()
        holder.windspeed.text = forecastObject.wind?.speed.toString()
        val temperatureFahrenheit = forecastObject.main?.temp
        val temperatureCelsius = temperatureFahrenheit?.minus(273.15)
        val temperatureFormatted = String.format("%.2f", temperatureCelsius)
        holder.temp.text = "$temperatureFormatted °C"

        //parsiranje datuma
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(forecastObject.dtTxt!!)
        val outputFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
        val dateanddayname = outputFormat.format(date!!)
        holder.dateDayName.text = dateanddayname

        //glide
        loadWeatherIcon(holder.imageGraphic, icon)
        loadWeatherIcon(holder.smallIcon, icon)
    }

    private fun getWindDirectionResource(deg: Int?): Int {
        return when (deg) {
            in 349..360, in 0..33 -> R.drawable.north
            in 34..78 -> R.drawable.northeast
            in 79..123 -> R.drawable.east
            in 124..168 -> R.drawable.southeast
            in 169..213 -> R.drawable.south
            in 214..258 -> R.drawable.southwest
            in 259..303 -> R.drawable.west
            in 304..348 -> R.drawable.northwest
            else -> R.drawable.wind
        }
    }



    private fun loadWeatherIcon(imageView: ImageView, icon: String) {
        val iconResource = when (icon) {
            "01d" -> R.drawable.sun
            "01n" -> R.drawable.clear
            "02d" -> R.drawable.psun
            "02n" -> R.drawable.pnight
            "03d", "03n" -> R.drawable.cloud1
            "10d" -> R.drawable.sunrain
            "10n" -> R.drawable.nrain
            "04d", "04n" -> R.drawable.cloudss
            "09d", "09n" -> R.drawable.drops
            "11d", "11n" -> R.drawable.thunder
            "13d", "13n" -> R.drawable.snow
            "50d", "50n" -> R.drawable.fog
            else -> R.drawable.sun
        }

        Glide.with(imageView.context)
            .load(iconResource)
            .into(imageView)
    }

    fun setList(newlist: List<WeatherList>) {
        this.listofforecast = newlist
    }
}

class ForeCastHolder(itemView: View) : ViewHolder(itemView){

    val imageGraphic: ImageView = itemView.findViewById(R.id.imageGraphic)
    val description : TextView = itemView.findViewById(R.id.weatherDescr)
    val humiditiy : TextView = itemView.findViewById(R.id.humidity)
    val windspeed : TextView = itemView.findViewById(R.id.windSpeed)
    val temp : TextView = itemView.findViewById(R.id.tempDisplayForeCast)
    val smallIcon : ImageView = itemView.findViewById(R.id.smallIcon)
    val dateDayName : TextView = itemView.findViewById(R.id.dayDateText)
    val windDirectionIcon: ImageView = itemView.findViewById(R.id.windDirectionIcon)
}

