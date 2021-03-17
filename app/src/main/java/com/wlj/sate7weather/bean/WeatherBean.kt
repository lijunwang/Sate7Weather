package com.wlj.sate7weather.bean

import android.content.Context
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

const val DebugShowALLMessage = true
data class BaseDataSimple<T>(val code: Int,val message: String,val success:Boolean,val data:T)
data class BaseDataList<T>(val code: Int,val message: String,val success:Boolean,val data:List<T>)
//实时天气状况
data class LiveWeatherDetail(
    @SerializedName("apparent_temperature")
    val apparentTemperature: Double,
    @SerializedName("cloudrate")
    val cloudRate: Double,//云量 90% 6
    val comfortDesc: String,
    val comfortIndex: Int,
    val direction: Double,//风向 东风1级 2
    val humidity: Double,//湿度 70% 1
    @SerializedName("precipitationDatasource")
    val precipitationDataSource: String,//降水数据源
    val precipitationIntensity: Double,//降水量 5mm 5
    val precipitationStatus: String,
    val pressure: Double,//气压 1014hPa 4
    @SerializedName("skycon")
    val skyType: String,//天气类型
    val speed: Double,
    val temperature: Double,
    val ultravioletDesc: String,
    val ultravioletIndex: Double,
    val visibility: Double,//能见度 7 15.3km
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

data class TyphoonBaseInfo(
    @SerializedName("begintime")
    val beginTime: String,
    @SerializedName("endtime")
    val endTime: Any,
    val nameEn: String,
    val typhoonNo: String,
    @SerializedName("typhooncnname")
    val typhoonCNName: String
) {
    override fun toString(): String {
        return "TyphoonBaseInfo(beginTime='$beginTime', endTime=$endTime, nameEn='$nameEn', typhoonNo='$typhoonNo', typhoonCNName='$typhoonCNName')"
    }
}
@Parcelize
data class TyphoonDetailInfo(
    val direction: String,
    val latitude: String,
    val longitude: String,
    val nameEn: String,
    val pressure: String,
    val speed: String,
    val strength: String,
    val timeBeijing: String,
    val typhoonNo: String,
    @SerializedName("typhooncnname")
    val typhoonCNName: String,
    val windSpeed: String,
    @SerializedName("windcircle30kts")
    val windCircle30kts: String?,//七级风圈的半径
    @SerializedName("windcircle50kts")
    val windCircle50kts: String?,//十级风圈的半径
    @SerializedName("windcircle64kts")
    val windCircle64kts: String?//十二级风圈的半径
): Parcelable
// title：预警信息标题，
// type：预警类型,
// longitude：经度,
// latitude：纬度,
// description : 预警信息描述,
// effective:有效时间,
// headline:预警信息概要
data class WeatherAlarmBean(
    val description: String,
    val effective: String,
    val headline: String,
    val id: String,
    val latitude: Any,
    val longitude: Any,
    val title: String,
    val type: String
)

@Parcelize
data class TideBean(
    val areaid: String,
    val cn_name: String,
    val en_name: String,
    val lat: Double,
    val lon: Double,
    val province: String
):Parcelable

data class TideSiteDetailIBean(
    val forecast: List<Forecast>,
    val tide_station_info: TideStationInfo
)

data class Forecast(
    val DateTime_Local: String,
    val DateTime_UTC: String,
    val Hight: Hight,
    val Type: String
)

data class TideStationInfo(
    val id: String,
    val tide_station_name_CN: String,
    val tide_station_name_EN: String
)

data class Hight(
    val Imperial: Imperial,
    val Metric: Metric
)

data class Imperial(
    val unit: String,
    val value: Double
)

data class Metric(
    val unit: String,
    val value: Double
)

data class TokenBean(
    val code: Int,
    val `data`: Any,
    val message: String,
    val success: Boolean
)

data class UserInfoBean(
    val address: String,
    @SerializedName("companyname")
    val companyName: String,
    @SerializedName("headerimg")
    val headerImg: String,
    val id: String,
    val industry: String,
    val mobile: String,
    val nickname: String,
    val username: String
)

