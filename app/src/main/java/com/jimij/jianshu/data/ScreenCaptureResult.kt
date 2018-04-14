package com.jimij.jianshu.data

import android.graphics.Bitmap

/**
 * Created by 铖哥 on 2018/4/13.
 */
data class ScreenCaptureResult(var code : Int, var bitmap: Bitmap?){
    companion object {
        val REQUEST = 1
        val RESPONSE = 2
    }

}