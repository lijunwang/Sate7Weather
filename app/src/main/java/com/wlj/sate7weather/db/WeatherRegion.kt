package com.wlj.sate7weather.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wlj.sate7weather.utils.log

@Entity(tableName = "WeatherRegion")
data class WeatherRegion(
    @ColumnInfo(name = "regionId") val regionId: Int,
    @ColumnInfo(name = "cityName") val cityName: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0

    constructor() : this(0,""){
        log("WeatherRegion 默认构造器")
    }
    override fun toString(): String {
        return "WeatherRegion(id=$id,regionId=$regionId, cityName='$cityName')"
    }

}

@Entity(tableName = "WeatherType")
data class WeatherType(
    @ColumnInfo(name = "weatherType") val weatherType: Int,
    @ColumnInfo(name = "nameCN") val nameCN: String,
    @ColumnInfo(name = "nameEn") val nameEn: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0
    override fun toString(): String {
        return "WeatherType(id=$id,weatherType=$weatherType, nameCN='$nameCN', nameEn='$nameEn')"
    }

    constructor():this(0,"",""){
        log("WeatherType 默认构造器")
    }

}