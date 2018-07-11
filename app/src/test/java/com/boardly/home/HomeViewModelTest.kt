package com.boardly.home

import com.nhaarman.mockito_kotlin.mock

class HomeViewModelTest {

    private val homeInteractor: HomeInteractor = mock()
    private val homeViewModel = HomeViewModel(homeInteractor)
    private val homeViewRobot = HomeViewRobot(homeViewModel)
}