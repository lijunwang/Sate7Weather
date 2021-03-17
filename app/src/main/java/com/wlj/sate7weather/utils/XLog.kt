package com.wlj.sate7weather.utils

import android.util.Log

fun log(msg:String){
    Log.d("Sate7Weather",msg)
}
fun logPermission(msg:String){
    Log.d("Sate7Weather","[AndPermission]$msg")
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


fun logTyphoon(msg:String){
    Log.d("Sate7Weather", "[Typhoon] $msg")
}

fun logRxJava(msg:String){
    Log.d("Sate7Weather", "[RxJava] $msg")
}