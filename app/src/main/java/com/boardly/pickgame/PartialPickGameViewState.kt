package com.boardly.pickgame

sealed class PartialPickGameViewState {

    abstract fun reduce(previousState: PickGameViewState): PickGameViewState

    class ProgressState : PartialPickGameViewState() {
        override fun reduce(previousState: PickGameViewState) = PickGameViewState(progress = true)
    }
}