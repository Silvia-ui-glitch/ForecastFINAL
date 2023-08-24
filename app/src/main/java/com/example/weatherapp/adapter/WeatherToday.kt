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
import com.example.weatherapp.R
import com.example.weatherapp.modal.WeatherList
import java.time.*
import java.util.*
import com.bumptech.glide.Glide


class WeatherToday : RecyclerView.Adapter<TodayHolder>() {

    private var listOfTodayWeather = listOf<WeatherList>()

    //viewholder for recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.todayforecast, parent, false)
        return TodayHolder(view)


    }

    override fun getItemCount(): Int {
        return listOfTodayWeather.size
    }

    //bind data -viewholder
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TodayHolder, position: Int) {
        val todayForeCast = listOfTodayWeather[position]
        val icon = todayForeCast.weather.firstOrNull()?.icon ?: ""

        val temperatureFahrenheit = todayForeCast.main?.temp
        val temperatureCelsius = temperatureFahrenheit?.minus(273.15)
        val temperatureFormatted = String.format("%.2f", temperatureCelsius)

        holder.timeDisplay.text = todayForeCast.dtTxt?.substring(11, 16).toString()
        holder.tempDisplay.text = "$temperatureFormatted °C"

        //ikone - Glide
        loadWeatherIcon(holder.imageDisplay, icon)
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


    fun setList(listOfToday: List<WeatherList>) {
        this.listOfTodayWeather = listOfToday
    }
}

class TodayHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val imageDisplay : ImageView = itemView.findViewById(R.id.imageDisplay)
    val tempDisplay : TextView = itemView.findViewById(R.id.tempDisplay)
    val timeDisplay : TextView = itemView.findViewById(R.id.timeDisplay)
}



