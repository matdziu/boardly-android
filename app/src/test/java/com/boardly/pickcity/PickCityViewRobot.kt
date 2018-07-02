package com.boardly.pickcity

import com.boardly.base.BaseViewRobot

class PickCityViewRobot(pickCityViewModel: PickCityViewModel) : BaseViewRobot<PickCityViewState>() {

    private val pickCityView = object : PickCityView {
        override fun render(pickCityViewState: PickCityViewState) {
            renderedStates.add(pickCityViewState)
        }
    }

    init {
        pickCityViewModel.bind(pickCityView)
    }
}