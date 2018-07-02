package com.boardly.pickgame

import com.boardly.base.BaseViewRobot

class PickGameViewRobot(pickGameViewModel: PickGameViewModel) : BaseViewRobot<PickGameViewState>() {

    private val pickGameView = object : PickGameView {
        override fun render(pickGameViewState: PickGameViewState) {
            renderedStates.add(pickGameViewState)
        }
    }

    init {
        pickGameViewModel.bind(pickGameView)
    }
}