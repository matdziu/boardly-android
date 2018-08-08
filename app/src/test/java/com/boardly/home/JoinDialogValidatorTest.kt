package com.boardly.home

import org.junit.Assert
import org.junit.Test

class JoinDialogValidatorTest {

    private val joinDialogValidator = JoinDialogValidator()

    @Test
    fun givenEmptyStringValidatorReturnsFalse() {
        Assert.assertEquals(joinDialogValidator.validate(" \n"), false)
        Assert.assertEquals(joinDialogValidator.validate(""), false)
    }

    @Test
    fun givenCorrectStringValidatorReturnsTrue() {
        Assert.assertEquals(joinDialogValidator.validate(" blabla"), true)
    }
}