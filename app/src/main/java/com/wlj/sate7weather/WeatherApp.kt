package com.wlj.sate7weather

import android.app.Application
import com.pgyersdk.crash.PgyCrashManager
import com.tencent.bugly.crashreport.CrashReport

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //app_id:b06f807bea
        //App Key:427cd4af-37c9-4735-b0da-8f4ac75d9386
        CrashReport.initCrashReport(baseContext, "b06f807bea", false);
        PgyCrashManager.register();
    }
}