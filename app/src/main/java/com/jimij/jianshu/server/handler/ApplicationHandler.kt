package com.jimij.jianshu.server.handler

import com.google.gson.Gson
import com.jimij.jianshu.data.MediaRepository
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.uitls.writeTo

/**
 * Created by jimiji on 2018/4/4.
 * 应用请求
 */
@Http("/getApp")
class ApplicationHandler : HttpHandler {
    override fun doGet(request: HttpRequest, response: HttpResponse) {
        response.addHeaders {
            "Content-Type" - "text/plain; charset=utf-8"
        }
        response.write { Gson().toJson(MediaRepository.getApplications()).byteInputStream().writeTo(this) }
    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {

    }
}