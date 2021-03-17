package com.wlj.sate7weather.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.wlj.sate7weather.bean.WeatherAlarmBean

@BindingAdapter("notice__detail_title")
fun noticeDetailTitle(textView: TextView,bean: WeatherAlarmBean){
    textView.text = bean.headline.split("[")[0]
}
@BindingAdapter("notice__detail_level")
fun noticeDetailLevel(textView: TextView,bean: WeatherAlarmBean){
    val levelIndex = bean.headline.indexOf("[") + 1
    val len = bean.headline.length
    val level = bean.headline.substring(levelIndex,len - 1)
    textView.text = level
}
@BindingAdapter("notice__detail_time")
fun noticeDetailTime(textView: TextView,bean: WeatherAlarmBean){
    textView.text = "发布时间:" + bean.effective
}
@BindingAdapter("notice__detail_org")
fun noticeDetailOrganization(textView: TextView,bean: WeatherAlarmBean){
    textView.text = "发布单位:中国气象局"
}