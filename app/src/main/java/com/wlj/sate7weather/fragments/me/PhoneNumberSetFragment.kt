package com.wlj.sate7weather.fragments.me

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.wlj.sate7weather.R
import com.wlj.sate7weather.utils.isMobileNO
import com.wlj.sate7weather.utils.log
import com.wlj.sate7weather.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_set_phone.*

class PhoneNumberSetFragment:BaseFragment() {
    private val loginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }
    private var clicked = false
    private val MSG_COUNT_DOWN = 0x10
    private var COUNT_CURRENT = 60
    private var verifyCode:String = ""
    private val handler: Handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                MSG_COUNT_DOWN -> {
                    if (COUNT_CURRENT > 0) {
                        sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000)
                        COUNT_CURRENT--
                        if(newSendVCode != null){
                            newSendVCode.text = "${COUNT_CURRENT}s后重试"
                            newSendVCode.isClickable = false
                        }
                    } else {
                        newSendVCode.text = "号码验证"
                        COUNT_CURRENT = 60
                        newSendVCode.isClickable = true
                    }

                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_phone,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.title = "更换手机"
        settingViewModel.observeGetUserInfoBean.observe(this){
            if(it != null && it.data.mobile.isNotEmpty()){
                setPhoneReal.text = cryptographic(it.data.mobile)
            }
        }

        newSendVCode.setOnClickListener {
            val newMobile = newMobileET.text.trim().toString()
            if(newMobile.isEmpty()){
                Toast.makeText(context, "号码不能为空...", Toast.LENGTH_SHORT).show()
            }else if(!newMobile.isMobileNO()){
                Toast.makeText(context, "请检查电话号码格式...", Toast.LENGTH_SHORT).show()
            }else{
                handler.removeMessages(MSG_COUNT_DOWN)
                handler.sendEmptyMessage(MSG_COUNT_DOWN)
                newSendVCode.isClickable = false
                loginViewModel.getVerifyCode(newMobile)
            }
        }

        //修改号码
        sureModify.setOnClickListener {
            val newMobile = newMobileET.text.trim().toString()
            val vcode = vCodeET.text.trim().toString()
            if (newMobile.isEmpty() ) {
                Toast.makeText(context, "请输入电话号码", Toast.LENGTH_SHORT).show()
            }else if(vcode.isEmpty()){
                Toast.makeText(context, "请输入正确的验证码", Toast.LENGTH_SHORT).show()
            }else if(!newMobile.isMobileNO()){
                Toast.makeText(context, "请输入正确的电话号码", Toast.LENGTH_SHORT).show()
            }else{
                settingViewModel.modifyPhone(context!!, newMobile, vcode)
                clicked = true
            }
        }
        //观察修改结果
        settingViewModel.observeChangeUserInfoResult.observe(this){
             log("修改号码结果观察clicked == $clicked")
             if(it!= null && it.success && clicked){
                 Toast.makeText(context, "号码修改成功", Toast.LENGTH_SHORT).show()
                 aboutMeActivity.supportFragmentManager.popBackStack()
             }else if(it!= null && clicked){
                 Toast.makeText(context, "号码修改失败:${it.message}", Toast.LENGTH_SHORT).show()
             }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeMessages(MSG_COUNT_DOWN)
    }
   private fun cryptographic(mobile:String):String{
        return "${mobile.subSequence(0,3)}****${mobile.subSequence(mobile.length-4,mobile.length)}"
   }
}