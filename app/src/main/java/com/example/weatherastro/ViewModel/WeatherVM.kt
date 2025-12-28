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
    public val forecastApiResponse = MutableLiveData<ApiState<ForecastModel>>()
    val forecastResponse : LiveData<ApiState<ForecastModel>> = forecastApiResponse

    fun GetData(inCityName:String)
    {
        Log.i("Requesting City Name:" ,inCityName)
        forecastApiResponse.value = ApiState.Loading

        viewModelScope.launch {
            try
            {
                val ResponseData = WeatherApiInst.GetForecastData("2715a0ad04c246f9854163700250111", inCityName,13)
                if(ResponseData.isSuccessful)
                {
                    Log.i("APIResponse Success:",ResponseData.body().toString())
                    ResponseData.body()?.let{forecastApiResponse.value = ApiState.Success(it) }
                }
                else forecastApiResponse.value = ApiState.Error("Failed to load data")

            } catch (e: Exception) {
                forecastApiResponse.value = ApiState.Error("Failed to load data")
            }
        }

    }
}