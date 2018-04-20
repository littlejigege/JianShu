package com.jimij.jianshu.server.handler

import android.util.Base64
import com.jimij.jianshu.data.BaseResponse
import com.jimij.jianshu.data.MediaRepository
import com.jimij.jianshu.utils.writeObject
import com.mobile.utils.JsonMaker
import com.mobile.utils.toBytes
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import java.io.File

/**
 * Created by 铖哥 on 2018/4/9.
 */
@Http("/getThumbnail")
class ThumbnailHandler : BaseHandler(){
    override fun doGet(request: HttpRequest, response: HttpResponse) {
        val path = request.getRequestArgument("path")
        val type = request.getRequestArgument("type")

        var mType : Int = -1
        if(type == "photo" ){
            mType = 1
        } else if (type == "video"){
            mType = 0
        }else if(type == "app"){
            mType = 2
        } else mType = 0

        val file = File(path)

        if(path == null || type == null) {
            response.writeObject(BaseResponse(-2,"参数不全,检查path和type"))
            return
        }

        if(!file.exists()) {
            response.writeObject(BaseResponse(-3,"文件不存在"))
            return
        }

        val thumbnail = MediaRepository.getThumbnail(file.path,mType)

        if(thumbnail == null){
            response.writeObject(JsonMaker.make {
                objects { "code"- -1 ; "errorMsg"-"无法获取缩略图" }
            })
            return
        }


        response.write {
            this.write(Base64.decode(thumbnail.bitmap,Base64.DEFAULT))
        }

        response.addHeaders {
            "Access-Control-Allow-Origin" - "*"
            "Access-Control-Allow-Methods" - "POST,GET"
        }




    }
}