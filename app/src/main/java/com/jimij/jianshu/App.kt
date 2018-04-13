package com.jimij.jianshu

import android.app.Application
import android.content.Context
import com.jimij.jianshu.data.MediaRepository
import com.jimij.jianshu.utils.NetCallback
import com.jimij.jianshu.utils.NetListener
import com.mobile.utils.NetworkType

import com.mobile.utils.Utils
import com.mobile.utils.toast


import kotlin.properties.Delegates

import com.taobao.sophix.SophixManager

//import com.uuzuche.lib_zxing.activity.ZXingLibrary


/**
 * Created by jimiji on 2018/3/31.
 */
class App : Application() {

    companion object {
        var ctx: Context by Delegates.notNull()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //初始化hotfix
        if (!BuildConfig.DEBUG) {
            SophixManager.getInstance().setContext(this)
                    .setAppVersion(BuildConfig.VERSION_NAME)
                    .setAesKey(null)
                    .setEnableDebug(true)
                    .initialize()
        }

    }

    override fun onCreate() {
        super.onCreate()
        //每次启动查一次补丁
        if (!BuildConfig.DEBUG) {
            SophixManager.getInstance().queryAndLoadNewPatch()
        }
        //初始化suger
//        ZXingLibrary.initDisplayOpinion(this)
        Utils.init(this)
        ctx = this
    }
}