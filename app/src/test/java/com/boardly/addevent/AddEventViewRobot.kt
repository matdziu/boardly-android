package com.boardly.addevent

import com.boardly.base.BaseViewRobot

class AddEventViewRobot(addEventViewModel: AddEventViewModel) : BaseViewRobot<AddEventViewState>() {

    private val addEventView = object : AddEventView {
        override fun render(addEventViewState: AddEventViewState) {
            renderedStates.add(addEventViewState)
        }
    }

    init {
        addEventViewModel.bind(addEventView)
    }
}