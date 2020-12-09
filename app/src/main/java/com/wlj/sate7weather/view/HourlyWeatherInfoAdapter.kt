package com.wlj.sate7weather.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wlj.sate7weather.bean.HourlyWeather
import com.wlj.sate7weather.databinding.HourlyWeatherInfoBinding
class HourlyWeatherInfoAdapter(private val context:Context, private val hourlyWeatherInfoList: ArrayList<HourlyWeather>) :RecyclerView.Adapter<HourlyInfoHolder>(){
    fun update(hourlyInfoList:ArrayList<HourlyWeather>,append:Boolean){
        if (append){
            hourlyWeatherInfoList.addAll(hourlyInfoList)
        }else{
            hourlyWeatherInfoList.clear()
            hourlyWeatherInfoList.addAll(hourlyInfoList)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyInfoHolder {
        return HourlyInfoHolder(HourlyWeatherInfoBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: HourlyInfoHolder, position: Int) {
        holder.onBind(context,hourlyWeatherInfoList[position])
    }

    override fun getItemCount(): Int {
        return hourlyWeatherInfoList.size
    }
}
class HourlyInfoHolder(private var binding:HourlyWeatherInfoBinding) : RecyclerView.ViewHolder(binding.root){
    fun onBind(context:Context,info:HourlyWeather){
        binding.hourlyWeatherInfo = info
        binding.rvHourlyWeatherType.setImageResource(info.getWeatherTypeResId(context))
    }
}