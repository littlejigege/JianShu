package com.weechan.httpserver.httpserver

import android.content.Context
import android.util.Log
import com.example.androidservice.httpserver.reslover.reslovebean.RequestMessage
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.uitls.getClassesInPackage
import java.net.Socket
import java.nio.charset.Charset

/**
 * Created by 铖哥 on 2018/3/19.
 */
class HttpServerBuilder {

    companion object {

        private var handlerPackage = "handler"
        private lateinit var handlerClassList: List<Class<*>>
        private var port = 8080
        private var path: String? = null
        var interceptor: ((RequestMessage) -> Boolean)? = null

        fun with(context: Context): Companion {
            handlerClassList = getClassesInPackage(handlerPackage, context)
            return this
        }

        fun handlerPackage(var0: String): Companion {
            handlerPackage = var0
            return this
        }

        fun port(port: Int): Companion {
            this.port = port
            return this
        }



        fun intercept(interceptor: ((RequestMessage) -> Boolean)? = null) : Companion{
            this.interceptor = interceptor
            return this
        }


        fun getHttpServer(): HttpServer {
            val server = HttpServer(port)
            for (clazz in handlerClassList) {
                server.addHandler(tryAs(clazz))
            }
            if(handlerClassList.isEmpty()){
                Log.e("HttpServerBuilder", "注意,没有加入任何请求的处理器,这是你想要的结果吗?")
            }
            server.interceptor = this.interceptor
            return server
        }

        fun tempFilePath(path:String){
            this.path = path
        }

        private fun tryAs(clazz: Class<*>): Class<HttpHandler>? {
            var var0: Class<HttpHandler>? = null
            try {
                var0 = clazz as Class<HttpHandler>
            } catch (e: Exception) {

            }
            return var0
        }


    }


}