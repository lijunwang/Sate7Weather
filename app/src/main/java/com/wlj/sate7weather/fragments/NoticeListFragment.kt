package com.wlj.sate7weather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.wlj.sate7weather.R
import com.wlj.sate7weather.adapters.NoticeAdapter
import com.wlj.sate7weather.bean.WeatherAlarmBean
import com.wlj.sate7weather.utils.log
import com.wlj.sate7weather.viewmodel.NoticeViewMode
import kotlinx.android.synthetic.main.fragment_notice.*

class NoticeListFragment : Fragment() {
    private lateinit var noticeViewMode: NoticeViewMode
    private lateinit var noticeAdapter: NoticeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noticeViewMode = ViewModelProviders.of(this).get(NoticeViewMode::class.java)
    }


    private fun initToolBar(){
        (activity as AppCompatActivity).apply {
            setSupportActionBar(findViewById(R.id.toolBar))
            title = "气象预警"
            supportActionBar?.setHomeButtonEnabled(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        noticeAdapter = NoticeAdapter(activity!!.supportFragmentManager,context!!,ArrayList<WeatherAlarmBean>())
        noticeRecyclerView.adapter = noticeAdapter
        noticeRecyclerView.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        noticeViewMode.noticeInfo.observe(this){
            log("NoticeListFragment observe ... " + it.size)
            noticeAdapter.update(it as ArrayList<WeatherAlarmBean>)
        }
        initToolBar()
        noticeViewMode.getNoticeInfo(context!!)//获取数据
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}