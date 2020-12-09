package com.wlj.sate7weather.db

import android.content.Context
import android.os.Environment
import com.wlj.sate7weather.utils.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class CopyDB2SD {
    companion object{
        fun save2SD(context:Context){
            GlobalScope.launch {
                var dbFile = context.getDatabasePath(DB_NAME);
                dbFile?.let {
                    log("file name:" + dbFile.absolutePath + "," + dbFile.length())
                    var saveTo = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), DB_NAME)
                    var copyResult = dbFile.copyTo(saveTo,true)
                    log("copyResult == " + copyResult.absolutePath + "," + copyResult.exists())
                }
            }
        }

        fun testLocalDB(context: Context){
            GlobalScope.launch {
                var allWeatherType = WeatherDB.getInstance(context).weatherDao().queryAllWeatherType()
                var allWeatherRegion = WeatherDB.getInstance(context).weatherDao().queryAllWeatherRegion()
                log("all type size == ${allWeatherType.size},regionSize = ${allWeatherRegion.size}")
                log("all allWeatherType == $allWeatherType")
                log("-----------------------------------------")
                log("all allWeatherRegion == $allWeatherRegion")
            }
        }
    }
}