package com.jimij.jianshu.ui.mainpage


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.util.Log
import com.google.zxing.integration.android.IntentIntegrator
import com.jimij.jianshu.R
import com.jimij.jianshu.common.BaseActivity

import com.jimij.jianshu.utils.DrawableFitSize
import com.jimij.jianshu.utils.createSafeTransitionParticipants
import com.mobile.utils.*
import com.mobile.utils.permission.Permission

import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.data
import android.widget.Toast
import com.google.zxing.integration.android.IntentResult
import com.jimij.jianshu.ui.scan.CaptureActivity


//import com.uuzuche.lib_zxing.activity.CaptureActivity
//import com.uuzuche.lib_zxing.activity.CodeUtils


class MainActivity : BaseActivity(), MainContract.View {

    val SCAN_REQUEST_CODE = 1

    //与活动生命周期关联
    private val presenter: MainPresenter = MainPresenter().apply { lifecycle.addObserver(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        setUpListener()
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
        val REQUEST_CODE = 1
        codeButton.setOnClickListener {
            Permission.CAMERA.doAfterGet(this){
                val i = IntentIntegrator(this)
                i.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                i.setCaptureActivity(CaptureActivity::class.java)
                i.initiateScan()
            }
        }
    }

    companion object {
        internal fun transitionTo(ctx: Activity) {
            val pairs = createSafeTransitionParticipants(ctx, true)
            val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ctx, *pairs)
            ctx.startActivity(Intent(ctx, MainActivity::class.java), transitionActivityOptions.toBundle())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                //TODO 扫描无结果
            } else {
                showToast(result.contents)
                //TODO 扫描结果为 result.contents
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
