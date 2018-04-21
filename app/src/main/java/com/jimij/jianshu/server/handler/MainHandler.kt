package com.jimij.jianshu.server.handler

import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.BaseHandler
import com.weechan.httpserver.httpserver.uitls.getHostIp
import com.weechan.httpserver.httpserver.uitls.writeTo
import org.greenrobot.eventbus.EventBus

/**
 * Created by 铖哥 on 2018/3/23.
 */
@Http("/")
class MainHandler() : BaseHandler() {

    val ip: String = getHostIp() + ":8080"

    val html = """
        <!DOCTYPE html>
<html>
<head>
    <title>简介</title>
    <meta charset="utf-8"/>
    <link rel="stylesheet" type="text/css" href="CSSFile/commonStruc.css"/>
    <link rel="shortcut icon" href="images/jianshu.ico"/>
    <script src="JSFile/URLForRequest.js"></script>
</head>
<body>
<div id="fileUploadBox"></div>
<div id="tableContainer">
    <div class="tableRow">
        <nav>
            <img id="logo" src="images/logo.png" alt="简输logo"/>
            <ul>
                <li><a href="index.html" title="简单介绍" class="selected">简介</a></li>
                <li><a href="uploadPage.html" title="进行文件上传"><img src="images/upload.png"/>上传</a></li>
                <li><a href="picture.html" title="查看手机中的图片"><img src="images/picture.png"/>图库</a></li>
                <li><a href="music.html" title="查看手机中的音乐"><img src="images/music.png"/>音乐</a></li>
                <li><a href="app.html" title="查看手机中的应用"><img src="images/app.png"/>应用</a></li>
                <li><a href="AllFile.html" title="查看手机中的所有文件"><img src="images/AllFile.png"/>文件</a></li>
                <li><a href="document.html" title="查看手机中的所有文件"><img src="images/document.png"/>文档</a></li>
            </ul>
            </ul>
        </nav>
        <div id="mainContainer">
        </div>
    </div>
</div>
</body>
</html>
    """.trimIndent()

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        EventBus.getDefault().post("")
        response.addHeaders {
            "Access-Control-Allow-Origin" - "*"
            "Access-Control-Allow-Methods" - "POST,GET"
        }
        response.write {
            html.byteInputStream().writeTo(this)
        }
    }


}