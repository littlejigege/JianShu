package com.jimij.jianshu.server

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.example.androidservice.httpserver.reslover.reslovebean.RequestMessage
import com.jimij.jianshu.server.handler.*
import com.weechan.httpserver.httpserver.HttpServerBuilder
import java.net.Socket


/**
 * Created by jimiji on 2018/3/31.
 */
class HttpServerService : Service() {

    val whiteList = mutableListOf<String>()
    var intercept: ((RequestMessage) -> Boolean)? = null

    private val mServer by lazy {
        val server = HttpServerBuilder
                .with(this)
                .port(8080)
                .intercept { message ->
                    if(whiteList.contains(message.ip)) return@intercept false
                    intercept?.invoke(message)?:false
                }
                .getHttpServer()

        server.addHandler(ApplicationHandler::class.java)
        server.addHandler(CaptureHandler::class.java)
        server.addHandler(DeviceInfoHandler::class.java)
        server.addHandler(Download::class.java)
        server.addHandler(FileHandler::class.java)
        server.addHandler(MainHandler::class.java)
        server.addHandler(MediaHandler::class.java)
        server.addHandler(MyHttpHandler::class.java)
        server.addHandler(ThumbnailHandler::class.java)
        server.addHandler(UploadHandler::class.java)

        server
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1,NotificationCompat.Builder(this,"service").build())
    }

    private val mController = object : ServiceController() {
        override fun clearWhiter() {
            whiteList.clear()
        }

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
        abstract fun clearWhiter()
        abstract fun onIntercept(listener: (RequestMessage) -> Boolean)
    }
}