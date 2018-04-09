package com.jimij.jianshu.server.handler

import com.jimij.jianshu.data.MediaRepository
import com.jimij.jianshu.utils.writeObject
import com.mobile.utils.JsonMaker
import com.mobile.utils.toBytes
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler

/**
 * Created by 铖哥 on 2018/4/9.
 */
@Http("/getThumbnail")
class ThumbnailHandler : BaseHandler(){
    override fun doGet(request: HttpRequest, response: HttpResponse) {
        val path = request.getRequestArgument("path")
        val type = request.getRequestArgument("type")
        var mType : Int = -1
        if(type == "photo") mType = 1 else if (type == "video") mType = 0

        if(path == null || type == null) {
            response.writeObject(JsonMaker.make {
                objects { "code"- -1
                    "errorMsg"-"参数不全,需要type与path"
                }
            })
            return
        }
        val bitmap = MediaRepository.getThumbnail(path,mType)

        if(bitmap == null){
            response.writeObject(JsonMaker.make {
                objects { "code"- -2 ; "errorMsg"-"无法获取缩略图" }
            })
            return
        }

        response.write {
            this.write(bitmap.toBytes())
        }


    }
}