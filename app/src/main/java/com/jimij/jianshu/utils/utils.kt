package com.jimij.jianshu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.wifi.WifiManager
import android.view.View
import android.widget.ImageView
import com.google.gson.Gson
import com.jimij.jianshu.App
import com.mobile.utils.dp2px
import com.mobile.utils.gaussBlud
import com.weechan.httpserver.httpserver.HttpResponse

/**
 * Created by jimiji on 2018/3/31.
 */
fun HttpResponse.writeObject(any: Any) {
    addHeaders {
        "Content-Type" - "text/html;charset=utf-8"
    }
    write { this.write((any as? String)?.toByteArray() ?: Gson().toJson(any).toByteArray()) }
}

fun DrawableFitSize(id: Int): Drawable {
    val drawable = App.ctx.getDrawable(id)
    drawable.setBounds(0, 0, dp2px(20), dp2px(15))
    return drawable
}

fun getConnectedWifiSSID(): String {
    //一定要在AppCtx中获取
    val wm = App.ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val info = wm.connectionInfo
    return if (info == null) "请连接wifi" else info.ssid
}

//节省每次创建时产生的开销，但要注意多线程操作synchronized
private val sCanvas = Canvas()

private fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }

}

fun createBitmapFromView(view: View, scale: Float = 1f): Bitmap? {
    if (view is ImageView) {
        val drawable = view.drawable
        if (drawable != null && drawable is BitmapDrawable) {
            return drawable.bitmap
        }
    }
    view.clearFocus()
    val bitmap = createBitmapSafely((view.width * scale).toInt(),
            (view.height * scale).toInt(), Bitmap.Config.ARGB_8888, 1)
    if (bitmap != null) {
        synchronized(sCanvas) {
            val canvas = sCanvas
            canvas.setBitmap(bitmap)
            canvas.save()
            canvas.drawColor(Color.WHITE) // 防止 View 上面有些区域空白导致最终 Bitmap 上有些区域变黑
            canvas.scale(scale, scale)
            view.draw(canvas)
            canvas.drawColor(0x88000000.toInt()) // 防止 View 上面有些区域空白导致最终 Bitmap 上有些区域变黑
            canvas.restore()
            canvas.setBitmap(null)
        }
    }
    return bitmap
}

fun viewToBlurBitmap(view: View): Bitmap? {
    return gaussBlud(createBitmapFromView(view)!!, 25f)
}