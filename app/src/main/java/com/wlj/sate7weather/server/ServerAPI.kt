package com.wlj.sate7weather.server

import com.wlj.sate7weather.bean.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val TEST_MODEL = false
//http://1.202.89.226:23091/api/1.0/forecast/hourfc?areaid=0755
//var BASE_URL = "http://1.202.89.226:23091/"
var BASE_URL = "http://47.104.129.239:8081/"
var BASE_URL_TEST = "https://www.wanandroid.com/"

interface ServerAPI {
    @GET("article/list/{page}/json")
    fun testWan(@Path("page") pageId:Int):Call<ResponseBody>

    @GET("weather/liveData")//实时天气
    fun getLiveWeather(@Query("lon") lon: String,@Query("lat") lat:String):Call<BaseDataSimple<LiveWeatherDetail>>

    @GET("weather/getHourlyForecastList")//逐时天气
    fun getFuture24Weather(@Query("areaid") regionId:String):Call<BaseDataList<HourlyWeather>>

    @GET("weather/getAqi")
    fun getAqi(@Query("lon") lon: String,@Query("lat") lat:String):Call<BaseDataSimple<AQI>>

    @GET("weather/getTempAreaByAreaName")//气温范围 最低温 ~ 最高温
    fun getTempRang(@Query("areaName") cityName:String):Call<BaseDataList<Int>>
    companion object INSTANCE {
        private var serverAPI: ServerAPI? = null
        fun getInstance(): ServerAPI {
            if (serverAPI == null) {
                synchronized(this) {
                    if (serverAPI == null){
                        serverAPI =
                            Retrofit.Builder().
                            baseUrl(if(TEST_MODEL) BASE_URL_TEST else BASE_URL).
                            client(OkHttpClient()).
                            addConverterFactory(ScalarsConverterFactory.create()).
                            addConverterFactory(GsonConverterFactory.create()).
                            build().
                            create(ServerAPI::class.java)
                    }
                }
            }
            return serverAPI!!
        }
    }
}