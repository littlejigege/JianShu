package com.jimij.jianshu

import android.app.Application
import com.mobile.utils.Utils

/**
 * Created by jimiji on 2018/3/31.
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}