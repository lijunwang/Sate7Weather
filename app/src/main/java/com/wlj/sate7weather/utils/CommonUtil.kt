package com.wlj.sate7weather.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import com.wlj.sate7weather.REQ_CODE_PERMISSION
import com.yanzhenjie.permission.AndPermission
fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
fun toSettingRequestPermission(context: Activity){
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.parse("package:${context.packageName}")
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    context.startActivityForResult(intent, REQ_CODE_PERMISSION)
}

fun andSetting(context: Activity,requestCode:Int){
    AndPermission.with(context)
                        .runtime()
                        .setting()
                        .start(requestCode)
}