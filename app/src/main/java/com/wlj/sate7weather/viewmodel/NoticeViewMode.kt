package com.wlj.sate7weather.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wlj.sate7weather.bean.BaseDataList
import com.wlj.sate7weather.bean.WeatherAlarmBean
import com.wlj.sate7weather.server.ServerAPI
import com.wlj.sate7weather.utils.getToke
import com.wlj.sate7weather.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeViewMode :ViewModel(){
    val noticeInfo = MutableLiveData<List<WeatherAlarmBean>>()
    fun getNoticeInfo(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            ServerAPI.getInstance().getWeatherAlarm(getToke(context)).enqueue(object:Callback<BaseDataList<WeatherAlarmBean>>{
                override fun onResponse(
                    call: Call<BaseDataList<WeatherAlarmBean>>,
                    response: Response<BaseDataList<WeatherAlarmBean>>
                ) {
                    log("getNoticeInfo ..." + response.code() + "," + response.body()?.data?.size)
                    log("getNoticeInfo content == " + response.body()?.data?.get(0))
                    response.body()?.data?.let {
                        noticeInfo.postValue(it)
                    }
                }
//预警信息（中国气象网站获取）数据说明：
// title：预警信息标题，
// type：预警类型,
// longitude：经度,
// latitude：纬度,
// description : 预警信息描述,
// effective:有效时间,
// headline:预警信息概要

                override fun onFailure(call: Call<BaseDataList<WeatherAlarmBean>>, t: Throwable) {
                    log("getNoticeInfo ..." + t.message)
                }
            })
        }
    }
}