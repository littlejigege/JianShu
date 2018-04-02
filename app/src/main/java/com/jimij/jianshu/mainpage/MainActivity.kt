package com.jimij.jianshu.mainpage


import android.app.Activity
import android.app.Service
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityOptionsCompat
import com.jimij.jianshu.R
import com.jimij.jianshu.common.BaseActivity

import com.jimij.jianshu.server.HttpServerService
import com.jimij.jianshu.utils.createSafeTransitionParticipants
import com.mobile.utils.*
import com.mobile.utils.permission.Permission
import com.taobao.sophix.SophixManager
import com.weechan.httpserver.httpserver.HttpServerBuilder
import com.weechan.httpserver.httpserver.uitls.getHostIp

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    companion object {
        internal fun transitionTo(ctx: Activity) {
            val pairs = createSafeTransitionParticipants(ctx, true)
            val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ctx, *pairs)
            ctx.startActivity(Intent(ctx, MainActivity::class.java), transitionActivityOptions.toBundle())
        }
    }

    lateinit var viewModel: MainViewModel
    //遥控器
    lateinit var mServerController: HttpServerService.ServiceController

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mServerController = service as HttpServerService.ServiceController
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        buildEnterTransition()
        Permission.STORAGE.doAfterGet(this) {
            bindService(Intent(this, HttpServerService::class.java), connection, Service.BIND_AUTO_CREATE)
        }

        textView.text = "${getHostIp()} 8080"
        btnOpenServer.setOnClickListener {
            progressBar.visiable()
            doAfter(2000) {
                progressBar.invisiable()
                textView.visiable()

            }
            mServerController.start()
        }
    }

}
