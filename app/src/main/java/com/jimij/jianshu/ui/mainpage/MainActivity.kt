package com.jimij.jianshu.ui.mainpage


import android.app.Activity
import android.content.Intent
import android.graphics.Color
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


class MainActivity : BaseActivity(), MainContract.View {
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

    private var dialog: MaterialDialog? = null
    //与活动生命周期关联
    private val presenter: MainPresenter = MainPresenter().apply { lifecycle.addObserver(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_main)
        initUI()
        setUpListener()
        Permission.STORAGE.doAfterGet(this) {
            MediaRepository
            inUiThread { presenter.startServer() }
        }
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

    companion object {
        internal fun transitionTo(ctx: Activity) {
            val pairs = createSafeTransitionParticipants(ctx, true)
            val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ctx, *pairs)
            ctx.startActivity(Intent(ctx, MainActivity::class.java), transitionActivityOptions.toBundle())
        }
    }

    //二维码扫描结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                showToast("扫描异常")
            } else {
                val url = "http://120.77.38.183/qr/send?sign=${result.contents}&ip=${Base64.encodeToString("${getHostIp()}:8080".toByteArray(), 0)}"
                launch {
                    try {
                        URL(url).readText()
                    } catch (e: Exception) {
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnected(nothing: String) {
        if (blurringView.visibility == View.VISIBLE) return
        blurringView.background = viewToBlurBitmap(contentView)?.toDrawable()
        blurringView.visiable()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}

