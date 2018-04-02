package com.jimij.jianshu

import android.app.Application
import android.content.Context
import android.util.Log
import com.mobile.utils.Utils

import kotlin.properties.Delegates

/**
 * Created by jimiji on 2018/3/31.
 */
class App : Application() {

    companion object {
        var ctx : Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        ctx = this
    }
}