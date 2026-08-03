package com.paradox543.malankaraorthodoxliturgica

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityHolder {
    private var activityRef: WeakReference<Activity>? = null

    var activity: Activity?
        get() = activityRef?.get()
        set(value) {
            activityRef = value?.let { WeakReference(it) }
        }
}