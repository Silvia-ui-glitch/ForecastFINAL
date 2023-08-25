package com.example.weatherapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weatherapp.adapter.WeatherToday
import com.example.weatherapp.databinding.TestlayoutBinding
import com.example.weatherapp.modal.WeatherList
import com.example.weatherapp.modal.Wind
import com.example.weatherapp.mvvm.WeatherVm
import java.text.SimpleDateFormat
import java.util.*





@SuppressWarnings("DEPRECATION")
class WeatherToday : Fragment() {

    lateinit var viM: WeatherVm

    lateinit var adapter: WeatherToday

    private lateinit var binding: TestlayoutBinding


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //inflate the fragment layout
        binding = DataBindingUtil.inflate(inflater, R.layout.testlayout, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewmodel
        viM = ViewModelProvider(this).get(WeatherVm::class.java)
        viM.getWeather()
        adapter = WeatherToday()

        //clear city
        val sharedPrefs = Preferences.getInstance(requireActivity())
        sharedPrefs.clearCityValue()



        //observe LiveData za danasnje vrijeme
        viM.todayWeatherLiveData.observe(viewLifecycleOwner) {
            val setNewlist = it as List<WeatherList>
            adapter.setList(setNewlist)
            binding.forecastRecyclerView.adapter = adapter
        }

        //bind ViewModel i data na layout
        binding.lifecycleOwner = this
        binding.vm = viM

        Glide.with(this)
            .load(R.drawable.rain)
            .into(binding.mainRainIcon)


        Glide.with(this)
            .load(R.drawable.humidity)
            .into(binding.mainHumidityIcon)

        viM.closetorexactlysameweatherdata.observe(viewLifecycleOwner) {
            val temperatureFahrenheit = it!!.main?.temp
            val temperatureCelsius = temperatureFahrenheit?.minus(273.15)
            val temperatureFormatted = String.format("%.2f", temperatureCelsius)

            binding.windSpeed.text = it.wind?.speed.toString()
            val windData = it.wind ?: Wind()
            loadWindIcon(binding.mainWindIcon, windData)

            //postavljanje ikona prema vremenu - glide
            for (i in it.weather) {
                binding.descMain.text = i.description
                i.icon?.let { it1 -> loadWeatherIcon(binding.imageMain, it1) }
            }

            binding.tempMain.text = "$temperatureFormatted°"

            binding.humidityMain.text = it.main!!.humidity.toString()
            binding.windSpeed.text = it.wind?.speed.toString()

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(it.dtTxt!!)
            val outputFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
            val dateanddayname = outputFormat.format(date!!)

            binding.dateDayMain.text = dateanddayname

            binding.chanceofrain.text = "${it.pop.toString()}%"
        }

        //prikaz prognoze za ZG kad se pokrene
        getZagreb()
        //viM.getWeather("Zagreb") - dohvaca "donji grad"

        //search
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Preferences.getInstance(requireActivity())
                sharedPrefs.setValueOrNull("city", query!!)

                if (query.isNotEmpty()) {
                    //dohvati info za trazeni grad
                    viM.getWeather(query)
                    //clear search
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                    binding.searchView.isIconified = true
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        //5 dana botun, vodi na drugi fragment
        binding.next5Days.setOnClickListener {
            //debugging
            //Log.d("WeatherToday", "5 Days Button Clicked")
            val foreCastFragment = ForecastFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, foreCastFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun getZagreb() {
        val latitude = 45.81444
        val longitude = 15.97798

        val myprefs = Preferences(requireContext())
        myprefs.setValue("lon", longitude.toString())
        myprefs.setValue("lat", latitude.toString())
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

    //smjer vjetra
    private fun loadWindIcon(imageView: ImageView, wind: Wind) {
        val iconResource = when (wind.deg ?: 0) {
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

        Glide.with(imageView.context)
            .load(iconResource)
            .into(imageView)
    }
}




