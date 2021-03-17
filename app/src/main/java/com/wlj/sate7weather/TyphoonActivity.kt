package com.wlj.sate7weather

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.jaeger.library.StatusBarUtil
import com.wlj.sate7weather.bean.TyphoonDetailInfo
import com.wlj.sate7weather.databinding.ActivityTyphoonBinding
import com.wlj.sate7weather.utils.drawLine
import com.wlj.sate7weather.utils.logTyphoon
import com.wlj.sate7weather.viewmodel.TyphoonViewModel
import kotlinx.android.synthetic.main.activity_typhoon.*
import java.text.SimpleDateFormat
import kotlin.math.abs


class TyphoonActivity : AppCompatActivity(),SensorEventListener {
    private lateinit var typhoonViewModel: TyphoonViewModel
    private lateinit var sensorManager: SensorManager
    private lateinit var binding: ActivityTyphoonBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_typhoon)
        binding = DataBindingUtil.setContentView<ActivityTyphoonBinding>(this,R.layout.activity_typhoon)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        typhoonViewModel = ViewModelProviders.of(this).get(TyphoonViewModel::class.java)
        observeTyphoonData()
        typhoonViewModel.getTyphoonList(this,SimpleDateFormat("yyyyMMddHHmm").format(System.currentTimeMillis()))
        StatusBarUtil.setColor(this,resources.getColor(R.color.typhoon_bg),0)
        title = "大风台风"
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setHomeButtonEnabled(true)
        initMap()
        setUpLocation()
        clicks()
    }

    private fun clicks(){
        positionCurrent.setOnClickListener{
            logTyphoon("go to current position ... ")
            //Move to last position ...
            mLastPosition?.let {
                val mapStatus = MapStatus.Builder().target(LatLng(it.latitude,it.longitude)).build()
                baiduMapView.map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus))
            }
        }
    }



    private fun observeTyphoonData(){
        typhoonViewModel.typhoonList.observe(this){
            logTyphoon("查询到了台风列表 ... ${it.size}")
            if(it.isNotEmpty()){
                typhoonEmptyTv.visibility = View.GONE
                //TODO 解析条件过滤，应该查询当前位置100Km范围内的台风
                typhoonViewModel.getTyphoonDetailInfo(this,it[0].typhoonNo)
            }
        }

        typhoonViewModel.typhoonDetailInfo.observe(this){
            logTyphoon("查询到了台风详情 ... ${it.size}")
            //step 1: 更新台风详情
            binding.typhoon = it[0]
            //step 2: 开始绘制台风路径
            drawLine(baiduMapView.map,it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initMap(){
        baiduMapView.map.isMyLocationEnabled = true
        baiduMapView.showZoomControls(false)
        val myLocationConfiguration =
            MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null)
        baiduMapView.map.setMyLocationConfiguration(myLocationConfiguration)
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_UI
        )

        baiduMapView.map.setOnMarkerClickListener { marker ->
            val detailInfo = marker.extraInfo.getParcelable<TyphoonDetailInfo>("TyphoonDetailInfo")
            detailInfo?.let {
                binding.typhoon = it
                logTyphoon("MarkerClick ... $it")
            }
            true
        }
    }
    private fun setUpLocation(){
        val locationClient = LocationClient(this)
        locationClient.registerLocationListener(LocationListener())
        val locationClientOption = LocationClientOption()
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll")
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(1000)
        //可选，设置是否使用gps，默认false
        //可选，设置是否使用gps，默认false
        locationClientOption.isOpenGps = true
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        locationClientOption.setIsNeedAddress(true)
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation
        locationClientOption.setIsNeedLocationPoiList(true)
        // 设置定位参数
        // 设置定位参数
        locationClient.locOption = locationClientOption
        // 开启定位
        // 开启定位
        locationClient.start()
    }
    override fun onResume() {
        super.onResume()
        baiduMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        baiduMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        baiduMapView.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x: Float? = event?.values?.get(SensorManager.DATA_X)
        x?.let {
            if (abs(x - lastX) > 1.0) {
                mCurrentDirection = x.toFloat()
                myLocationData = MyLocationData.Builder()
                    .accuracy(mCurrentAccuracy)
                    .direction(mCurrentDirection)
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon).build()
                baiduMapView.map.setMyLocationData(myLocationData)
            }
            lastX = x.toFloat()
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    private var isFirstLoc = true
    private var lastX = 0.0f
    private var mCurrentLat:Double = 0.0
    private var mCurrentLon:Double = 0.0
    private var mCurrentDirection:Float = 0.0f
    private var mCurrentAccuracy:Float = 0.0f
    private var myLocationData:MyLocationData? = null
    private var mLastPosition:BDLocation? = null
    private inner class LocationListener: BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
//            logTyphoon("位置更新 ... " + location?.addrStr)
            if (location == null || baiduMapView.map == null) {
                return
            }
            mLastPosition = location
            mCurrentLat = location.latitude
            mCurrentLon = location.longitude
            mCurrentAccuracy = location.radius
            myLocationData = MyLocationData.Builder()
                .direction(mCurrentDirection) // 此处设置开发者获取到的方向信息，顺时针0-360
                .latitude(mCurrentLat)
                .longitude(mCurrentLon)
                .build()
            baiduMapView.map.setMyLocationData(myLocationData)
            if (location.locType == BDLocation.TypeGpsLocation || location.locType == BDLocation.TypeNetWorkLocation || location.locType == BDLocation.TypeOffLineLocation) {
                if (isFirstLoc) {
                    isFirstLoc = false
                    val ll = LatLng(location.latitude, location.longitude)
                    val builder = MapStatus.Builder()
//                    builder.target(ll).zoom(18.0f)
                    baiduMapView.map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
                }
            }
        }
    }

}