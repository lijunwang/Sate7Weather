package com.wlj.sate7weather

import android.app.Application
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.pgyersdk.crash.PgyCrashManager
import com.tencent.bugly.crashreport.CrashReport

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //app_id:b06f807bea
        //App Key:427cd4af-37c9-4735-b0da-8f4ac75d9386
        CrashReport.initCrashReport(baseContext, "b06f807bea", false)
        PgyCrashManager.register()

        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}