package com.jimij.jianshu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.wifi.WifiManager
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.gson.Gson
import com.jimij.jianshu.App
import com.jimij.jianshu.R
import com.mobile.utils.dp2px
import com.mobile.utils.gaussBlud
import com.mobile.utils.windowManager
import com.mobile.utils.*
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.uitls.writeTo
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.concurrent.thread

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
            canvas.drawColor(0x88000000.toInt()) // hei
            canvas.restore()
            canvas.setBitmap(null)
        }
    }
    return bitmap
}

fun viewToBlurBitmap(view: View): Bitmap? {
    return gaussBlud(createBitmapFromView(view)!!, 25f)
}

object NetListener {
    private val ls = mutableSetOf<NetCallback>()
    private var type: NetworkType = NetworkType.NETWORK_2G
    fun addListener(l: NetCallback) {
        ls.add(l)
    }

    fun removeListener(l: NetCallback) {
        ls.remove(l)
    }

    init {
        type = getNetworkType()
        async {
            while (true) {
                delay(2000)
                synchronized(ls) {
                    if (ls.isNotEmpty()) {
                        val type_new = getNetworkType()
                        if (type_new != type) {
                            ls.forEach {
                                inUiThread { it.onChange(type_new) }
                            }
                            doAfter(2000) {
                                Log.e("NetListener", "ASDSA")
                                ls.forEach { it.onChange(type_new) }
                            }
                            type = type_new
                        }
                    }
                }

            }
        }
    }
}

interface NetCallback {
    fun onChange(flag: NetworkType)
}

fun getScreenSize(): Point {
    val point = Point()
    windowManager.defaultDisplay.getRealSize(point)
    return point
}

fun zipFiles(srcfile: Array<File>, zipfile: HttpResponse) {
    val buf = ByteArray(1024)
    try {
        //ZipOutputStream类：完成文件或文件夹的压缩
        zipfile.write {
            val out = ZipOutputStream(this)
            for (i in srcfile.indices) {
                val `in` = FileInputStream(srcfile[i])
                out.putNextEntry(ZipEntry(srcfile[i].getName()))
                var len: Int = 0
                while ({ len = `in`.read(buf);len > 0 }.invoke()) {
                    out.write(buf, 0, len)
                }
                out.closeEntry()
                `in`.close()
            }
            out.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun File.zipInputStream() = ZipInputStream(this.inputStream())

fun File.zipOutputStream() = ZipOutputStream(this.outputStream())

fun InputStream.zipInputStream() = ZipInputStream(this)

fun OutputStream.zipOutputStream() = ZipOutputStream(this)


infix fun File.unZipTo(path: String) {
    //使用GBK编码,避免压缩中文文件名乱码
    checkUnzipFolder(path)
    ZipFile(this) unZipTo path
}

infix fun ZipFile.unZipTo(path: String) {
    checkUnzipFolder(path)
    for (entry in entries()) {
        //判断是否为文件夹
        if (entry.isDirectory) {
            File("${path}/${entry.name}").mkdirs()
        } else {
            val input = getInputStream(entry)
            val outputFile = File("${path}/${entry.name}")
            if (!outputFile.exists()) outputFile.smartCreateNewFile()
            val output = outputFile.outputStream()
            input.writeTo(output, DEFAULT_BUFFER_SIZE)
        }
    }
}

/**
 * 检查路径正确性
 */
private fun checkUnzipFolder(path: String) {
    val file = File(path)
    if (file.isFile) throw RuntimeException("路径不能是文件")
    if (!file.exists()) {
        if (!file.mkdirs()) throw RuntimeException("创建文件夹失败")
    }
}

fun ZipOutputStream.zipFrom(vararg srcs: String) {

    val files = srcs.map { File(it) }

    files.forEach {
        if (it.isFile) {
            zip(arrayOf(it), null)
        } else if (it.isDirectory) {
            zip(it.listFiles(), it.name)
        }
    }
    this.close()
}

private fun ZipOutputStream.zip(files: Array<File>, path: String?) {
    //前缀,用于构造路径
    val prefix = if (path == null) "" else "$path/"

    if (files.isEmpty()) createEmptyFolder(prefix)

    files.forEach {
        if (it.isFile) {
            val entry = ZipEntry("$prefix${it.name}")
            val ins = it.inputStream().buffered()
            putNextEntry(entry)
            ins.writeTo(this, DEFAULT_BUFFER_SIZE, closeOutput = false)
            closeEntry()
        } else {
            zip(it.listFiles(), "$prefix${it.name}")
        }
    }
}

/**
 * inputstream内容写入outputstream
 */
fun InputStream.writeTo(outputStream: OutputStream, bufferSize: Int = 1024 * 2,
                        closeInput: Boolean = true, closeOutput: Boolean = true) {

    val buffer = ByteArray(bufferSize)
    val br = this.buffered()
    val bw = outputStream.buffered()
    var length = 0

    while ({ length = br.read(buffer);length != -1 }()) {
        bw.write(buffer, 0, length)
    }

    bw.flush()

    if (closeInput) {
        close()
    }

    if (closeOutput) {
        outputStream.close()
    }
}

/**
 * 生成一个压缩文件的文件夹
 */
private fun ZipOutputStream.createEmptyFolder(location: String) {
    putNextEntry(ZipEntry(location))
    closeEntry()
}

fun File.smartCreateNewFile(): Boolean {

    if (exists()) return true
    if (parentFile.exists()) return createNewFile()

    if (parentFile.mkdirs()) {
        if (this.createNewFile()) {
            return true
        }
    }
    return false
}

fun getDefaultSavePath(): String {
    val root = Environment.getExternalStorageDirectory()
    val file = File(root.path, "Jianshu")
    file.toggleDir()
    return file.path
}

fun unZipWebFiles() {
    writeFileFromIS(File(getDefaultSavePath(), "web.zip"), App.ctx.resources.openRawResource(R.raw.web), false)
    File(getDefaultSavePath(), "web.zip").unZipTo(getDefaultSavePath())
}