package com.jimij.jianshu


import android.app.Application
import android.content.Context
import com.mobile.utils.Utils
import com.taobao.sophix.SophixManager
import java.io.File
import kotlin.properties.Delegates

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
        Thread.setDefaultUncaughtExceptionHandler({ t, e -> File("/storage/emulated/0/error.log").writeText(e.toString()) })

    }


}