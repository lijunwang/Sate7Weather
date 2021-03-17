package com.wlj.sate7weather

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.wlj.sate7weather.bean.UserInfoBean
import com.wlj.sate7weather.fragments.me.AboutMeFragment
import com.wlj.sate7weather.viewmodel.MeSettingViewModel

class AboutMeActivity : AppCompatActivity() {
    internal val industryChangeListener: MutableLiveData<String> = MutableLiveData()
    internal val settingViewMode by lazy {
        ViewModelProviders.of(this).get(MeSettingViewModel::class.java)
    }
    internal val handler by lazy {
        object :Handler(){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
            }
        }
    }
    internal var userInfoBean: UserInfoBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me)
        setSupportActionBar(findViewById(R.id.aboutMeToolBar))
        title = ""
        supportActionBar?.setHomeButtonEnabled(true)

        supportFragmentManager.beginTransaction().add(R.id.aboutMeContainer, AboutMeFragment()).commit()
        settingViewMode.getUserInfo(this)

        //fuck server 1
        settingViewMode.observeGetUserInfoBean.observe(this){
            if(it !== null && it.success){
                userInfoBean = it.data
            }
        }
        //fuck server 2
        settingViewMode.observeChangeUserInfoResult.observe(this){
            if(it !== null && it.success){
                userInfoBean = it.data
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}