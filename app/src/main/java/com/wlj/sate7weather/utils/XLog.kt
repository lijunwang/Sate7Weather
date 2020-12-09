package com.wlj.sate7weather.utils

import android.util.Log

fun log(msg:String){
    Log.d("Sate7Weather",msg)
}
fun logLiveWeather(msg:String){
    Log.d("Sate7Weather", "[LieWeather]$msg")
}
fun logHourlyWeather(msg:String){
    Log.d("Sate7Weather", "[HourlyWeather]$msg")
}
fun loge(msg:String){
    Log.e("Sate7Weather",msg)
}
fun logPgy(msg:String){
    Log.d("Sate7Weather","[AppUpdate]-PGY $msg")
}