package com.boardly.signup

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.boardly.base.BaseActivity
import com.boardly.databinding.ActivitySignUpBinding
import com.boardly.editprofile.EditProfileActivity
import com.boardly.factories.SignUpViewModelFactory
import com.boardly.signup.models.InputData
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import javax.inject.Inject

class SignUpActivity : BaseActivity(), SignUpView {

    private lateinit var signUpViewModel: SignUpViewModel

    @Inject
    lateinit var signUpViewModelFactory: SignUpViewModelFactory

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        signUpViewModel =
            ViewModelProviders.of(this, signUpViewModelFactory)[SignUpViewModel::class.java]
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
        binding.emailEditText.showError(!signUpViewState.emailValid)
        binding.passwordEditText.showError(!signUpViewState.passwordValid)

        if (signUpViewState.error && !signUpViewState.dismissToast) {
            Toast.makeText(this, getString(signUpViewState.errorMessageId), Toast.LENGTH_SHORT)
                .show()
        }

        if (signUpViewState.signUpSuccess) {
            EditProfileActivity.start(this, false)
            finish()
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.contentViewGroup.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            showBackToolbarArrow(false)
        } else {
            binding.contentViewGroup.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            showBackToolbarArrow(true, this::finish)
        }
    }

    override fun inputEmitter(): Observable<InputData> {
        return RxView.clicks(binding.createAccountButton).map {
            InputData(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }
    }
}