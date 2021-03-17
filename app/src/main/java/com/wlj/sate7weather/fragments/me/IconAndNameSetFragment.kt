package com.wlj.sate7weather.fragments.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.wlj.sate7weather.R
import com.wlj.sate7weather.utils.GlideEngine
import com.wlj.sate7weather.utils.log
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.fragment_icon_name.*
import java.io.File

class IconAndNameSetFragment :BaseFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_icon_name,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.title = "个人信息"
        modifyIcon.setOnClickListener{
            selectPicture()
        }

        modifyName.setOnClickListener {
            goTo(NickFragment())
        }
        observers()
    }

    private fun observers(){
        settingViewModel.observeGetUserInfoBean.observe(this){
            if(it!=null && it.success){
                nickNameTv.text = it.data.nickname
                if(it.data!= null && it.data.headerImg.isNotEmpty()){
                    Glide.with(this).load("http://47.104.129.239:8081" + it.data.headerImg).into(meHeadImg)
                }
            }
        }
        //改名成功后回调修改nickName
        settingViewModel.observeChangeUserInfoResult.observe(this){
            if(it!=null && it.success){
                nickNameTv.text = it.data.nickname
            }
        }
    }

    private fun goTo(fragment: Fragment){
        log("go to fragment ... " + fragment.javaClass.name)
        fragment.arguments = Bundle()
        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.aboutMeContainer,
            fragment
        )?.addToBackStack("")?.commit()
    }
    private fun selectPicture(){
        AndPermission.with(this).runtime().permission(Permission.CAMERA).start()
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())
//            .isWeChatStyle(false)
            .maxSelectNum(1)
            .imageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: List<LocalMedia?>) {
                    val path = result[0]?.path
                    val path1 = result[0]?.realPath
                    val path2 = result[0]?.androidQToPath
                    log("onResult head image ... path==$path,realPath==$path1,androidQPath==$path2")
                    if(path!= null){
                        Glide.with(context!!).load(path).into(meHeadImg)
                        settingViewModel.updateHeadImg(context!!,File(path1))
                    }
                }

                override fun onCancel() {
                    log("onCancel ... ")
                }
            })
    }

}