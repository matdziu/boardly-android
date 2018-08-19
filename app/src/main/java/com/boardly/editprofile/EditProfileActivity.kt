package com.boardly.editprofile

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.constants.SHOW_HAMBURGER_MENU
import com.boardly.editprofile.models.InputData
import com.boardly.extensions.loadImageFromFile
import com.boardly.extensions.loadImageFromUrl
import com.boardly.extensions.toString
import com.boardly.factories.EditProfileViewModelFactory
import com.boardly.home.HomeActivity
import com.jakewharton.rxbinding2.view.RxView
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_edit_profile.nameEditText
import kotlinx.android.synthetic.main.activity_edit_profile.profilePictureImageView
import kotlinx.android.synthetic.main.activity_edit_profile.ratingTextView
import kotlinx.android.synthetic.main.activity_edit_profile.saveChangesButton
import kotlinx.android.synthetic.main.activity_sign_up.contentViewGroup
import kotlinx.android.synthetic.main.activity_sign_up.progressBar
import java.io.File
import javax.inject.Inject

class EditProfileActivity : BaseDrawerActivity(), EditProfileView {

    private lateinit var editProfileViewModel: EditProfileViewModel

    private var selectedProfilePictureFile: File? = null

    @Inject
    lateinit var editProfileViewModelFactory: EditProfileViewModelFactory

    private lateinit var fetchProfileDataSubject: PublishSubject<Boolean>

    private var init = true

    private var showHamburgerMenu = false

    companion object {

        fun start(context: Context, showHamburgerMenu: Boolean) {
            val intent = Intent(context, EditProfileActivity::class.java)
            if (!showHamburgerMenu) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            intent.putExtra(SHOW_HAMBURGER_MENU, showHamburgerMenu)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_edit_profile)
        super.onCreate(savedInstanceState)

        showHamburgerMenu = intent.getBooleanExtra(SHOW_HAMBURGER_MENU, false)

        editProfileViewModel = ViewModelProviders.of(this, editProfileViewModelFactory)[EditProfileViewModel::class.java]
        profilePictureImageView.setOnClickListener { askForImage() }

        showHamburgerIcon(showHamburgerMenu)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        editProfileViewModel.bind(this)
        fetchProfileDataSubject.onNext(init)
    }

    private fun initEmitters() {
        fetchProfileDataSubject = PublishSubject.create()
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.profile_item)
    }

    override fun onStop() {
        init = false
        editProfileViewModel.unbind()
        super.onStop()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val imageFile = File(result.uri.path)
                selectedProfilePictureFile = imageFile
                loadImageFromFile(profilePictureImageView, imageFile)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, R.string.generic_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun render(editProfileViewState: EditProfileViewState) {
        with(editProfileViewState) {
            showProgress(progress)
            nameEditText.showError(nameFieldEmpty)
            if (successfulUpdate && !showHamburgerMenu) {
                startActivity(Intent(this@EditProfileActivity, HomeActivity::class.java))
                finish()
            } else if (successfulUpdate) {
                finish()
            }

            if (render) {
                nameEditText.setText(profileData.name)
                profileData.profilePicture?.let { loadImageFromUrl(profilePictureImageView, it, R.drawable.profile_picture_shape) }
                ratingTextView.text = profileData.rating?.toString("#.#") ?: "-"
            }
        }
    }

    override fun inputDataEmitter(): Observable<InputData> {
        return RxView.clicks(saveChangesButton)
                .doOnNext { hideSoftKeyboard() }
                .map { InputData(nameEditText.text.toString().trim(), selectedProfilePictureFile) }
    }

    override fun fetchProfileDataTriggerEmitter(): Observable<Boolean> = fetchProfileDataSubject

    private fun askForImage() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this)
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