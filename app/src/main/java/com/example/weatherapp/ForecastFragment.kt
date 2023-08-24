package com.example.weatherapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.ForeCastAdapter
import com.example.weatherapp.modal.WeatherList
import com.example.weatherapp.mvvm.WeatherVm

class ForecastFragment : Fragment() {

    private lateinit var adapterForeCastAdapter: ForeCastAdapter
    lateinit var viM: WeatherVm
    lateinit var rvForeCast: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewmodel, recyclerview adapter
        viM = ViewModelProvider(this).get(WeatherVm::class.java)
        adapterForeCastAdapter = ForeCastAdapter()

        rvForeCast = view.findViewById(R.id.rvForeCast)

        val sharedPrefs = Preferences.getInstance(requireContext())
        val city = sharedPrefs.getValueOrNull("city")

        Log.d("Prefs", city.toString())

        //dohvati podatke ako je grad postoji
        if (city != null) {
            viM.getForecastUpcoming(city)
        } else {
            viM.getForecastUpcoming()
        }

        //livedata
        viM.forecastWeatherLiveData.observe(viewLifecycleOwner) {
            val setNewlist = it as List<WeatherList>

            Log.d("Forecast LiveData", setNewlist.toString())

            adapterForeCastAdapter.setList(setNewlist)
            rvForeCast.adapter = adapterForeCastAdapter
        }
    }
}
