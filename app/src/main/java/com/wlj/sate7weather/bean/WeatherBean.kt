package com.wlj.sate7weather.bean

import android.content.Context
import com.google.gson.annotations.SerializedName
const val DebugShowALLMessage = false
data class BaseDataSimple<T>(val code: Int,val message: String,val success:Boolean,val data:T)
data class BaseDataList<T>(val code: Int,val message: String,val success:Boolean,val data:List<T>)
//实时天气状况
data class LiveWeatherDetail(
    @SerializedName("apparent_temperature")
    val apparentTemperature: Double,
    @SerializedName("cloudrate")
    val cloudRate: Double,//云量
    val comfortDesc: String,
    val comfortIndex: Int,
    val direction: Double,
    val humidity: Double,
    @SerializedName("precipitationDatasource")
    val precipitationDataSource: String,//降水数据源
    val precipitationIntensity: Double,//降水量
    val precipitationStatus: String,
    val pressure: Double,
    @SerializedName("skycon")
    val skyType: String,//天气类型
    val speed: Double,
    val temperature: Double,
    val ultravioletDesc: String,
    val ultravioletIndex: Double,
    val visibility: Double,
    val speedDesc:String,
    val directionDesc:String
) {
    override fun toString(): String {
        return if (DebugShowALLMessage){
            "LiveWeatherDetail(apparent_temperature=$apparentTemperature, cloudRate=$cloudRate, comfortDesc='$comfortDesc', comfortIndex=$comfortIndex, direction=$direction, humidity=$humidity, precipitationDataSource='$precipitationDataSource', precipitationIntensity=$precipitationIntensity, precipitationStatus='$precipitationStatus', pressure=$pressure, skyType='$skyType', speed=$speed, temperature=$temperature, ultravioletDesc='$ultravioletDesc', ultravioletIndex=$ultravioletIndex, visibility=$visibility)"
        }else{
            "LiveWeatherDetail(体感温度=$apparentTemperature,风速=$speed,温度=$temperature,湿度=$humidity)"
        }
    }
}

//逐小时天气
data class HourlyWeather(
    @SerializedName("ja")
    val weatherId: String,//天气现象编号
    @SerializedName("jb")
    val temperature: String,//温度
    @SerializedName("jc")
    val windDirectionId: String,//风向编号
    @SerializedName("jd")
    val windSpeed: String,//风速
    @SerializedName("je")
    val humidity: String,//相对湿度
    @SerializedName("jf")
    val time: String//预报时间
) {
    fun getHour(): String {
        return time.substring(8,10) + "h"
    }

    fun getWeatherTypeResId(context: Context): Int {
        return context.resources.getIdentifier("weather_type_$weatherId","drawable",context.packageName)
    }
    override fun toString(): String {
        return "HourlyWeather(weatherId='$weatherId', temperature='$temperature', windDirectionId='$windDirectionId', windSpeed='$windSpeed', humidity='$humidity', time='$time')"
    }
}

//空气质量
data class AQI(
    val aqiLevel: String,
    val chn: Int,//中国指数
    val usa: Int//美国指数
) {
    override fun toString(): String {
        return "AQI(aqiLevel='$aqiLevel', chn=$chn, usa=$usa)"
    }
}

