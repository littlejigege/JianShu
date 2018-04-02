package com.jimij.jianshu.server

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.weechan.httpserver.httpserver.HttpServerBuilder


/**
 * Created by jimiji on 2018/3/31.
 */
class HttpServerService : Service() {

    private val mServer by lazy {
        HttpServerBuilder
                .handlerPackage("server.handler")
                .with(this)
                .port(8080)
                .getHttpServer()
    }

    private val mController by lazy {
        object : ServiceController() {
            override fun start() {
                mServer.start()
            }

            override fun stop() {
                mServer.stop()
            }

        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return mController
    }

    //遥控器
    abstract class ServiceController : Binder() {
        abstract fun start()
        abstract fun stop()
    }
}