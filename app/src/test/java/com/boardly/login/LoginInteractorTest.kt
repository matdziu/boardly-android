package com.boardly.login

import com.boardly.login.models.LoginData
import com.boardly.login.network.LoginService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class LoginInteractorTest {

    @Test
    fun testSuccessfulLogin() {
        val loginService: LoginService = mock { on { it.login(any(), any()) } doReturn Observable.just(false) }
        val loginInteractor = LoginInteractor(loginService)

        loginInteractor.login("test@test.pl", "qwerty").test()
                .assertValue(PartialLoginViewState.LoginSuccess(false))
    }

    @Test
    fun testLoginWithError() {
        val exception = Exception("testException")
        val loginService: LoginService = mock { on { it.login(any(), any()) } doReturn Observable.error(exception) }
        val loginInteractor = LoginInteractor(loginService)

        loginInteractor.login("test@test.pl", "qwerty").test()
                .assertValueAt(0, PartialLoginViewState.ErrorState(exception))
                .assertValueAt(1, PartialLoginViewState.ErrorState(exception, true))
    }

    @Test
    fun whenUserIsLoggedInAndHasNoFilledAccountInteractorReturnsLoggedInState() {
        val loginService: LoginService = mock { on { it.isLoggedIn() } doReturn Observable.just(LoginData(true)) }
        val loginInteractor = LoginInteractor(loginService)

        loginInteractor.isLoggedIn().test()
                .assertValue(PartialLoginViewState.LoginSuccess(false))
    }

    @Test
    fun whenUserIsLoggedInAndHasFilledAccountInteractorReturnsLoggedInState() {
        val loginService: LoginService = mock { on { it.isLoggedIn() } doReturn Observable.just(LoginData(true, true)) }
        val loginInteractor = LoginInteractor(loginService)

        loginInteractor.isLoggedIn().test()
                .assertValue(PartialLoginViewState.LoginSuccess())
    }

    @Test
    fun whenUserIsNotLoggedInInteractorReturnsNotLoggedInState() {
        val loginService: LoginService = mock { on { it.isLoggedIn() } doReturn Observable.just(LoginData(false)) }
        val loginInteractor = LoginInteractor(loginService)

        loginInteractor.isLoggedIn().test()
                .assertValue { it is PartialLoginViewState.NotLoggedIn }
    }
}