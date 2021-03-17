package com.wlj.sate7weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wlj.sate7weather.bean.BaseDataSimple
import com.wlj.sate7weather.server.ServerAPI
import com.wlj.sate7weather.utils.log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    internal val sendVCodeResult:MutableLiveData<Boolean> = MutableLiveData()
    internal val loginToken:MutableLiveData<BaseDataSimple<String>> = MutableLiveData()
    fun getVerifyCode(phoneNumber:String){
        ServerAPI.getInstance().getVerifyCode(phoneNumber).enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                log("getVerifyCode onResponse ... " + response.code() + "," + response.message())
                if(response.code() == 200){
                    sendVCodeResult.postValue(true)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                log("getVerifyCode onFailure ... " + t.message)
                sendVCodeResult.postValue(false)
            }
        })
    }
    fun loginByVerifyCode(phoneNumber:String,verifyCode:String){
        log("登陆号码==$phoneNumber,登陆验证码==$verifyCode ... ")
        ServerAPI.getInstance().loginByVCode(phoneNumber,verifyCode).enqueue(object :Callback<BaseDataSimple<String>>{
            override fun onResponse(
                call: Call<BaseDataSimple<String>>,
                response: Response<BaseDataSimple<String>>
            ) {
                val body: BaseDataSimple<String>? = response.body()
                log("登陆 ... " + body?.code + "," + body?.message)
                if(body != null && body.success){
                    loginToken.postValue(body)
                }else{
                    loginToken.postValue(null)
                }
            }

            override fun onFailure(call: Call<BaseDataSimple<String>>, t: Throwable) {
                loginToken.postValue(null)
            }
        })

    }
}