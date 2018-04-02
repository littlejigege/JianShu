package com.jimij.jianshu.startpage

import android.os.Bundle
import android.widget.Toast
import com.jimij.jianshu.mainpage.MainActivity
import com.jimij.jianshu.R

import com.jimij.jianshu.common.BaseActivity
import com.mobile.utils.doAfter
import com.mobile.utils.fullScreen
import com.mobile.utils.showToast

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        fullScreen()
        setContentView(R.layout.activity_start)
        //假装在加载什么牛逼的东西
        doAfter(2000) {
            MainActivity.transitionTo(this)
            finishAfterTransition()
        }

        showToast("修复后 一点BUG也么啦")
    }

}
