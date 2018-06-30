package com.boardly.editprofile

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.editprofile.models.InputData
import com.boardly.factories.EditProfileViewModelFactory
import com.boardly.injection.modules.GlideApp
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

class EditProfileActivity : BaseActivity(), EditProfileView {

    private lateinit var editProfileViewModel: EditProfileViewModel

    private var selectedProfilePictureFile: File? = null

    @Inject
    lateinit var editProfileViewModelFactory: EditProfileViewModelFactory

    private lateinit var fetchProfileDataSubject: PublishSubject<Boolean>

    private var init = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_edit_profile)
        super.onCreate(savedInstanceState)

        editProfileViewModel = ViewModelProviders.of(this, editProfileViewModelFactory)[EditProfileViewModel::class.java]
        profilePictureImageView.setOnClickListener { askForImage() }
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
            if (successfulUpdate) finish()

            if (render) {
                nameEditText.setText(profileData.name)
                profileData.profilePicture?.let { loadImageFromUrl(profilePictureImageView, it) }
                ratingTextView.text = profileData.rating?.toString() ?: "-"
            }
        }
    }

    override fun emitInputData(): Observable<InputData> {
        return RxView.clicks(saveChangesButton)
                .doOnNext { hideSoftKeyboard() }
                .map { InputData(nameEditText.text.toString(), selectedProfilePictureFile) }
    }

    override fun emitFetchProfileDataTrigger(): Observable<Boolean> = fetchProfileDataSubject

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

    private fun loadImageFromFile(imageView: ImageView, pictureFile: File) {
        GlideApp.with(this)
                .load(pictureFile)
                .into(imageView)
    }
}