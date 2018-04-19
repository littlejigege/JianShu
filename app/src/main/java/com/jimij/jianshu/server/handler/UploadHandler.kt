package com.jimij.jianshu.server.handler

import android.util.Log
import com.jimij.jianshu.data.BaseResponse
import com.jimij.jianshu.data.json
import com.jimij.jianshu.utils.writeObject
import com.mobile.utils.JsonMaker
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.HttpState
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import com.weechan.httpserver.httpserver.uitls.writeTo
import java.io.File

/**
 * Created by 铖哥 on 2018/3/25.
 */
@Http("/upload")
class UploadHandler : BaseHandler() {
    override fun doPost(request: HttpRequest, response: HttpResponse) {
        val input = request.getRequestBody().getPart("file")?.inputSink
        var savePath = request.getRequestArgument("savePath")
        val fileName = request.getRequestArgument("fileName")

        response.addHeaders {
            "Access-Control-Allow-Origin" - "*"
            "Access-Control-Allow-Methods" - "POST,GET"
        }

        response.write {
            JsonMaker.make {
                objects {
                    "code" - if (savePath == null || fileName == null) -1 else 1
                    "errorMsg" - if (savePath == null || fileName == null) "参数不全,检查savePath与fileName参数" else ""
                }
            }.byteInputStream().writeTo(this)
        }
        if (savePath == null || fileName == null) return

        savePath = if (savePath.endsWith("/")) {
            savePath.substring(0, savePath.length - 1)
        } else savePath

        if ( input == null){
            response.writeObject(BaseResponse(-3,"没有要上传的文件").json())
            return
        }

        val buf = ByteArray(1024 * 1024)
        var length = input.read(buf)
        val out = File("$savePath/$fileName").outputStream().buffered()
        while (length != -1) {
            out.write(buf, 0, length)
            length = input.read(buf)
        }
        out.flush()

//        var savePath = request.getRequestArgument("savePath")
//        val fileName = request.getRequestArgument("fileName")
//
//        response.write {
//            JsonMaker.make {
//                objects {
//                    "code" - if (savePath == null || fileName == null) -1 else 1
//                    "errorMsg" - if (savePath == null || fileName == null) "参数不全,检查savePath与fileName参数" else ""
//                }
//            }.byteInputStream().writeTo(this)
//        }
//
//        if (savePath == null || fileName == null) return
//
//        savePath = if (savePath.endsWith("/")) {
//            savePath.substring(0, savePath.length - 1)
//        } else savePath
//
//
//        val buf = ByteArray(1024 * 1024)
//
//        val input = request.getRequestBody().getRawInputStream()
//
//        var length = input.read(buf)
//        val out = File("$savePath/$fileName").outputStream().buffered()
//        while (length != -1) {
//            out.write(buf, 0, length)
//            length = input.read(buf)
//        }
//        out.flush()
//
//        response.writeObject(BaseResponse(0,"").json())
    }

    override fun doOptions(request: HttpRequest, response: HttpResponse) {
        response.addHeaders {
            "Access-Control-Allow-Method"-"GET, POST, PUT, DELETE, OPTIONS"
            "Access-Control-Allow-Origin" -"*"
            "Access-Control-Allow-Headers" - "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With"
        }

        response.httpState = HttpState.NOT_CONTENT_204

    }
}


