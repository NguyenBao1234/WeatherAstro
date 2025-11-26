package com.example.weatherastro.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherastro.Model.ApiState
import com.example.weatherastro.api.RetrofitObj
import com.example.weatherastro.Model.WeatherModel
import kotlinx.coroutines.launch

class WeatherVM : ViewModel()
{
    private val WeatherApiInst = RetrofitObj.WeatherApiInstance
    private val _WeatherApiResponse = MutableLiveData<ApiState<WeatherModel>>()
    val mWeatherResponse : LiveData<ApiState<WeatherModel>> = _WeatherApiResponse


    fun GetData(inCityName:String)
    {
        Log.i("Requesting City Name:" ,inCityName)
        _WeatherApiResponse.value = ApiState.Loading

        viewModelScope.launch {
            try
            {
                val ResponseData = WeatherApiInst.GetWeatherData("2715a0ad04c246f9854163700250111", inCityName)
                if(ResponseData.isSuccessful)
                {
                    Log.i("APIResponse Success:",ResponseData.body().toString())
                    ResponseData.body()?.let{_WeatherApiResponse.value = ApiState.Success(it) }
                }
                else _WeatherApiResponse.value = ApiState.Error("Failed to load data")

            } catch (e: Exception) {
                _WeatherApiResponse.value = ApiState.Error("Failed to load data")
            }
        }

    }
}