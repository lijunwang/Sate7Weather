package com.wlj.sate7weather

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Html
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.wlj.sate7weather.utils.log
import com.wlj.sate7weather.utils.saveToken
import com.wlj.sate7weather.viewmodel.LoginViewModel
import com.wynsbin.vciv.VerificationCodeInputView
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    lateinit var loginViewMode:LoginViewModel
    private val MSG_COUNT_DOWN = 0x10
    private var COUNT_CURRENT = 60
    private var verifyCode:String = ""
    private val handler:Handler = object :Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                MSG_COUNT_DOWN -> {
                    if (COUNT_CURRENT > 0) {
                        loginGetVCode.text = "${COUNT_CURRENT}s后重试"
                        sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000)
                        COUNT_CURRENT--
                        loginGetVCode.isClickable = false
                    } else {
                        loginGetVCode.text = "获取验证码"
                        COUNT_CURRENT = 60
                        loginGetVCode.isClickable = true
                    }

                }
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate ... ")
        setContentView(R.layout.activity_login)
        initToolBar()
        val str = "登陆即同意<font color = '#2077F7'> 用户协议</font> 以及 <font color = '#2077F7'> 隐私政策</font>"
        loginNotice.text = Html.fromHtml(str)
        loginViewMode = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        clicks()
        observers()
    }

    private fun clicks(){
        loginInputVCode.setOnInputListener(object : VerificationCodeInputView.OnInputListener {
            override fun onComplete(code: String?) {
                login.isEnabled = true
                if (code != null) {
                    verifyCode = code
                }
            }

            override fun onInput() {
                login.isEnabled = false
            }
        })
        loginGetVCode.setOnClickListener{
            loginViewMode.getVerifyCode("18682145730")
        }
        login.setOnClickListener{
            log("登陆 ... ")
            val number = loginPhoneNum.text.toString().trim()
            if(number.isNotEmpty() && verifyCode.isNotEmpty()){
                loginViewMode.loginByVerifyCode(number, verifyCode)
            }
        }
    }

    private fun observers(){
        loginViewMode.sendVCodeResult.observe(this){
            if(it){
                handler.removeMessages(MSG_COUNT_DOWN)
                handler.sendEmptyMessage(MSG_COUNT_DOWN)
            }
        }
        loginViewMode.loginToken.observe(this){data ->
            log("loginActivity token == $data")
            if(data != null && data.success && data.data.isNotEmpty()){
                //save token,go to MainActivity
                saveToken(this,data.data)
//                val intent = Intent(this,MainActivity::class.java)
                val intent = Intent(this,AboutMeActivity::class.java)
                intent.putExtra("fromLogin",true)
                startActivity(intent)
                finish()
            }else if(data!= null){
                Toast.makeText(this, "失败:${data.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initToolBar(){
        setSupportActionBar(findViewById(R.id.loginToolBar))
        supportActionBar?.setHomeButtonEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeMessages(MSG_COUNT_DOWN)
    }
}

