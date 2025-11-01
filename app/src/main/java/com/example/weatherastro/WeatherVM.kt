package com.example.weatherastro

import android.util.Log
import androidx.lifecycle.ViewModel

class WeatherVM : ViewModel()
{
    fun GetData(inCityName:String)
    {
        Log.i("Name:," ,inCityName);
    }
}