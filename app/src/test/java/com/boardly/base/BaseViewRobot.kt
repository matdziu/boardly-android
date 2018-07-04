package com.boardly.base

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert

open class BaseViewRobot<T> {

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    val renderedStates = arrayListOf<T>()

    fun assertViewStates(vararg expectedStates: T) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}