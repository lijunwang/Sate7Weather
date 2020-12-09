package com.wlj.sate7weather

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jaeger.library.StatusBarUtil
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.pgyersdk.update.javabean.AppBean
import com.wlj.sate7weather.bean.HourlyWeather
import com.wlj.sate7weather.db.WeatherDB
import com.wlj.sate7weather.db.WeatherDao
import com.wlj.sate7weather.utils.*
import com.wlj.sate7weather.view.HourlyWeatherInfoAdapter
import com.wlj.sate7weather.viewmodel.WeatherInfoViewModel
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_title_bar.*


class MainActivity : AppCompatActivity(){
    private lateinit var weatherViewModel: WeatherInfoViewModel
    private lateinit var weatherDao: WeatherDao
    private lateinit var adapter: HourlyWeatherInfoAdapter
    private lateinit var bdLocationHelper:BDLocationHelp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherViewModel =  ViewModelProviders.of(this).get(WeatherInfoViewModel::class.java)
        bdLocationHelper = BDLocationHelp.getInstance(this)
        weatherDao = WeatherDB.getInstance(this).weatherDao()
        adapter = HourlyWeatherInfoAdapter(this, ArrayList())
        main_24_weather_recycler_view.adapter = adapter
        log("onCreate ... ")
        //Jetpack数据驱动UI第一步: 监听天气请求数据
        observeWeatherChange()
        //请求定位以获取经纬度信息
        requestLocationPermission()
        StatusBarUtil.setTranslucentForImageView(this, 0, findViewById(R.id.view_need_offset))
        appUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        PgyUpdateManager.unRegister()
    }
    private fun requestLocationPermission(){
        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.LOCATION,Permission.Group.STORAGE)
            .onGranted(Action { it ->
                log("获得的权限：$it")
                bdLocationHelper.startLocate(false) { location ->
                    //location success
                    logLiveWeather("get location result: " + location?.addrStr)
                    location?.let {
                        location
                        logLiveWeather("get location success to get live data by longitude-latitude " + it.district)
                        //Jetpack数据驱动UI第二步: 请求数据
                        weatherViewModel.getLiveWeather(
                            "" + location.longitude,
                            "" + location.latitude
                        )
                        weatherViewModel.getAQI("" + location.longitude, "" + location.latitude)
                        weatherViewModel.getFuture24Weather(location, weatherDao)
                        weatherViewModel.getWeatherRange(location.city)
                        bdLocationHelper.stopLocate()//防止重复拉取数据
                        //更新状态栏
                        main_titlebar_title.text = it.district
                    }
                }
            })
            .onDenied(Action {
                log("被拒绝的权限：$it")
                if (AndPermission.hasAlwaysDeniedPermission(this, it)) {
                    //show dialog
                }
            })
            .start()
    }
    private fun observeWeatherChange(){
        //实时天气详情变化
        weatherViewModel.liveWeatherInfo.observe(this,
            {
                logLiveWeather("got live data to update UI $it")
                it?.apply {
                    main_current_temperature.text = "" + temperature//当前温度
                    main_current_temperature_sensible.text = resources.getString(
                        R.string.temperature_sensible,
                        apparentTemperature
                    )//体感温度
                    //TODO 温度范围、当前日期、风速风向
                    main_wet.text = humidityToString(humidity)//湿度
                    main_wind.text = "$speedDesc $directionDesc"//风速风向
                }
            })
        //空气质量变化
        weatherViewModel.aqi.observe(this,
            {
                //Jetpack数据驱动UI第三步: 得到数据后更新UI
                log("AQI onChange ...$it")
                it?.let {
                    main_air.text = it.aqiLevel
                }
            })
        //逐时天气变化
        weatherViewModel.future24Weather.observe(this,
            {
                logHourlyWeather("get hourly weather.db.db success and to update UI ${it.size}")
                main_current_date.text = currentDate()
                hourly_2.text = resources.getString(
                    R.string.hourly_temperature,
                    it[0].temperature.toInt(),
                    it[1].temperature.toInt()
                )
                hourly_12.text = resources.getString(
                    R.string.hourly_temperature,
                    it[10].temperature.toInt(),
                    it[11].temperature.toInt()
                )
                hourly_24.text = resources.getString(
                    R.string.hourly_temperature,
                    it[22].temperature.toInt(),
                    it[23].temperature.toInt()
                )
                //更新天气图标
                hourly2_weather_type.setImageResource(it[1].getWeatherTypeResId(this))
                hourly12_weather_type.setImageResource(it[11].getWeatherTypeResId(this))
                hourly24_weather_type.setImageResource(it[23].getWeatherTypeResId(this))
                //更新RecyclerView;
                adapter.update(it as ArrayList<HourlyWeather>, false)
            })
        weatherViewModel.temperatureRange.observe(this, {
            log("getWeatherRange update UI ... $it")
            it?.let {
                main_temperature_range.text = resources.getString(
                    R.string.hourly_temperature,
                    it[0],
                    it[1]
                )
            }
        })
    }

    private fun appUpdate(){
        //更新检测
        logPgy("appUpdate ....")
        PgyUpdateManager.Builder()
            .setDeleteHistroyApk(true)
            .setUpdateManagerListener(object : UpdateManagerListener {
                override fun onNoUpdateAvailable() {
                    logPgy("最新版本...")
                }

                override fun onUpdateAvailable(appBean: AppBean?) {
                    logPgy("有新版本啦...${appBean?.releaseNote}")
                    appBean?.let {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(R.string.pgy_update_title)
                            .setMessage(resources.getString(R.string.pgy_update_msg,appBean.releaseNote))
                            .setNegativeButton(R.string.cancel
                            ) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.ok){
                                    _,_ ->
                                PgyUpdateManager.downLoadApk(appBean.downloadURL)
                            }
                            .show()
                    }
                }

                override fun checkUpdateFailed(exception: Exception?) {
                    logPgy("检测最新版本失败...")
                }
            })
            /*.setDownloadFileListener(object :DownloadFileListener{
                override fun downloadFailed() {
                }

                override fun downloadSuccessful(file: File?) {
                }

                override fun onProgressUpdate(vararg progress: Int?) {
                }
            })*/
            .register()
    }
}