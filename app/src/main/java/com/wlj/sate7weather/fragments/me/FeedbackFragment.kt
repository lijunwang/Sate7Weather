package com.wlj.sate7weather.fragments.me

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.wlj.sate7weather.R
import com.wlj.sate7weather.adapters.AddPictureClickListener
import com.wlj.sate7weather.adapters.MAX_SIZE
import com.wlj.sate7weather.adapters.PicturesAdapter
import com.wlj.sate7weather.utils.GlideEngine
import com.wlj.sate7weather.utils.log
import com.wlj.sate7weather.utils.toEditable
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment :BaseFragment(){
    private  var picturesAdapter:PicturesAdapter? = null
    private val pictureList = ArrayList<LocalMedia?>()
    private val alertDialog by lazy {
        AlertDialog.Builder(context)
            .setView(LayoutInflater.from(context).inflate(R.layout.feedback_success,null,false))
            .create()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedbackET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                feedbackCount.text = "${s?.length}/300"
                if (s != null && s.length >= 300) {
                    Toast.makeText(context, "超出字数", Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        log("FeedbackFragment onViewCreated ...")
    }
    private var clicked = false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        log("FeedbackFragment onActivityCreated ...")
        activity?.title = "意见反馈"
        picturesAdapter = PicturesAdapter(context!!,pictureList)
        picturesAdapter!!.setAddClicked(object :AddPictureClickListener{
            override fun onAddPictureClicked() {
                selectPicture()
            }
        })

        feedbackCommit.setOnClickListener {
            val content = feedbackET.text.trim().toString()
            if(content.isNotEmpty()){
                settingViewModel.feedbackTextImage(context!!,content)
                clicked = true
            }else{
                Toast.makeText(context!!, "请填写反馈信息", Toast.LENGTH_SHORT).show()
            }
        }

        settingViewModel.feedbackResult.observe(this){
            if (it && clicked){
//                Toast.makeText(context, "反馈成功", Toast.LENGTH_SHORT).show()
                alertDialog.show()
                aboutMeActivity.handler.postDelayed({
                    alertDialog.dismiss()
                },2000)
                feedbackET.text = "".toEditable()
//                activity!!.supportFragmentManager.popBackStack()
            }else if(clicked){
                Toast.makeText(context, "反馈失败...", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        log("FeedbackFragment onResume ...")
        feedbackRecycler.adapter = picturesAdapter
    }
    private fun selectPicture(){
        AndPermission.with(this).runtime().permission(Permission.CAMERA).start()
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())
//            .isWeChatStyle(false)
            .maxSelectNum(MAX_SIZE - picturesAdapter!!.itemCount)
            .imageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: List<LocalMedia?>) {
                    log("onResult ... $result")
                    picturesAdapter!!.update(result,true)
                }

                override fun onCancel() {
                    log("onCancel ... ")
                }
            })
    }

}
