package com.boardly.home

class JoinDialogValidator {

    fun validate(input: String): Boolean {
        return input.isNotBlank()
    }
}