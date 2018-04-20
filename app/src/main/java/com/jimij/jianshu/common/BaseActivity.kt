package com.jimij.jianshu.common

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.Explode
import android.transition.Slide
import android.transition.Transition
import android.transition.Visibility
import com.mobile.utils.fullScreen
import com.mobile.utils.permission.PermissionCompatActivity

@SuppressLint("Registered")
open
/**
 * Created by jimiji on 2018/3/31.
 */
class BaseActivity : PermissionCompatActivity() {

    protected fun buildExitTransition() {
        val enterTransition = Slide()
        enterTransition.duration = 500
        window.exitTransition = enterTransition
    }

    protected fun buildEnterTransition() {
        val enterTransition = Explode()
        enterTransition.duration = 500
        window.enterTransition = enterTransition
    }
}