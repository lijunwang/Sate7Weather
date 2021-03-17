package com.wlj.sate7weather.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.wlj.sate7weather.R
import com.wlj.sate7weather.bean.WeatherAlarmBean
import com.wlj.sate7weather.databinding.NoticeItemBinding
import com.wlj.sate7weather.fragments.NoticeDetailFragment
import com.wlj.sate7weather.utils.log

class NoticeViewHolder(val binding: NoticeItemBinding): RecyclerView.ViewHolder(binding.root) {
}
class NoticeAdapter(private val fragmentManager: FragmentManager, val context:Context, private val noticeList:ArrayList<WeatherAlarmBean>) : RecyclerView.Adapter<NoticeViewHolder>() {
    private val noticeDetailFragment: NoticeDetailFragment = NoticeDetailFragment()
    private val noticeDetailTag = "noticeDetailFragment"
    private var clickListener: ((WeatherAlarmBean) -> Unit)? = null
    fun setOnItemClicked(click:(WeatherAlarmBean)->Unit){
        clickListener = click
    }
    fun update(notices:ArrayList<WeatherAlarmBean>){
        noticeList.clear()
        noticeList.addAll(notices)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        return NoticeViewHolder(DataBindingUtil.inflate<NoticeItemBinding>(LayoutInflater.from(context), R.layout.notice_item,parent,false))
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.binding.noticeBean = noticeList[position]
        holder.binding.apply {
            root.setOnClickListener{
                log("onClick ... ")
                openDetailFragment(noticeList[position])
                /*clickListener?.let {
                    log(noticeList[position].title + ">")
                    it.invoke(noticeList[position])
                }*/
            }
        }
    }

    private fun openDetailFragment(bean: WeatherAlarmBean){
        fragmentManager.beginTransaction().replace(R.id.container,noticeDetailFragment,noticeDetailTag).addToBackStack(null).commit()
        noticeDetailFragment.setData(bean)
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }
}