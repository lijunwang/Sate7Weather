package com.wlj.sate7weather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWeatherRegion(region: WeatherRegion):Long
    @Query("SELECT regionId FROM weatherRegion where cityName like :cityName")
    abstract fun queryWeatherRegionId(cityName:String):Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWeatherType(type: WeatherType):Long
    @Query("SELECT nameCN FROM weatherType where weatherType = :type")
    abstract fun queryWeatherNameCN(type:Int):String
    @Query("SELECT nameEn FROM weatherType where weatherType = :type")
    abstract fun queryWeatherNameEn(type:Int):String
    @Query("SELECT * FROM weatherType")
    abstract fun queryAllWeatherType():List<WeatherType>
    @Query("SELECT * FROM weatherRegion")
    abstract fun queryAllWeatherRegion():List<WeatherRegion>
}