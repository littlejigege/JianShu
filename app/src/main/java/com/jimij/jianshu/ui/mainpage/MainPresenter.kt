package com.jimij.jianshu.ui.mainpage

import android.app.Service
import android.content.*
import android.os.IBinder
import com.jimij.jianshu.App
import com.jimij.jianshu.server.HttpServerService
import com.jimij.jianshu.utils.NetCallback
import com.jimij.jianshu.utils.NetListener
import com.mobile.utils.NetworkType
import com.mobile.utils.doAfter
import com.mobile.utils.inUiThread
import com.mobile.utils.showToast
import com.weechan.httpserver.httpserver.uitls.getHostIp

/**
 * Created by jimiji on 2018/4/2.
 */
class MainPresenter(var view : MainActivity?) : MainContract.Presenter<MainActivity> {
    override fun stopPresenter() {
        view = null
    }

    var isInterceptPass = false
    var isPass = false
    //遥控器
    var mServerController: HttpServerService.ServiceController? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mServerController = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mServerController = service as HttpServerService.ServiceController
            mServerController?.start()
            mServerController?.onIntercept { message ->
                inUiThread { getView()?.requestPermission(message.ip) }
                while (!isInterceptPass) {
                }
                isInterceptPass = false
                !isPass
            }
        }

    }


    override fun setupNetListener() {
        NetListener.addListener(object : NetCallback {
            override fun onChange(flag: NetworkType) {
                mServerController?.start()
                if (flag != NetworkType.NETWORK_WIFI) {
                    view?.onIpPort("非WIFI环境不可用", "")
                } else {
                    view?.onIpPort(getHostIp()!!, "8080")
                }
            }
        })
    }

    override fun clearWhiter() {
        mServerController?.clearWhiter()
    }

    override fun addWhiter(ip: String) {
        mServerController?.addWhiter(ip)

    }


    private fun getView(): MainContract.View? = view

    override fun startServer() {
        setupNetListener()
        getView()?.onServerStart()
        if (mServerController == null) {
            view?.bindService(Intent(view, HttpServerService::class.java), connection, Service.BIND_AUTO_CREATE)
        } else {
            mServerController?.start()
        }
        getView()?.onServerStarted()
        getView()?.onIpPort(getHostIp()!!, "8080")
        doAfter(1000) {
            getView()?.onIpPort(getHostIp()!!, "8080")
        }
    }

    override fun stopServer() {
        getView()?.onServerStop()
        mServerController?.stop()
        getView()?.onServerStopped()
    }





    override fun copyText(text: String) {
        val cm = App.ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.text = text
        showToast("链接已复制到剪切板")
    }

}