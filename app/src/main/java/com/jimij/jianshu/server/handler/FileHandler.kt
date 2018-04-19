package com.jimij.jianshu.server.handler

import com.jimij.jianshu.data.BaseResponse
import com.jimij.jianshu.data.json
import com.jimij.jianshu.utils.writeObject
import com.mobile.utils.JsonMaker
import com.mobile.utils.moveTo
import com.mobile.utils.smartDelete
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import java.io.File

/**
 * Created by 铖哥 on 2018/4/12.
 */

@Http("/file")
class FileHandler : BaseHandler() {
    override fun doGet(request: HttpRequest, response: HttpResponse) {
        response.addHeaders {
            "Access-Control-Allow-Origin" - "*"
            "Access-Control-Allow-Methods" - "POST,GET"
            "Content-Type" - "text/plain; charset=utf-8"
        }

        val operation = request.getRequestArgument("operation")
        val path = request.getRequestArgument("path")

        if (path == null) {
            response.writeObject(BaseResponse(-2, "缺少path参数"))
            return
        }

        val file = File(path)

        if (!file.exists() && operation == "delete" && !path.contains("|")) {
            response.writeObject(BaseResponse(-1, "文件不存在/路径无效").json())
            return
        }

        val toPath = request.getRequestArgument("to")
        if (toPath != null && !File(toPath).exists()) {
            response.writeObject(BaseResponse(-3, "移动的目的文件夹不存在"))
            return
        }

        when (operation) {
            "delete" -> kotlin.run {
                if (path.contains("|")) {
                    path.split("|").map { File(it) }.filter { it.exists() }.forEach {
                        it.smartDelete()
                    }
                } else {
                    file.smartDelete()
                }
                response.writeObject(BaseResponse(0, "").json())
                return
            }

            "create" -> if (file.mkdirs()) {
                response.writeObject(BaseResponse(0, "").json())
                return
            }
            "move" -> {
                File(path).moveTo(toPath!!)
                response.writeObject(BaseResponse(0, "").json())
                return
            }
        }

        response.writeObject(BaseResponse(-2, "参数不全or无权限删除该文件").json())
    }
}