package com.example.androidservice.httpserver.reslover

import android.util.Log
import java.net.URLDecoder

/**
 * Created by 铖哥 on 2018/3/17.
 */
class URLResolver {
    companion object {
        fun getRequestRouter(path: String): String {
            val index = path.indexOf('?')
            if (index != -1) {
                return path.substring(0, index)
            }

            return path
        }

        /**
         * 获取请求URL中的参数名以及参数值
         */
        fun getRequestArgument(realPath: String): HashMap<String, String> {

            val argumentMap = hashMapOf<String,String>()
            
            try{

                val router = getRequestRouter(realPath)

                if(realPath == router) return argumentMap

                val arguments = realPath.substring(router.length+1, realPath.length)
                val args = arguments.split("&")

                args.forEach {
                    val pair = it.split('=')
                    argumentMap.put(URLDecoder.decode(pair[0],"utf-8"),URLDecoder.decode(pair[1],"utf-8"))
                }

            }catch (e : Exception){
                Log.e("URLResolver", "解析url异常,请检查URL的格式是否符合规范")
            }

            return argumentMap
        }
    }

}