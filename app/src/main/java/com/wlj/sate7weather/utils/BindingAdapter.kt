package com.wlj.sate7weather.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.wlj.sate7weather.bean.HourlyWeather
import com.wlj.sate7weather.bean.TyphoonDetailInfo

@BindingAdapter("typhoonDetailInfo")
fun typhoonDetailInfo(textView:TextView,info:TyphoonDetailInfo?){
    info?.let { detailInfo->
        var sb = StringBuilder()
        sb.append(detailInfo.typhoonCNName).append("\n")
            .append("时间：").append(convertTime(detailInfo)).append("\n")
//            .append("中心风力").append("").append("\n")
            .append("最大风速：").append(detailInfo.windSpeed + "米/秒").append("\n")
            .append("中心气压：").append(detailInfo.pressure + "hpa").append("\n")
            .append("移动方向：").append(convertDirect(detailInfo)).append("\n")
            .append("移动速度：").append(detailInfo.speed + "公里/小时").append("\n")
        detailInfo.windCircle30kts == "null"
        detailInfo.windCircle30kts?.let {
            sb.append("七级风圈半径：").append(convertRadio30Speed(detailInfo)).append("\n")
        }
        detailInfo.windCircle50kts?.let {
            sb.append("十级风圈半径：").append(convertRadio50Speed(detailInfo)).append("\n")
        }
        detailInfo.windCircle64kts?.let {
            sb.append("十二级风圈半径：").append(convertRadio64Speed(detailInfo)).append("\n")
        }
        textView.text = sb.toString()
    }
}

@BindingAdapter("temperature")
fun temperature(textView:TextView,info: HourlyWeather){
    textView.text = info.temperature + "℃"
}

private fun convertTime(detailInfo:TyphoonDetailInfo): String {
    val sb = java.lang.StringBuilder()
    val time = detailInfo.timeBeijing.split(" ")[0].split("-")
//    logTyphoon("Time:${detailInfo.timeBeijing}")
//    logTyphoon("Time:$time")
    return sb.append(time[0]).append("年").append(time[1]).append("月").append(time[2]).append("日").toString()
}

private fun convertDirect(detailInfo:TyphoonDetailInfo): String {
    //TODO
    return when(detailInfo.direction){
        "NW" -> "西北向"
        "NE" -> "西南向"
        "NW" -> "西北向"
        "NW" -> "西北向"
        "NW" -> "西北向"
        else -> "东北向"
    }
}

private fun convertRadio30Speed(detailInfo:TyphoonDetailInfo): String {
    return detailInfo.windCircle30kts?.replace(";",",") + "公里"
}
private fun convertRadio50Speed(detailInfo:TyphoonDetailInfo): String {
    return detailInfo.windCircle50kts?.replace(";",",") + "公里"
}
private fun convertRadio64Speed(detailInfo:TyphoonDetailInfo): String {
    return detailInfo.windCircle64kts?.replace(";",",") + "公里"
}