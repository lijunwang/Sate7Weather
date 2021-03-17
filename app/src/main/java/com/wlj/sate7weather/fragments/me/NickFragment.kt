package com.wlj.sate7weather.fragments.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.wlj.sate7weather.R
import com.wlj.sate7weather.utils.log
import kotlinx.android.synthetic.main.fragment_nick.*

class NickFragment:BaseFragment() {
    private val nameET by lazy {
        activity!!.findViewById(R.id.nickNameET) as EditText
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nick,container,false)
    }
    private var clicked = false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nameChange.setOnClickListener {
            val content = nameET.text.trim().toString()
            if(content.isNotEmpty()){
                settingViewModel.changeName(context!!,content)
                clicked = true
            }else{
                Toast.makeText(context, "昵称不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        log("NickFragment onActivityCreated ... ")
        settingViewModel.observeChangeUserInfoResult.observe(this){
            if(it == null){
                Toast.makeText(context, "失败...", Toast.LENGTH_SHORT).show()
            }else{
                if(it.success){
                    if(clicked){
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show()
                        activity!!.supportFragmentManager.popBackStack()
                    }
                }else{
                    Toast.makeText(context, "失败:${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}