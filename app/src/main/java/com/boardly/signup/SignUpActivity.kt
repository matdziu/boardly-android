package com.boardly.signup

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.factories.SignUpViewModelFactory
import com.boardly.home.HomeActivity
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_sign_up.contentViewGroup
import kotlinx.android.synthetic.main.activity_sign_up.createAccountButton
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_up.progressBar
import javax.inject.Inject

class SignUpActivity : BaseActivity(), SignUpView {

    private lateinit var signUpViewModel: SignUpViewModel

    @Inject
    lateinit var signUpViewModelFactory: SignUpViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_sign_up)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        signUpViewModel = ViewModelProviders.of(this, signUpViewModelFactory)[SignUpViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        signUpViewModel.bind(this)
    }

    override fun onStop() {
        signUpViewModel.unbind()
        super.onStop()
    }

    override fun render(signUpViewState: SignUpViewState) {
        hideSoftKeyboard()
        showProgress(signUpViewState.inProgress)
        emailEditText.showError(!signUpViewState.emailValid)
        passwordEditText.showError(!signUpViewState.passwordValid)

        if (signUpViewState.error && !signUpViewState.dismissToast) {
            Toast.makeText(this, getString(signUpViewState.errorMessageId), Toast.LENGTH_SHORT).show()
        }

        if (signUpViewState.signUpSuccess) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            contentViewGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            showBackToolbarArrow(false)
        } else {
            contentViewGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            showBackToolbarArrow(true, this::finish)
        }
    }

    override fun emitInput(): Observable<InputData> {
        return RxView.clicks(createAccountButton).map {
            InputData(emailEditText.text.toString(),
                    passwordEditText.text.toString())
        }
    }
}