package com.jimij.jianshu.utils

import android.graphics.drawable.Drawable
import com.google.gson.Gson
import com.jimij.jianshu.App
import com.mobile.utils.dp2px
import com.weechan.httpserver.httpserver.HttpResponse

/**
 * Created by jimiji on 2018/3/31.
 */
fun HttpResponse.writeObject(any: Any) {
    addHeaders {
        "Content-Type" - "text/html;charset=utf-8"
    }
    write { this.write((any as? String)?.toByteArray() ?: Gson().toJson(any).toByteArray()) }
}

fun DrawableFitSize(id: Int): Drawable {
    val drawable = App.ctx.getDrawable(id)
    drawable.setBounds(0, 0, dp2px(20), dp2px(15))
    return drawable
}