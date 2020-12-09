package com.wlj.sate7weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val DB_NAME = "weather.db";
@Database(exportSchema = false,version = 2, entities = [WeatherRegion::class, WeatherType::class])
abstract class WeatherDB : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    companion object {
        private var INSTANCE: WeatherDB? = null
        fun getInstance(context: Context): WeatherDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, WeatherDB::class.java, DB_NAME)
                    .createFromAsset(DB_NAME)
                    .build()
            }
        }
    }
}