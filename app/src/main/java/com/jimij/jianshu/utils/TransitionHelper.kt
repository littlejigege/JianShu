package com.jimij.jianshu.utils

import android.app.Activity
import android.support.v4.util.Pair
import android.view.View
import java.util.*

/**
 * Created by jimiji on 2018/3/31.
 */
fun createSafeTransitionParticipants(activity: Activity,
                                     includeStatusBar: Boolean, vararg otherParticipants: Pair<*, *>): Array<Pair<View, String>> {
    // Avoid system UI glitches as described here:
    // https://plus.google.com/+AlexLockwood/posts/RPtwZ5nNebb
    val decor = activity.window.decorView
    var statusBar: View? = null
    if (includeStatusBar) {
        statusBar = decor.findViewById(android.R.id.statusBarBackground)
    }
    val navBar = decor.findViewById<View>(android.R.id.navigationBarBackground)

    // Create pair of transition participants.
    val participants = ArrayList<Pair<*, *>>(3)
    addNonNullViewToTransitionParticipants(statusBar, participants)
    addNonNullViewToTransitionParticipants(navBar, participants)
    // only add transition participants if there's at least one none-null element
    if (otherParticipants != null && !(otherParticipants.size == 1 && otherParticipants[0] == null)) {
        participants.addAll(Arrays.asList<Pair<*, *>>(*otherParticipants))
    }
    return participants.toTypedArray() as Array<Pair<View, String>>
}

private fun addNonNullViewToTransitionParticipants(view: View?, participants: MutableList<Pair<*, *>>) {
    if (view == null) {
        return
    }
    participants.add(Pair(view, view.transitionName))
}
