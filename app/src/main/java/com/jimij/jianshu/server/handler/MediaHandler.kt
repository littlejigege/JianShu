package com.jimij.jianshu.server.handler



import com.google.gson.Gson
import com.jimij.jianshu.data.MFileResponse
import com.jimij.jianshu.data.MediaRepository
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import com.weechan.httpserver.httpserver.uitls.writeTo
import java.io.InputStream

/**
* Created by weechan on 18-3-24.
 * 多媒体请求
*/

@Http("/getMedia")
class MediaHandler : BaseHandler() {

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        val type = request.getRequestArgument("type")
        var jsonIn : InputStream? = null
        println(type)
        when (type) {
            "music" -> jsonIn = Gson().toJson(MFileResponse(0,MediaRepository.getMusic())).byteInputStream()
            "document" -> jsonIn = Gson().toJson((MFileResponse(0,MediaRepository.getDocument()))).byteInputStream()
            "video" ->jsonIn = Gson().toJson((MFileResponse(0,MediaRepository.getVideo()))).byteInputStream()
            "photoDir" -> jsonIn = Gson().toJson((MFileResponse(0,MediaRepository.getPhotosDirectory()))).byteInputStream()
            "photo" -> jsonIn = Gson().toJson((MFileResponse(0,MediaRepository.getPhotos(request.getRequestArgument("path"))))).byteInputStream()
        }

        response.write {
            jsonIn?.writeTo(this)
        }

        response.addHeaders {
            "Access-Control-Allow-Origin"-"*"
            "Access-Control-Allow-Methods"-"POST,GET"
            "Content-Type" - "text/plain; charset=utf-8"
        }
    }


}