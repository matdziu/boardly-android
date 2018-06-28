package com.boardly.base

import junit.framework.Assert

open class BaseViewRobot<T> {

    val renderedStates = arrayListOf<T>()

    fun assertViewStates(vararg expectedStates: T) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}