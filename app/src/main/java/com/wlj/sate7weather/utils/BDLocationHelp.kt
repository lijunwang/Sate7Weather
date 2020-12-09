package com.wlj.sate7weather.utils

import android.content.Context
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption

class BDLocationHelp {
    private var locationClient:LocationClient
    private val locationOption: LocationClientOption = LocationClientOption()
    private var myLocationListener:MyLocationListener
    private constructor(context: Context){
        locationClient = LocationClient(context)
        myLocationListener = MyLocationListener(this)
        locationClient.registerLocationListener(myLocationListener)
        initOption()
    }

    private fun initOption(){
        log("initOption ...")
        //声明LocationClient类实例并配置定位参数
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02")
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
//        locationOption.setScanSpan(1000)
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false)
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.isLocationNotify = false
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false)
        //可选，默认false，设置是否开启Gps定位
        locationOption.isOpenGps = true
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode()
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
//        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT)
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.locOption = locationOption
    }
    companion object{
        private var INSTANCE:BDLocationHelp? = null
        fun getInstance(context: Context): BDLocationHelp {
            if (INSTANCE == null){
                synchronized(this){
                    if(INSTANCE == null){
                        INSTANCE = BDLocationHelp(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }
    fun startLocate(once: Boolean,actionAfterLocate: (location: BDLocation?) -> Unit) {
        log("start locate $once")
        myLocationListener.setAction(once,actionAfterLocate)
        locationClient.registerLocationListener(myLocationListener)
        locationClient.start()
    }

    fun stopLocate(){
        log("stop locate")
        locationClient.unRegisterLocationListener(myLocationListener)
        locationClient.stop()
        INSTANCE = null
    }
}
class MyLocationListener(private val help: BDLocationHelp): BDAbstractLocationListener() {
    private lateinit var action:(location: BDLocation?)->Unit
    private var once: Boolean = false
    fun setAction(once:Boolean,doAction: (location: BDLocation?) -> Unit){
        action = doAction
        this.once = once
    }
    override fun onReceiveLocation(location: BDLocation?) {
        log("onReceiveLocation ... ${location?.addrStr}")
        location?.let {
            action?.invoke(location)
        }
        if (once && location!= null){
            help.stopLocate()
        }
    }
}

