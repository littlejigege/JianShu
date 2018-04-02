package com.jimij.jianshu.server.handler

import com.mobile.utils.JsonMaker
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import java.io.File

/**
 * Created by 铖哥 on 2018/3/25.
 */
@Http("/uplaod")
class  UploadHandler : HttpHandler{
    override fun doGet(request: HttpRequest, response: HttpResponse) {

    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {

        val savePath = request.getRequestArgument("savePath")

        response.write {
            JsonMaker.make {
                objects {
                    "code" - if(savePath == null) -1 else 1
                    "errMsg" - if(savePath == null) "需要savePath参数" else ""
                }
            }
        }

        val buf = ByteArray(1024*1024)
        val part = request.getRequestBody().getPart("file")
        val input = part?.inputSink
        if (input != null){
            var length = input.read(buf)
            val out = File("${savePath})/${part.fileName}").outputStream().buffered()
            while(length != -1){
                out.write(buf,0,length)
                length = input.read(buf)
            }
            out.flush()
            input.close()
        }


    }

}