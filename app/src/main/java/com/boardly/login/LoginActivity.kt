package com.boardly.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.SIGN_IN_REQUEST_CODE
import com.boardly.databinding.ActivityLoginBinding
import com.boardly.editprofile.EditProfileActivity
import com.boardly.factories.LoginViewModelFactory
import com.boardly.home.HomeActivity
import com.boardly.login.models.InputData
import com.boardly.signup.SignUpActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginView {

    private lateinit var googleSignInSubject: Subject<GoogleSignInAccount>
    private lateinit var facebookSignInSubject: Subject<AccessToken>
    private lateinit var initialLoginCheckSubject: Subject<Boolean>

    private lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var loginViewModelFactory: LoginViewModelFactory

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var loginManager: LoginManager

    private var init = true

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        binding.createAccountButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
        loginViewModel =
            ViewModelProviders.of(this, loginViewModelFactory)[LoginViewModel::class.java]

        initFacebookSignInCallback()

        binding.createAccountButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.loginWithGoogleButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, SIGN_IN_REQUEST_CODE)
        }
        binding.loginWithFacebookButton.setOnClickListener {
            loginManager.logInWithReadPermissions(this, arrayListOf("email"))
        }
        binding.privacyPolicyTextView.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW)
            viewIntent.data = Uri.parse("https://boardly.github.io/")
            startActivity(viewIntent)
        }
    }


    override fun onStart() {
        super.onStart()
        initEmitters()
        loginViewModel.bind(this)
        initialLoginCheckSubject.onNext(init)
    }

    private fun initEmitters() {
        googleSignInSubject = PublishSubject.create()
        facebookSignInSubject = PublishSubject.create()
        initialLoginCheckSubject = PublishSubject.create()
    }

    override fun onStop() {
        init = false
        loginViewModel.unbind()
        super.onStop()
    }

    private fun initFacebookSignInCallback() {
        loginManager.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    facebookSignInSubject.onNext(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.e("FacebookSignIn", "onCancel()")
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_error_text),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.contentViewGroup.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.contentViewGroup.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                googleSignInSubject.onNext(account)
            } catch (exception: ApiException) {
                if (exception.statusCode == GoogleSignInStatusCodes.SIGN_IN_FAILED || exception.statusCode == CommonStatusCodes.NETWORK_ERROR) {
                    Toast.makeText(this, getString(R.string.login_error_text), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun googleSignInEmitter(): Observable<GoogleSignInAccount> = googleSignInSubject

    override fun facebookSignInEmitter(): Observable<AccessToken> = facebookSignInSubject

    override fun inputEmitter(): Observable<InputData> {
        return RxView.clicks(binding.loginButton).map {
            InputData(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }
    }

    override fun initialLoginCheckEmitter(): Observable<Boolean> = initialLoginCheckSubject

    override fun render(loginViewState: LoginViewState) {
        hideSoftKeyboard()
        showProgress(loginViewState.inProgress)
        binding.emailEditText.showError(!loginViewState.emailValid)
        binding.passwordEditText.showError(!loginViewState.passwordValid)

        if (loginViewState.error && !loginViewState.dismissToast) {
            Toast.makeText(this, getString(loginViewState.errorMessageId), Toast.LENGTH_SHORT)
                .show()
        }

        if (loginViewState.loginSuccess && loginViewState.isProfileFilled) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else if (loginViewState.loginSuccess && !loginViewState.isProfileFilled) {
            EditProfileActivity.start(this, false)
            finish()
        }
    }
}