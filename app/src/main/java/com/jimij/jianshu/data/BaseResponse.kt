package com.jimij.jianshu.data

import com.google.gson.Gson

/**
 * Created by 铖哥 on 2018/4/12.
 */
data class BaseResponse(val code : Int , val errorMsg : String)

fun BaseResponse.json(): String {
    return Gson().toJson(this)
}