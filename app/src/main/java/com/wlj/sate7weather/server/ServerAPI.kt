package com.wlj.sate7weather.server

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.wlj.sate7weather.bean.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

const val TEST_MODEL = false
//http://1.202.89.226:23091/api/1.0/forecast/hourfc?areaid=0755
//var BASE_URL = "http://1.202.89.226:23091/"
var BASE_URL = "http://47.104.129.239:8081/"
var BASE_URL_TEST = "https://www.wanandroid.com/"

interface ServerAPI {
    @GET("article/list/{page}/json")
    fun testWan(@Path("page") pageId:Int):Call<ResponseBody>

    @GET("weather/liveData")//实时天气
    fun getLiveWeather(@Header("auth") token:String,@Query("lon") lon: String,@Query("lat") lat:String):Call<BaseDataSimple<LiveWeatherDetail>>

    @GET("weather/getHourlyForecastList")//逐时天气
    fun getFuture24Weather(@Header("auth") token:String,@Query("areaid") regionId:String):Call<BaseDataList<HourlyWeather>>

    @GET("weather/getAqi")
    fun getAqi(@Header("auth") token:String,@Query("lon") lon: String,@Query("lat") lat:String):Call<BaseDataSimple<AQI>>

    @GET("weather/getTempAreaByAreaName")//气温范围 最低温 ~ 最高温
    fun getTempRang(@Header("auth") token:String,@Query("areaName") cityName:String):Call<BaseDataList<Int>>

    @GET("weather/getTyphoonList")//台风列表
    fun getTyphoonList(@Header("auth") token:String,@Query("date") date:String):Call<BaseDataList<TyphoonBaseInfo>>

    @GET("weather/getTyphoonDetails")//台风详情
    fun getTyphoonDetailInfo(@Header("auth") token:String,@Query("typhoonNo") number:String):Call<BaseDataList<TyphoonDetailInfo>>

    @GET("weather/liveData")//实时天气
    fun testRxJava(@Query("lon") lon: String,@Query("lat") lat:String): Observable<BaseDataSimple<LiveWeatherDetail>>

    /*@GET("weather/weatherAlarm")
    fun getWeatherAlarm():Observable<BaseDataList<WeatherAlarmBean>>*/
    @GET("weather/weatherAlarm")
    fun getWeatherAlarm(@Header("auth") token:String):Call<BaseDataList<WeatherAlarmBean>>

    @GET("weather/getTideList")
    fun getTideList(@Header("auth") token:String):Call<BaseDataList<TideBean>>

    @GET("weather/getTideInfoByLatLon")
    fun getTideDetailInfo(@Header("auth") token:String,@Query("lon") lon:Double,@Query("lat") lat:Double):Call<BaseDataSimple<TideSiteDetailIBean>>

    @GET("weather/getTideInfoByLatLon")
    fun getTideDetailInfoRaw(@Header("auth") token:String,@Query("lon") lon:Double,@Query("lat") lat:Double):Call<ResponseBody>

    @GET("user/sendVcode")
    fun getVerifyCode(@Query("mobile") mobile:String):Call<ResponseBody>

    @POST("user/loginByMobileVcode")
    fun loginByVCodeRaw(@Query("mobile") mobile:String,@Query("vcode") vcode:String):Call<ResponseBody>

    @POST("user/loginByMobileVcode")
    fun loginByVCode(@Query("mobile") mobile:String,@Query("vcode") vcode:String):Call<BaseDataSimple<String>>

    @GET("user/getUserInfo")
    suspend fun getUserInfo(@Header("auth") token:String):BaseDataSimple<UserInfoBean>

    @PUT("user/updateNickname")
    fun updateNickname(@Header("auth") token:String,@Query("nickname") nickname:String):Call<BaseDataSimple<UserInfoBean>>

    @Multipart
    @POST("uploadHeadImg")
    suspend fun updateHeadImg(@Header("auth") token:String,@Part requestBody: MultipartBody.Part):BaseDataSimple<String>

    @PUT("user/updateUserInfo")
    suspend fun updateUserInfo(@Header("auth") token:String,@Query("address") address:String?,@Query("industry") industry:String?):BaseDataSimple<UserInfoBean>

    @POST("user/feedback")
    suspend fun feedbackText(@Header("auth") token:String,@Query("feedback") feedback:String,@Query("imgPath") imgPath:String):BaseDataSimple<String>

    @PUT("user/updateMobile")
    suspend fun changeMobile(@Header("auth") token:String,@Query("newMobile") newMobile:String,@Query("vcode") vcode:String):BaseDataSimple<UserInfoBean>
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
                            addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
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