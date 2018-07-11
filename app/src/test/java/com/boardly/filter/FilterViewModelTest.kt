package com.boardly.filter

import com.nhaarman.mockito_kotlin.mock

class FilterViewModelTest {

    private val filterInteractor: FilterInteractor = mock()
    private val filterViewModel = FilterViewModel(filterInteractor)
    private val filterViewRobot = FilterViewRobot(filterViewModel)
}