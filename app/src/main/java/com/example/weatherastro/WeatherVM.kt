package com.example.weatherastro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherastro.api.RetrofitObj
import kotlinx.coroutines.launch

class WeatherVM : ViewModel()
{
    private val WeatherApiInst = RetrofitObj.WeatherApiInstance
    fun GetData(inCityName:String)
    {
        Log.i("City Name:" ,inCityName)
        viewModelScope.launch{
            val ResponseData = WeatherApiInst.GetWeatherData("2715a0ad04c246f9854163700250111", inCityName)
            if(ResponseData.isSuccessful) {
                Log.i("APIResponse Success:",ResponseData.body().toString())
            }
            else{Log.i("API Response failed:", "he")}
        }

    }
}