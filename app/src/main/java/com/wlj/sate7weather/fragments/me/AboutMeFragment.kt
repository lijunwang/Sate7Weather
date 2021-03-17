package com.wlj.sate7weather.fragments.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.wlj.sate7weather.AboutMeActivity
import com.wlj.sate7weather.LoginActivity
import com.wlj.sate7weather.R
import com.wlj.sate7weather.bean.UserInfoBean
import com.wlj.sate7weather.utils.BDLocationHelp
import com.wlj.sate7weather.utils.cleanToken
import com.wlj.sate7weather.utils.log
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.LocateState
import com.zaaach.citypicker.model.LocatedCity
import kotlinx.android.synthetic.main.fragment_me.*


class AboutMeFragment :BaseFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        clicks()
        observers()
    }

    private fun observers(){
        //获取用户信息 结果监听
        settingViewModel.observeGetUserInfoBean.observe(this){
            //获取用户信息成功后 更新界面
            if(it!=null && it.success){
                updateDetailInfo(it.data)
            }
        }
        //修改用户信息 结果监听
        settingViewModel.observeChangeUserInfoResult.observe(this){
            //修改用户信息成功后 更新界面
            if(it!=null && it.success){
                updateDetailInfo(it.data)
            }
        }
        //监听行业设置是否被修改
        (activity as AboutMeActivity).industryChangeListener.observe(this){
            log("行业设置变为 ... $it,${meIndustryTV.text}")
            if(it != null && it.isNotEmpty() && !it.equals(meIndustryTV.text.toString())){
                //行业变了,通知服务器修改
                settingViewModel.changeUserInfo(context!!,address = aboutMeActivity.userInfoBean?.address,industry = it)
            }
        }
    }

    private fun updateDetailInfo(info: UserInfoBean){
        log("updateDetailInfo: ${info.headerImg}")
        if(info!= null && info.headerImg.isNotEmpty()){
            Glide.with(this).load("http://47.104.129.239:8081" + info.headerImg).into(meIcon)
        }
        meName.text = info.username
        if(!info.address.isNullOrEmpty() && !info.industry.isNullOrEmpty()){
            meAddType.text = info.address + " · " + info.industry
        }else if(!info.address.isNullOrEmpty()){
            meAddType.text = info.address
        }else if(!info.industry.isNullOrEmpty()){
            meAddType.text = info.industry
        }else{
            meAddType.text = ""
        }
        meAddressTV.text = info.address
        meIndustryTV.text = info.industry
        mePhoneTV.text = info.mobile
    }

    private fun clicks(){
        //修改用户头像
        meIcon.setOnClickListener {
            goTo(IconAndNameSetFragment())
        }
        //修改用户名
        meName.setOnClickListener {
            goTo(IconAndNameSetFragment())
        }
        //地址设置
        meSetAddress.setOnClickListener{
            showAddressSelector()
        }

        //行业设置
        meSetType.setOnClickListener {
            goTo(IndustrySettingFragment())
        }
        //更换手机
        meSetPhone.setOnClickListener {
            goTo(PhoneNumberSetFragment())
        }
        //关于我们
        meAboutUs.setOnClickListener{
            log("go to Sate7Fragment")
            goTo(Sate7Fragment())
        }
        //意见反馈
        meFeedback.setOnClickListener {
            goTo(FeedbackFragment())
        }
        //退出登录
        meExit.setOnClickListener {
            cleanToken(this.context!!)
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun goTo(fragment: Fragment){
        log("go to fragment ... " + fragment.javaClass.name)
        fragment.arguments = Bundle()
        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.aboutMeContainer,
            fragment
        )?.addToBackStack("")?.commit()
    }

    override fun onResume() {
        super.onResume()
        activity!!.title = ""
    }

    private fun showAddressSelector(){
        CityPicker.from(activity)
            .enableAnimation(true)
//            .setAnimationStyle(anim)
            .setLocatedCity(null)
//            .setHotCities(hotCities)
            .setOnPickListener(object : OnPickListener {
                override fun onPick(position: Int, data: City?) {
                    log("onPick $position,${data?.name}")
                    this@AboutMeFragment.settingViewModel.changeUserInfo(context!!,address = data?.name,aboutMeActivity.userInfoBean?.industry)
                }

                override fun onCancel() {
                }

                override fun onLocate() {
                    BDLocationHelp.getInstance(context!!).startLocate(true){
                        log("onLocate AA ...${it?.addrStr},{$it.city},{${it?.province}}")
                        if(it != null){
                            CityPicker.from(activity).locateComplete(LocatedCity(it.city,it.province,it.cityCode),
                                LocateState.SUCCESS)
                        }else{
                            CityPicker.from(activity).locateComplete(LocatedCity("深圳", "广东", "101280601"),
                                LocateState.FAILURE)
                        }
//                        CityPicker.from(activity).locateComplete(LocatedCity("深圳", "广东", "101280601"),LocateState.FAILURE)
                        /*Handler().postDelayed({
                            CityPicker.from(activity).locateComplete(
                                LocatedCity("深圳", "广东", "101280601"),
                                LocateState.SUCCESS
                            )
                        }, 1000)*/
                    }
                }
            })
            .show()
    }
}