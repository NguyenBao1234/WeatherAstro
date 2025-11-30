package com.example.weatherastro.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherastro.Model.ApiState
import com.example.weatherastro.Model.Forecast.ForecastModel
import com.example.weatherastro.api.RetrofitObj
import com.example.weatherastro.Model.WeatherModel
import kotlinx.coroutines.launch

class WeatherVM : ViewModel()
{
    private val WeatherApiInst = RetrofitObj.WeatherApiInstance
    private val _WeatherApiResponse = MutableLiveData<ApiState<WeatherModel>>()
    private val _ForecastApiResponse = MutableLiveData<ApiState<ForecastModel>>()
    val mWeatherResponse : LiveData<ApiState<WeatherModel>> = _WeatherApiResponse
    val forecastResponse : LiveData<ApiState<ForecastModel>> = _ForecastApiResponse

    fun GetData(inCityName:String)
    {
        Log.i("Requesting City Name:" ,inCityName)
        _ForecastApiResponse.value = ApiState.Loading

        viewModelScope.launch {
            try
            {
                val ResponseData = WeatherApiInst.GetForecastData("2715a0ad04c246f9854163700250111", inCityName,13)
                if(ResponseData.isSuccessful)
                {
                    Log.i("APIResponse Success:",ResponseData.body().toString())
                    ResponseData.body()?.let{_ForecastApiResponse.value = ApiState.Success(it) }
                }
                else _ForecastApiResponse.value = ApiState.Error("Failed to load data")

            } catch (e: Exception) {
                _ForecastApiResponse.value = ApiState.Error("Failed to load data")
            }
        }

    }
}