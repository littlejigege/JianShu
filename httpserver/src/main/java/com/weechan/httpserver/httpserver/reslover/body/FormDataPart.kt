package com.weechan.httpserver.httpserver.reslover.body

/**
 * Created by ��� on 2018/3/27.
 */
data class FormDataPart(val key:String?, val fileName:String?, val contentType : String?){
    lateinit var inputSink : ResponseInputstream
}