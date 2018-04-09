package com.weechan.httpserver.httpserver.reslover.body

import android.util.Log
import java.io.InputStream

/**
 * Created by 铖哥 on 2018/4/4.
 */
class BinaryInputStream(val ins :InputStream, val length : Long) {

    private var readBytes = 0L

    fun read(buf:ByteArray): Int {
        if(readBytes >= length) return -1
        val maxSize = Math.min(buf.size.toLong(),length-readBytes)
        val read =  ins.read(buf,0,maxSize.toInt())
        readBytes += read
        return read
    }

    fun close(){
        // Actually not allow to close()
    }


}