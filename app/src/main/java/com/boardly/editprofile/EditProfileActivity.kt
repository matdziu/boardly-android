package com.boardly.editprofile

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.factories.EditProfileViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_edit_profile.nameEditText
import kotlinx.android.synthetic.main.activity_sign_up.contentViewGroup
import kotlinx.android.synthetic.main.activity_sign_up.progressBar
import java.io.File
import javax.inject.Inject

class EditProfileActivity : BaseActivity(), EditProfileView {

    private lateinit var editProfileViewModel: EditProfileViewModel

    @Inject
    lateinit var editProfileViewModelFactory: EditProfileViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_edit_profile)
        super.onCreate(savedInstanceState)

        editProfileViewModel = ViewModelProviders.of(this, editProfileViewModelFactory)[EditProfileViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        editProfileViewModel.bind(this)
    }

    override fun onStop() {
        editProfileViewModel.unbind()
        super.onStop()
    }

    override fun render(editProfileViewState: EditProfileViewState) {
        with(editProfileViewState) {
            showProgress(progress)
            nameEditText.showError(nameFieldEmpty)
            if (success) finish()
        }
    }

    override fun emitInputData(): Observable<InputData> {
        return Observable.just(InputData("lal", File("aa")))
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            contentViewGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentViewGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}