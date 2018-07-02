package com.boardly.pickcity

sealed class PartialPickCityVIewState {

    abstract fun reduce(previousState: PickCityViewState): PickCityViewState

    class ProgressState : PartialPickCityVIewState() {
        override fun reduce(previousState: PickCityViewState) = PickCityViewState(progress = true)
    }
}