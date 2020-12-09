package com.wlj.sate7weather.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun humidityToString(humidity: Double): String {
    val percent = humidity / 1.0
    val nt: NumberFormat = NumberFormat.getPercentInstance() //获取格式化对象
//    nt.minimumFractionDigits = 2 //设置百分数精确度2即保留两位小数
    return nt.format(percent)
}

fun currentDate(): String {
    return SimpleDateFormat("MM月d日EEEE", Locale.CHINA).format(Date())
}

fun main() {
    val rr = humidityToString(0.01)
    println("main test ... $rr")
    println("202009132120".substring(8,10))
}