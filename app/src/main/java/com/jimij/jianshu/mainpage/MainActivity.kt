package com.jimij.jianshu.mainpage


import android.app.Activity
import android.app.Service
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityOptionsCompat
import com.jimij.jianshu.R
import com.jimij.jianshu.common.BaseActivity

import com.jimij.jianshu.server.HttpServerService
import com.jimij.jianshu.utils.DrawableFitSize
import com.jimij.jianshu.utils.createSafeTransitionParticipants
import com.mobile.utils.*
import com.mobile.utils.permission.Permission
import com.taobao.sophix.SophixManager
import com.weechan.httpserver.httpserver.HttpServerBuilder
import com.weechan.httpserver.httpserver.uitls.getHostIp

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.View {


    //与活动生命周期关联
    private val presenter: MainPresenter = MainPresenter().apply { lifecycle.addObserver(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        Permission.STORAGE.doAfterGet(this) {
            inUiThread { presenter.startServer() }
        }
    }

    private fun initUI() {
        setSupportActionBar(toolBar)
        supportActionBar?.setIcon(R.drawable.logo)
        window.statusBarColor = Color.WHITE
        setStatusBarTextBlack()
        wifiName.setCompoundDrawables(DrawableFitSize(R.drawable.wifi), null, null, null)
        buildEnterTransition()
    }

    private fun setUpListener() {
        codeButton.setOnClickListener {
            //TODO 扫码
        }
    }

    companion object {
        internal fun transitionTo(ctx: Activity) {
            val pairs = createSafeTransitionParticipants(ctx, true)
            val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ctx, *pairs)
            ctx.startActivity(Intent(ctx, MainActivity::class.java), transitionActivityOptions.toBundle())
        }
    }

    override fun onServerStop() {

    }

    override fun onServerStopped() {

    }

    override fun onServerStart() {

    }

    override fun onServerStarted() {

    }

    override fun onIpPort(ip: String, post: String) {
        textViewHost.text = ip + ":" + post
    }
}
