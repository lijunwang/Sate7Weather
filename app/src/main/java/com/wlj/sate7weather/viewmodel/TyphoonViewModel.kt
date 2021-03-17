package com.wlj.sate7weather.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wlj.sate7weather.bean.BaseDataList
import com.wlj.sate7weather.bean.TyphoonBaseInfo
import com.wlj.sate7weather.bean.TyphoonDetailInfo
import com.wlj.sate7weather.server.ServerAPI
import com.wlj.sate7weather.utils.getToke
import com.wlj.sate7weather.utils.logTyphoon
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TyphoonViewModel : ViewModel(){
    val typhoonList:MutableLiveData<List<TyphoonBaseInfo>> = MutableLiveData()
    val typhoonDetailInfo:MutableLiveData<List<TyphoonDetailInfo>> = MutableLiveData()

    fun getTyphoonList(context: Context,date:String){
        viewModelScope.launch(IO) {
            ServerAPI.getInstance().getTyphoonList(getToke(context),date).enqueue(object :retrofit2.Callback<BaseDataList<TyphoonBaseInfo>> {
                override fun onResponse(
                    call: Call<BaseDataList<TyphoonBaseInfo>>,
                    response: Response<BaseDataList<TyphoonBaseInfo>>
                ) {
                    val data = response.body()?.data
                    data?.let {
                        typhoonList.postValue(it)
                        logTyphoon("getTyphoonList onResponse[$date] ... $it")
                    }
                }

                override fun onFailure(call: Call<BaseDataList<TyphoonBaseInfo>>, t: Throwable) {
                    logTyphoon("getTyphoonList onFailure ... " + t.message)
                }
            })
        }
    }

    fun getTyphoonDetailInfo(context:Context,id:String){
        viewModelScope.launch(IO) {
            ServerAPI.getInstance().getTyphoonDetailInfo(getToke(context),id).enqueue(object : Callback<BaseDataList<TyphoonDetailInfo>> {
                override fun onResponse(
                    call: Call<BaseDataList<TyphoonDetailInfo>>,
                    response: Response<BaseDataList<TyphoonDetailInfo>>
                ) {
                    val data = response.body()?.data
                    data?.let {
                        typhoonDetailInfo.postValue(it)
                        logTyphoon("getTyphoonDetailInfo onResponse[$id] ... ${it.size} ... [0]== ${it[0]}")
                    }
                }

                override fun onFailure(
                    call: Call<BaseDataList<TyphoonDetailInfo>>,
                    t: Throwable
                ) {
                    logTyphoon("getTyphoonDetailInfo onFailure ... " + t.message)
                }
            })
        }
    }
}