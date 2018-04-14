package com.jimij.jianshu.server.handler

import android.util.Log
import com.jimij.jianshu.data.ScreenCaptureResult
import com.jimij.jianshu.utils.ScreenCapture
import com.mobile.utils.toBytes
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by 铖哥 on 2018/4/13.
 */

@Http("/capture")
class CaptureHandler : BaseHandler() {

    var block = true

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        EventBus.getDefault().register(this)
        val result = ScreenCaptureResult(ScreenCaptureResult.REQUEST,null)
        EventBus.getDefault().post(result)
        while(block){
            Thread.sleep(100)
        }
        response.write {
            this.write(result.bitmap?.toBytes())
            result.bitmap?.recycle()
        }

        EventBus.getDefault().unregister(this)

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun getCaptureResult(result : ScreenCaptureResult){
        if(result.code == ScreenCaptureResult.RESPONSE){
            block = false
        }
    }
}