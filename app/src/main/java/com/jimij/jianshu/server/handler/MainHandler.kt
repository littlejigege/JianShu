package com.jimij.jianshu.server.handler

import com.jimij.jianshu.utils.writeObject
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.HttpHandler

/**
 * Created by jimiji on 2018/3/31.
 */
@Http("/")
class MainHandler: HttpHandler {
    override fun doGet(request: HttpRequest, response: HttpResponse) {

        response.writeObject("1231231231")
    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {

    }
}