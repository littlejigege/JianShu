package com.jimij.jianshu.ui.startpage

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.transition.Explode
import com.jimij.jianshu.ui.mainpage.MainActivity
import com.jimij.jianshu.R

import com.jimij.jianshu.common.BaseActivity
import com.jimij.jianshu.utils.createSafeTransitionParticipants
import com.mobile.utils.doAfter
import com.mobile.utils.fullScreen
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
        setContentView(R.layout.activity_start)
        //假装在加载什么牛逼的东西
        doAfter(1500) {
            transitionTo(this)
            finishAfterTransition()

            val drawable = imageView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                bitmap.recycle()
            }
        }



    }

    fun transitionTo(ctx: Activity) {
//        val pairs = createSafeTransitionParticipants(ctx, true)
//        val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ctx, *pairs)
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()

        ctx.startActivity(Intent(ctx, MainActivity::class.java),bundle)
    }

}
