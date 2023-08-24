package com.example.weatherapp.mvvm

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.*
import com.example.weatherapp.modal.WeatherList
import com.example.weatherapp.service.RetrofitInstance
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
class WeatherVm : ViewModel() {
    //LiveData
    val todayWeatherLiveData = MutableLiveData<List<WeatherList>>()
    val forecastWeatherLiveData = MutableLiveData<List<WeatherList>>()
    val closetorexactlysameweatherdata = MutableLiveData<WeatherList?>()
    val cityName = MutableLiveData<String>()
    private val context = Application.instance

    //dohvaca weather data za dan grad
    fun getWeather(city: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val todayWeatherList = mutableListOf<WeatherList>()

        val currentDateTime = LocalDateTime.now()
        val currentDateO = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val sharedPrefs = Preferences.getInstance(context)
        val lat = sharedPrefs.getValue("lat").toString()
        val lon = sharedPrefs.getValue("lon").toString()

        //API call
        val call = if (city != null) {
            RetrofitInstance.api.getWeatherByCity(city)
        } else {
            RetrofitInstance.api.getCurrentWeather(lat, lon)
        }
        val response = call.execute()

        if (response.isSuccessful) {
            val weatherList = response.body()?.weatherList

            cityName.postValue(response.body()?.city!!.name)

            weatherList?.forEach { weather ->
                if (weather.dtTxt!!.split("\\s".toRegex()).contains(currentDateO)) {
                    todayWeatherList.add(weather)
                }
            }

            val closestWeather = findClosestWeather(todayWeatherList)
            closetorexactlysameweatherdata.postValue(closestWeather)

            todayWeatherLiveData.postValue(todayWeatherList)

        } else {
            val errorMessage = response.message()
            Log.e("CurrentWeatherError", "Error: $errorMessage")
        }
    }

    fun getForecastUpcoming(city: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val forecastWeatherList = mutableListOf<WeatherList>()

        val currentDateTime = LocalDateTime.now()
        val currentDateO = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val sharedPrefs = Preferences.getInstance(context)
        val lat = sharedPrefs.getValue("lat").toString()
        val lon = sharedPrefs.getValue("lon").toString()


        val call = if (city != null) {
            RetrofitInstance.api.getWeatherByCity(city)
        } else {
            RetrofitInstance.api.getCurrentWeather(lat, lon)
        }

        val response = call.execute()

        if (response.isSuccessful) {
            val weatherList = response.body()?.weatherList

            weatherList?.forEach { weather ->
                if (!weather.dtTxt!!.split("\\s".toRegex()).contains(currentDateO)) {
                    if (weather.dtTxt!!.substring(11, 16) == "12:00") {
                        forecastWeatherList.add(weather)
                    }
                }
            }
            forecastWeatherLiveData.postValue(forecastWeatherList)

            Log.d("Forecast LiveData", forecastWeatherLiveData.value.toString())
        } else {
            val errorMessage = response.message()
            Log.e("CurrentWeatherError", "Error: $errorMessage")
        }
    }


    private fun findClosestWeather(weatherList: List<WeatherList>): WeatherList? {
        val systemTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        var closestWeather: WeatherList? = null
        var minTimeDifference = Int.MAX_VALUE

        for (weather in weatherList) {
            val weatherTime = weather.dtTxt!!.substring(11, 16)
            val timeDifference = abs(timeToMinutes(weatherTime) - timeToMinutes(systemTime))

            if (timeDifference < minTimeDifference) {
                minTimeDifference = timeDifference
                closestWeather = weather
            }
        }
        return closestWeather
    }

    private fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }
}