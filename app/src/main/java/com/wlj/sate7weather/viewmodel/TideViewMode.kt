package com.wlj.sate7weather.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wlj.sate7weather.bean.BaseDataList
import com.wlj.sate7weather.bean.BaseDataSimple
import com.wlj.sate7weather.bean.TideBean
import com.wlj.sate7weather.bean.TideSiteDetailIBean
import com.wlj.sate7weather.server.ServerAPI
import com.wlj.sate7weather.utils.getToke
import com.wlj.sate7weather.utils.log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TideViewMode: ViewModel() {
    val tideList: MutableLiveData<List<TideBean>> = MutableLiveData()
    fun getTideList(context:Context){
        viewModelScope.launch(IO) {
            ServerAPI.getInstance().getTideList(getToke(context)).enqueue(object :Callback<BaseDataList<TideBean>>{
                override fun onResponse(
                    call: Call<BaseDataList<TideBean>>,
                    response: Response<BaseDataList<TideBean>>
                ) {
                    log("tide list ... " + response.code() + "," + response.message())
                    log("tide data ... " + response.body()?.data?.size)
                    log("tide data ... " + response.body()?.data)
                    tideList.postValue(response.body()?.data)
                }

                override fun onFailure(call: Call<BaseDataList<TideBean>>, t: Throwable) {
                    log("tide onFailure ... " + t.message)
                }
            })
        }
    }

    fun getDetailInfo(context: Context,lon:Double,lat:Double){
        viewModelScope.launch(IO){
            ServerAPI.getInstance().getTideDetailInfo(getToke(context),lon,lat).enqueue(object :Callback<BaseDataSimple<TideSiteDetailIBean>>{
                override fun onResponse(
                    call: Call<BaseDataSimple<TideSiteDetailIBean>>,
                    response: Response<BaseDataSimple<TideSiteDetailIBean>>
                ) {
                    log("tide site detail info 22... " + response.code() + "," + response.message())
                    log("tide site detail data ... " + response.body()?.data)
                }

                override fun onFailure(
                    call: Call<BaseDataSimple<TideSiteDetailIBean>>,
                    t: Throwable
                ) {
                    log("tide site detail onFailure ... " + t.message)
                }

            })
        }
    }

    fun getDetailInfoRaw(context: Context,lon:Double,lat:Double){
        viewModelScope.launch(IO){
            ServerAPI.getInstance().getTideDetailInfoRaw(getToke(context),lon,lat).enqueue(object :Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    log("getDetailInfoRaw message ... " + response.message())
                    log("getDetailInfoRaw code ... " + response.code())
                    log("getDetailInfoRaw ... " + response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    log("getDetailInfoRaw onFailure... " + t.message)
                }

            })
        }
    }
}