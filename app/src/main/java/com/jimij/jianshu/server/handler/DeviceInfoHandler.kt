package com.jimij.jianshu.server.handler

import android.os.Environment
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import com.weechan.httpserver.httpserver.interfaces.HttpHandler

import android.os.StatFs
import android.os.Environment.getDataDirectory
import com.mobile.utils.JsonMaker
import android.os.Build.PRODUCT
import android.os.Build
import com.jimij.jianshu.utils.writeObject
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.uitls.writeTo
import java.io.File


/**
 * Created by 铖哥 on 2018/4/4.
 */
@Http("/getDeviceInfo")
class DeviceInfoHandler : BaseHandler() {

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        response.writeObject(JsonMaker.make {
            objects {
                "code" - 1
                "totalSize" - getTotalInternalMemorySize()
                "availableSize" - getAvailableInternalMemorySize()
                "model" - getSystemModel()
                "sd" - getSDCard()
            }
        })

        response.addHeaders {
            "Access-Control-Allow-Origin" - "*"
            "Access-Control-Allow-Methods" - "POST,GET"
        }

    }


    /**
     * 获取手机内部空间总大小
     *
     * @return 大小，字节为单位
     */
    fun getTotalInternalMemorySize(): Long {
        //获取内部存储根目录
        val path = Environment.getDataDirectory()
        //系统的空间描述类
        val stat = StatFs(path.getPath())
        //每个区块占字节数
        val blockSize = stat.blockSize.toLong()
        //区块总数
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return 大小，字节为单位
     */
    fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSize.toLong()
        //获取可用区块数量
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

    fun getSystemModel(): String {
        return android.os.Build.MODEL
    }

    fun getSDCard() = File("/storage/").listFiles().map { it.listFiles() }.joinToString()

}