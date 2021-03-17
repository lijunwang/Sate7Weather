package com.wlj.sate7weather.fragments.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wlj.sate7weather.AboutMeActivity
import com.wlj.sate7weather.R
import com.wlj.sate7weather.utils.log
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.fragment_industry.*

class IndustrySettingFragment:BaseFragment(){
    private val tagList:List<String> = listOf("水利工程","电力工程","军事","旅游探险","应急救援","海洋渔业","其他")
    private var selectedTag = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_industry,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.title = "行业设置"
        tagFlow.adapter = object :TagAdapter<String>(tagList){
            override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
                val textView:TextView =
                    LayoutInflater.from(context).inflate(R.layout.type_tag,tagFlow,false) as TextView
                textView.text = t
                return textView
            }

        }

        tagFlow.setOnTagClickListener { view, position, parent ->
            val tagStringList = tagFlow.selectedList.map {
                tagList[it]
            }
            if(tagStringList.isNotEmpty()){
                log("选中的标签:$tagStringList")
                selectedTag =  tagStringList[0]
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("TypeSettingFragment onDestroy 22....")
        (activity as AboutMeActivity).industryChangeListener.value = selectedTag
    }
}