package com.jimij.jianshu.ui.mainpage

import android.app.Service
import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.jimij.jianshu.server.HttpServerService
import com.mobile.utils.doAfter
import com.weechan.httpserver.httpserver.uitls.getHostIp

/**
 * Created by jimiji on 2018/4/2.
 */
class MainPresenter : MainContract.Presenter<MainActivity>, GenericLifecycleObserver {


    var view: MainActivity? = null
    //遥控器
    var mServerController: HttpServerService.ServiceController? = null
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mServerController = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mServerController = service as HttpServerService.ServiceController
            mServerController?.start()
        }

    }

    private fun getView(): MainContract.View? = if (view == null || view!!.lifecycle.currentState != Lifecycle.State.RESUMED) null else view as MainContract.View

    override fun startServer() {
        getView()?.onServerStart()
        if (mServerController == null) {
            view?.bindService(Intent(view, HttpServerService::class.java), connection, Service.BIND_AUTO_CREATE)
        } else {
            mServerController?.start()
        }
        doAfter(2000) {
            getView()?.onServerStarted()
            getView()?.onIpPort(getHostIp()!!, "8080")
        }
    }

    override fun stopServer() {
        getView()?.onServerStop()
        mServerController?.stop()
        getView()?.onServerStopped()
    }

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            view = null
        }
        view = source as MainActivity
    }

}