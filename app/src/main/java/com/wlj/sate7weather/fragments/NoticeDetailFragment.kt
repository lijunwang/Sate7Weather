package com.wlj.sate7weather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wlj.sate7weather.R
import com.wlj.sate7weather.bean.WeatherAlarmBean
import com.wlj.sate7weather.databinding.NoticeDetailBinding

class NoticeDetailFragment : Fragment() {
    private var binding:NoticeDetailBinding? = null
    private var detailBean:WeatherAlarmBean? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<NoticeDetailBinding>(LayoutInflater.from(context),R.layout.notice_detail,container,false)
        detailBean?.let {
            binding!!.noticeDetailBean = it
        }
        return  binding!!.root
    }

    fun setData(data:WeatherAlarmBean){
        binding?.noticeDetailBean = data
        detailBean = data
    }
}