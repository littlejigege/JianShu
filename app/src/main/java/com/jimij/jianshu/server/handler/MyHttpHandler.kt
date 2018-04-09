package com.jimij.jianshu.server.handler

import com.google.gson.Gson
import com.jimij.jianshu.data.MFile
import com.jimij.jianshu.data.MFileResponse
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
* Created by 铖哥 on 2018/3/20.
*/

@Http(route = "/find")
class MyHttpHandler : HttpHandler {

    override fun doPost(request: HttpRequest, response: HttpResponse) {

    }

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        val file = File(request.getRequestArgument("path"))
        if (file.isDirectory) {
            response.write {
                writeFileMessage(file, this)
            }
        }
        response.addHeaders {
            "Access-Control-Allow-Origin" - "*"
            "Access-Control-Allow-Methods" - "POST,GET"
            "Content-Type" - "text/plain; charset=utf-8"
        }
    }

    private fun writeFileMessage(file: File, output: OutputStream) {
        val response: MFileResponse
        response = if (!file.exists()) {
            MFileResponse(-1, null)
        } else {
            val mFiles = mutableListOf<MFile>()
            file.listFiles().forEach {
                mFiles.add(MFile(it.isDirectory, it.length(), it.path, SimpleDateFormat.getDateInstance().format(Date(it.lastModified()))))
            }
            mFiles.sortWith(Comparator { o1, o2 ->
                fun getName(file: MFile) = file.path.substring(file.path.lastIndexOf("/") + 1, file.path.lastIndex)
                getName(o1).compareTo(getName(o2))
            })
            mFiles.sortWith(Comparator { o1, o2 ->
                if (o1.isDirectory == o2.isDirectory)  return@Comparator 0
                if (o1.isDirectory)  1 else  -1
            })
            MFileResponse(1, mFiles)
        }
        val resp = Gson().toJson(response)
        output.write(resp.toByteArray())
    }

}