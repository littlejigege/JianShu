package com.jimij.jianshu.server.handler

import android.util.Log
import com.mobile.utils.JsonMaker
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.uitls.writeTo
import java.io.File

/**
 * Created by 铖哥 on 2018/3/25.
 */
@Http("/upload")
class UploadHandler : HttpHandler {
    override fun doGet(request: HttpRequest, response: HttpResponse) {

    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {

        var savePath = request.getRequestArgument("savePath")
        val fileName = request.getRequestArgument("fileName")

        response.write {
            JsonMaker.make {
                objects {
                    "code" - if (savePath == null || fileName == null) -1 else 1
                    "errMsg" - if (savePath == null || fileName == null) "参数不全,检查savePath与fileName参数" else ""
                }
            }.byteInputStream().writeTo(this)
        }

        if (savePath == null || fileName == null) return

        savePath = if (savePath.endsWith("/")) {
            savePath.substring(0, savePath.length - 1)
        } else savePath


        val buf = ByteArray(1024 * 1024)

        val input = request.getRequestBody().getRawInputStream()

        var length = input.read(buf)
        val out = File("$savePath/$fileName").outputStream().buffered()
        while (length != -1) {
            out.write(buf, 0, length)
            length = input.read(buf)
        }
        out.flush()
    }


}

