package com.jimij.jianshu.ui.mainpage


import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.jimij.jianshu.R
import com.jimij.jianshu.common.BaseActivity
import com.jimij.jianshu.utils.*
import com.mobile.utils.*
import com.mobile.utils.permission.Permission
import kotlinx.android.synthetic.main.activity_main.*
import android.view.WindowManager



class MainActivity : BaseActivity(), MainContract.View {

    private var screenCapture: ScreenCapture? = null
    //与活动生命周期关联
    private lateinit var presenter: MainPresenter


    override fun onResume() {
        super.onResume()
        presenter.view = this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//保持屏幕常亮
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildEnterTransition()
        presenter = MainPresenter(this)
        setContentView(R.layout.activity_main)
        initUI()
        setUpListener()
        Permission.STORAGE.doAfterGet(this) {
            unZipWebFiles()
            inUiThread { presenter.startServer() }
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.stopPresenter()
    }

    private fun initUI() {
        setSupportActionBar(toolBar)
        window.statusBarColor = Color.WHITE
        setStatusBarTextBlack()
        wifiName.setCompoundDrawables(DrawableFitSize(R.drawable.wifi), null, null, null)
        buildEnterTransition()
        wifiName.text = getConnectedWifiSSID()
    }

    private fun setUpListener() {

        textViewHost.setOnLongClickListener {
            //复制文本
            presenter.copyText(textViewHost.text.toString())
            return@setOnLongClickListener true
        }

        disConnectButton.setOnClickListener {
            presenter.clearWhiter()
            blurringView.gone()
        }
    }


    override fun onBackPressed() {
        ActivityManager.doubleExit()
    }

    override fun onServerStop() {

    }

    override fun onServerStopped() {

    }

    override fun onServerStart() {

    }

    override fun onServerStarted() {

    }

    override fun onIpPort(ip: String, port: String) {
        textViewHost.text = ip + ":" + port
        wifiName.text = getConnectedWifiSSID()
    }




}

