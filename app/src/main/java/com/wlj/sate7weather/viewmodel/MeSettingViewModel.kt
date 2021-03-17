package com.wlj.sate7weather.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wlj.sate7weather.bean.BaseDataSimple
import com.wlj.sate7weather.bean.UserInfoBean
import com.wlj.sate7weather.server.ServerAPI
import com.wlj.sate7weather.utils.getToke
import com.wlj.sate7weather.utils.log
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MeSettingViewModel: ViewModel() {
    internal val observeChangeUserInfoResult = MutableLiveData<BaseDataSimple<UserInfoBean>>()
    internal val observeGetUserInfoBean = MutableLiveData<BaseDataSimple<UserInfoBean>>()
    internal val feedbackResult = MutableLiveData<Boolean>()
    //获取用户信息
    fun getUserInfo(context:Context) = viewModelScope.launch {
        val userInfoBean = ServerAPI.getInstance().getUserInfo(getToke(context))
        observeGetUserInfoBean.postValue(userInfoBean)
    }
    //修改用户信息(昵称修改) TODO 后台合并
    fun changeName(context: Context,newName:String){
        ServerAPI.getInstance().updateNickname(getToke(context),newName).enqueue(object :Callback<BaseDataSimple<UserInfoBean>>{
            override fun onResponse(call: Call<BaseDataSimple<UserInfoBean>>, response: Response<BaseDataSimple<UserInfoBean>>) {
                if(response.body() != null && response.body()!!.success){
                    log("修改昵称成功,去通知 ...")
                    observeChangeUserInfoResult.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<BaseDataSimple<UserInfoBean>>, t: Throwable) {
                log("onFailure ... " + t.message)
                log("修改昵称失败,去通知 ...")
                observeChangeUserInfoResult.postValue(null)
            }
        })
    }

    fun changeUserInfo(context: Context,address:String? = null,industry:String? = null){
        viewModelScope.launch {
            val result = ServerAPI.getInstance().updateUserInfo(getToke(context),address,industry)
            log("修改地址和行业成功,去通知 ... ")
            observeChangeUserInfoResult.postValue(result)
        }
    }

    fun updateHeadImg(context: Context,file:File){
        log("开始上传头像 ... " + file.exists() + "," + file.absolutePath + "," + file.canRead())
        val requestFile = RequestBody.create(MediaType.parse("image/png"), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)
        viewModelScope.launch {
            val result = ServerAPI.getInstance().updateHeadImg(getToke(context),body)
            log("updateHeadImg result==$result")
            if(!result.success){
                Toast.makeText(context, "上传失败:图片尺寸不能超过5M", Toast.LENGTH_SHORT).show()
            }
            getUserInfo(context)
        }
    }

    fun feedbackTextImage(context: Context,msg:String){
        viewModelScope.launch {
            val result = ServerAPI.getInstance().feedbackText(getToke(context),msg,"sb")
            if(result.success){
                feedbackResult.postValue(true)
            }
        }
    }

    fun modifyPhone(context: Context,newMobile:String,vcode:String){
        viewModelScope.launch {
            val result = ServerAPI.getInstance().changeMobile(getToke(context),newMobile,vcode)
            log("号码修改结果为: ... ${result.success},${result.message},${result.data}")
            observeChangeUserInfoResult.postValue(result)
        }
    }
}