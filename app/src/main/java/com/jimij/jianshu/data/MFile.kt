package com.jimij.jianshu.data

/**
 * Created by 铖哥 on 2018/3/17.
 */

data class MFileResponse(val code : Int, val mFile: List<MFile>?)

data class MFile(val size: Long = 0,
                 val path: String = "",
                 val lastModify : String ="",
                 val isDirectory: Boolean = false

)