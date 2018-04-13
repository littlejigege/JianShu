package com.jimij.jianshu.ui.mainpage

import com.jimij.jianshu.common.BaseMVPContract

/**
 * Created by jimiji on 2018/4/2.
 */
interface MainContract {
    interface View : BaseMVPContract.View {
        //ip端口信息获取回调
        fun onIpPort(ip: String, port: String)

        fun onServerStart()
        fun onServerStarted()
        fun onServerStop()
        fun onServerStopped()

        fun requestPermission(ip:String)
    }

    interface Presenter<V : MainContract.View> : BaseMVPContract.Presenter<V> {
        fun startServer()
        fun stopServer()
        fun copyText(text: String)
        fun addWhiter(ip:String)
        fun clearWhiter()
        fun setupNetListener()
    }
}