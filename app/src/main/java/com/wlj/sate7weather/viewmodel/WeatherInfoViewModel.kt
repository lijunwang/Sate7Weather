package com.wlj.sate7weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.location.BDLocation
import com.wlj.sate7weather.bean.*
import com.wlj.sate7weather.db.WeatherDao
import com.wlj.sate7weather.server.ServerAPI
import com.wlj.sate7weather.utils.log
import com.wlj.sate7weather.utils.loge
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class WeatherInfoViewModel:ViewModel(){
    var liveWeatherInfo:MutableLiveData<LiveWeatherDetail> = MutableLiveData()
    var future24Weather:MutableLiveData<List<HourlyWeather>> = MutableLiveData()
    var temperatureRange:MutableLiveData<List<Int>> = MutableLiveData()
    val aqi:MutableLiveData<AQI> = MutableLiveData()
    fun getLiveWeather(longitude:String,latitude:String){
        viewModelScope.launch(IO) {
            ServerAPI.getInstance().getLiveWeather(longitude,latitude).enqueue(object :Callback<BaseDataSimple<LiveWeatherDetail>>{
                override fun onResponse(
                    call: Call<BaseDataSimple<LiveWeatherDetail>>,
                    response: Response<BaseDataSimple<LiveWeatherDetail>>
                ) {
                    val data = response.body()?.data
                    log("getLiveWeather onResponse(lon=$longitude,lat=$latitude) ... $data ${response.code()}")
                    data?.let {
                        liveWeatherInfo.postValue(it)
                    }
                }

                override fun onFailure(call: Call<BaseDataSimple<LiveWeatherDetail>>, t: Throwable) {
                    loge("getLiveWeather onFailure ... ${t.message}")
                }
            })

        }
    }

    //通过regionID去查询逐时天气
    private fun getFuture24Weather(regionId:Int){
        viewModelScope.launch(IO) {
            ServerAPI.getInstance().getFuture24Weather("" + regionId).enqueue(object :Callback<BaseDataList<HourlyWeather>>{
                override fun onResponse(
                    call: Call<BaseDataList<HourlyWeather>>,
                    response: Response<BaseDataList<HourlyWeather>>
                ) {
                    val hourlyWeather = response.body()?.data
                    log("getFuture24Weather[$regionId] onResponse ...${hourlyWeather?.size}")
                    hourlyWeather?.let {
                        future24Weather.postValue(hourlyWeather)
                    }
                }
                override fun onFailure(call: Call<BaseDataList<HourlyWeather>>, t: Throwable) {
                    loge("getFuture24Weather onFailure ..." + t.message)
                }
            })
        }
    }

    fun getFuture24Weather(location:BDLocation?,dao:WeatherDao){
        viewModelScope.launch(IO) {
            var regionId = 0
            var level = 0;
            location?.let {
                regionId =  dao.queryWeatherRegionId(location.district)
                if(regionId == 0){
                    log("通过xx区去获取region失败...尝试用城市名去获取")
                    regionId = dao.queryWeatherRegionId(location.city)
                    level ++
                }

                if(regionId == 0){
                    loge("获取region失败")
                }else{
                    if(level == 0){
                        log("[${location.district}] 的regionId == $regionId" )
                    }else{
                        log("[${location.city}] 的regionId == $regionId" )
                    }
                    getFuture24Weather(regionId)
                }
            }

        }
    }

    fun getAQI(longitude:String,latitude:String){
        viewModelScope.launch(IO) {
            ServerAPI.getInstance().getAqi(longitude,latitude).enqueue(object :Callback<BaseDataSimple<AQI>>{
                override fun onResponse(
                    call: Call<BaseDataSimple<AQI>>,
                    response: Response<BaseDataSimple<AQI>>
                ) {
                    var aqiDetail = response.body()?.data
                    log("AQI ... ${aqiDetail?.aqiLevel}")
                    aqi.postValue(aqiDetail)
                }

                override fun onFailure(call: Call<BaseDataSimple<AQI>>, t: Throwable) {
                    loge("AQI onFailure ... ${t.message}")
                }
            })
        }
    }

    fun getWeatherRange(cityName:String){
        viewModelScope.launch(IO) {
            ServerAPI.getInstance().getTempRang(cityName).enqueue(object :Callback<BaseDataList<Int>>{
                override fun onResponse(
                    call: Call<BaseDataList<Int>>,
                    response: Response<BaseDataList<Int>>
                ) {
                    val data = response.body()?.data
                    log("getWeatherRange ...$cityName  $data")
                    temperatureRange.postValue(data)
                }

                override fun onFailure(call: Call<BaseDataList<Int>>, t: Throwable) {
                    log("getWeatherRange onFailure ... ${t.message}")
                }
            })
        }
    }
}