package com.jimij.jianshu.ui.mainpage


import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.util.Base64
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.jimij.jianshu.R
import com.jimij.jianshu.common.BaseActivity
import com.jimij.jianshu.data.MediaRepository
import com.jimij.jianshu.ui.scan.CaptureActivity
import com.jimij.jianshu.utils.*
import com.mobile.utils.*
import com.mobile.utils.permission.Permission
import com.weechan.httpserver.httpserver.uitls.getHostIp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.URL
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.jimij.jianshu.data.ScreenCaptureResult
import android.widget.Toast
import android.hardware.usb.UsbAccessory
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Parcelable
import com.jimij.jianshu.App


class MainActivity : BaseActivity(), MainContract.View {

    private var screenCapture: ScreenCapture? = null
    private var dialog: MaterialDialog? = null
    //与活动生命周期关联
    private lateinit var presenter: MainPresenter


    override fun requestPermission(ip: String) {
        if (dialog == null) {
            dialog = MaterialDialog.Builder(this)
                    .title("连接请求")
                    .content("ip为${ip}的设备请求连接")
                    .positiveText("允许")
                    .negativeText("不允许")
                    .onPositive({ _, _ -> presenter.addWhiter(ip);presenter.isPass = true;presenter.isInterceptPass = true;dialog = null })
                    .onNegative({ _, _ -> presenter.isPass = false;presenter.isInterceptPass = true; dialog = null })
                    .cancelable(false)
                    .show()
        }
        dialog?.show()
    }

    override fun onResume() {
        super.onResume()
        presenter.view = this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//保持屏幕常亮
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildEnterTransition()
        presenter = MainPresenter(this)
        EventBus.getDefault().register(this)
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
        codeButton.setOnClickListener {
            Permission.CAMERA.doAfterGet(this) {
                val i = IntentIntegrator(this)
                i.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                i.captureActivity = CaptureActivity::class.java
                i.initiateScan()
            }
        }

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


    //二维码扫描结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {

            } else {
                val url = "http://120.77.38.183/qr/send?sign=${result.contents}&ip=${Base64.encodeToString("${getHostIp()}:8080".toByteArray(), 0)}"
                launch {
                    try {
                        URL(url).readText()
                    } catch (e: Exception) {
                    }
                }
            }
        }

//        if (requestCode == 199) {
//            if (screenCapture == null) return
//
//            val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
//            if (mediaProjection == null) {
//                Log.e("@@", "media projection is null")
//                return
//            }
//            with(screenCapture!!) {
//                mediaProjection.createVirtualDisplay("test", width, height, 1,
//                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                        mImageReader.surface, null, null)
//            }
//        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnected(nothing: String) {
        if (blurringView.visibility == View.VISIBLE) return
        blurringView.background = viewToBlurBitmap(contentView)?.toDrawable()
        blurringView.visiable()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCaptureScreen(screenCaptureResult: ScreenCaptureResult) {
        if (screenCaptureResult.code == ScreenCaptureResult.REQUEST) {
            val bitmap = screenCapture?.startCapture()
            screenCaptureResult.bitmap = bitmap
            screenCaptureResult.code = ScreenCaptureResult.RESPONSE
            EventBus.getDefault().post(screenCaptureResult)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }




}

