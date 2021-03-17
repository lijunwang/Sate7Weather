package com.wlj.sate7weather.utils

import android.content.Context
const val TOKEN_KEY = "token"
fun saveToken(context: Context, token:String) {
    log("saveToken==$token")
    context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).edit().putString(
        TOKEN_KEY,"qx $token").apply()//注意空格
}

fun getToke(context: Context):String{
    return context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getString(TOKEN_KEY,"")!!
}
fun cleanToken(context: Context){
    log("cleanToken ...")
    context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).edit().putString(
        TOKEN_KEY,"").apply()//注意空格
}