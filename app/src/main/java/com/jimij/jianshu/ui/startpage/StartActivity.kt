package com.jimij.jianshu.ui.startpage

import android.os.Bundle
import com.jimij.jianshu.ui.mainpage.MainActivity
import com.jimij.jianshu.R

import com.jimij.jianshu.common.BaseActivity
import com.mobile.utils.doAfter
import com.mobile.utils.fullScreen

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
        setContentView(R.layout.activity_start)
        //假装在加载什么牛逼的东西
        doAfter(1500) {
            MainActivity.transitionTo(this)
            finishAfterTransition()
        }

    }

}
