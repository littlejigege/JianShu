package com.jimij.jianshu.server

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.androidservice.httpserver.reslover.reslovebean.RequestMessage
import com.weechan.httpserver.httpserver.HttpServerBuilder
import java.net.Socket


/**
 * Created by jimiji on 2018/3/31.
 */
class HttpServerService : Service() {

    val whiteList = mutableListOf<String>()
    var intercept: ((RequestMessage) -> Boolean)? = null

    private val mServer by lazy {
        HttpServerBuilder
                .handlerPackage("server.handler")
                .with(this)
                .port(8080)
                .intercept { message ->
                    if(whiteList.contains(message.ip)) return@intercept false
                    intercept?.invoke(message)?:false
                }
                .getHttpServer()
    }

    private val mController = object : ServiceController() {
        override fun addWhiter(ip: String) {
            whiteList.add(ip)
        }

        override fun start() {
            mServer.start()
        }

        override fun stop() {
            mServer.stop()
        }

        override fun onIntercept(listener: (RequestMessage) -> Boolean) {
            intercept = listener
        }

    }


    override fun onBind(intent: Intent?): IBinder {
        return mController
    }

    //遥控器
    abstract class ServiceController : Binder() {
        abstract fun start()
        abstract fun stop()
        abstract fun addWhiter(ip: String)
        abstract fun onIntercept(listener: (RequestMessage) -> Boolean)
    }
}