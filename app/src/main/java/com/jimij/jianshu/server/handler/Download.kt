package com.jimij.jianshu.server.handler

import android.os.Environment
import android.util.Log
import com.jimij.jianshu.utils.zipFiles
import com.jimij.jianshu.utils.zipFrom
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.HttpState
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import com.weechan.httpserver.httpserver.uitls.writeTo
import java.io.File
import java.io.OutputStream
import java.io.RandomAccessFile
import java.util.zip.ZipOutputStream

@Http("/download")
class Download : BaseHandler() {

    private lateinit var response: HttpResponse
    private lateinit var file: File
    private var fileSize: Long = 0
    private var range: Pair<Long, Long>? = null

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        this.response = response

        val path = request.getRequestArgument("path") ?: return
        val preview = request.getRequestArgument("preview")
        val type: String = if (preview == "true") "inline" else if (preview == "false") "attachment" else "attachment"
        var fileName = ""

        if (!path.contains("|")) {
            this.file = File(path)

            if (file.exists() && file.isFile) fileSize = file.length()
            range = resloveRange(request.getRequestHead("range"), fileSize)

            fileName = path.substring(path.lastIndexOf("/") + 1, path.length)

            setHttpState()

            setResponseHeaders()

            setBody()
        } else {

            val files: Array<File> = path.split("|").map { File(it) }.filter { it.exists() }.toTypedArray()

            fileName = "${files[0].name}等${files.size}个文件.zip"

            response.write {
                ZipOutputStream(this).zipFrom(*files.map { it.path }.toTypedArray())
            }

            response.httpState = HttpState.OK_200
        }

        response.addHeaders {
            "Content-Disposition" - "$type; filename=$fileName"
        }


    }


    private fun setResponseHeaders() {
        if (range != null) {
            response.addHeaders {
                "Content-Length" - " $${fileSize}\r\n"
                "Content-Range" - " bytes ${range?.first}-${range?.second}/${fileSize}\r\n"
                "Accept-Ranges" - " bytes\r\n"
            }
        }

    }

    private fun setBody() {

        if (range != null) {
            response.write {
                readAndSendPartFile(this)
            }
        } else {
            response.write {
                file.inputStream().writeTo(this, true)
            }
        }

    }

    private fun readAndSendPartFile(output: OutputStream) {
        val (from, to) = range!!
        var length = to - from + 1
        val raf = RandomAccessFile(file.path, "r")
        raf.seek(from)

        val buf = ByteArray(1024 * 1024 * 2)

        while (length > 0) {
            var readBytes: Int
            if (length < buf.size) {
                readBytes = raf.read(buf, 0, length.toInt())
            } else {
                readBytes = raf.read(buf)
            }

            output.write(buf, 0, readBytes)
            length -= readBytes
            Log.e("ResponseBuilder", length.toString())
        }

    }

    private fun resloveRange(range: String?, fileSize: Long?): Pair<Long, Long>? {
        if (range == null || fileSize == null) return null
        var (from, to) = range.split("=")[1].split("-").map { if (it.isEmpty()) -1 else it.toLong() }
        if (from == -1L) from = 0
        if (to == -1L) to = fileSize - 1
        return Pair(from, to)
    }

    private fun setHttpState() {

        if ((!file.exists() || !file.isFile)) {
            response.httpState = HttpState.Not_Found_404
            return
        }

        if (range != null) {
            response.httpState = HttpState.RangeOK_206
            return
        }

        response.httpState = HttpState.OK_200
    }

}