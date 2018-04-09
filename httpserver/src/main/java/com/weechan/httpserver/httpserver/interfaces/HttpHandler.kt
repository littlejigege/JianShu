package com.weechan.httpserver.httpserver.interfaces

import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.HttpState

/**
 * Created by 铖哥 on 2018/3/17.
 */
interface HttpHandler{
    fun doGet(request : HttpRequest, response: HttpResponse)
    fun doPost(request : HttpRequest, response: HttpResponse)
}

abstract class BaseHandler : HttpHandler{


    override fun doGet(request: HttpRequest, response: HttpResponse) {
        response.httpState = HttpState.Method_Not_Allowed
    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {
        response.httpState = HttpState.Method_Not_Allowed
    }

}