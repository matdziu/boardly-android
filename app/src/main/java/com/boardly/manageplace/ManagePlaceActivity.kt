package com.boardly.manageplace

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.common.search.SearchResultData
import com.boardly.constants.LATITUDE
import com.boardly.constants.LONGITUDE
import com.boardly.constants.PICKED_SEARCH_RESULT
import com.boardly.constants.PLACE_PICK_REQUEST_CODE
import com.boardly.databinding.ActivityManagePlaceBinding
import com.boardly.discover.models.Place
import com.boardly.extensions.loadImageFromFile
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.ManagePlaceViewModelFactory
import com.boardly.gamescollection.GamesCollectionActivity
import com.boardly.manageplace.models.PlaceInputData
import com.boardly.pickplace.PickPlaceActivity
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.jakewharton.rxbinding2.view.RxView
//import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.File
import javax.inject.Inject

class ManagePlaceActivity : BaseDrawerActivity(), ManagePlaceView {

    @Inject
    lateinit var managePlaceViewModelFactory: ManagePlaceViewModelFactory

    private lateinit var managePlaceViewModel: ManagePlaceViewModel

    private lateinit var placeDataFetchTriggerSubject: PublishSubject<Boolean>
    private lateinit var placePickEventSubject: PublishSubject<Boolean>

    private var currentManagedPlace = Place()

    private var init = true

    private var selectedPlacePictureFile: File? = null

    private lateinit var binding: ActivityManagePlaceBinding

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ManagePlaceActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityManagePlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        managePlaceViewModel = ViewModelProviders.of(
            this,
            managePlaceViewModelFactory
        )[ManagePlaceViewModel::class.java]

        binding.pickPlaceButton.setOnClickListener { launchPlacePickScreen() }
        binding.editGamesCollectionButton.setOnClickListener {
            GamesCollectionActivity.startManageMode(
                this,
                currentManagedPlace.collectionId
            )
        }
        binding.placeImageView.setOnClickListener { askForImage() }
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.manage_place_item)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        managePlaceViewModel.bind(this)
        if (init) placeDataFetchTriggerSubject.onNext(true)
    }

    private fun initEmitters() {
        placeDataFetchTriggerSubject = PublishSubject.create()
        placePickEventSubject = PublishSubject.create()
    }

    override fun onStop() {
        init = false
        managePlaceViewModel.unbind()
        super.onStop()
    }

    override fun render(managePlaceViewState: ManagePlaceViewState) = with(managePlaceViewState) {
        currentManagedPlace = managedPlace
        binding.placeNameEditText.showError(!placeNameValid)
        binding.placeDescriptionEditText.showError(!placeDescriptionValid)
        binding.phoneNumberEditText.showError(!placeNumberValid)
        showNoLocationError(!placeLocationValid)
        if (render) {
            binding.placeNameEditText.setText(managedPlace.name)
            binding.placeDescriptionEditText.setText(managedPlace.description)
            binding.phoneNumberEditText.setText(managedPlace.phoneNumber)
            binding.pageLinkEditText.setText(managedPlace.pageLink)
            renderLocationTextView(managedPlace.locationName)
            loadImageFromUrl(
                binding.placeImageView,
                managedPlace.imageUrl,
                R.drawable.place_placeholder
            )
        }
        if (successfulUpdate) {
            Toast.makeText(this@ManagePlaceActivity, R.string.generic_success, Toast.LENGTH_SHORT)
                .show()
            finish()
        }
        showProgressBar(progress)
        showNoPartnershipText(!isPartner && !progress)
        showContentView(!progress && isPartner)
    }

    private fun renderLocationTextView(locationName: String) {
        if (locationName.isEmpty()) {
            binding.placeTextView.setText(R.string.place_text_placeholder)
        } else {
            binding.placeTextView.text = locationName
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showNoPartnershipText(show: Boolean) {
        if (show) {
            binding.noPartnershipTextView.visibility = View.VISIBLE
        } else {
            binding.noPartnershipTextView.visibility = View.GONE
        }
    }

    private fun showContentView(show: Boolean) {
        if (show) {
            binding.contentViewGroup.visibility = View.VISIBLE
        } else {
            binding.contentViewGroup.visibility = View.GONE
        }
    }

    private fun showNoLocationError(show: Boolean) {
        if (show) {
            binding.placeTextView.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
        } else {
            binding.placeTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryText
                )
            )
        }
    }

    override fun placeDataEmitter(): Observable<PlaceInputData> =
        RxView.clicks(binding.saveChangesButton).map {
            PlaceInputData(
                currentManagedPlace.copy(
                    name = binding.placeNameEditText.text.toString(),
                    description = binding.placeDescriptionEditText.text.toString(),
                    phoneNumber = binding.phoneNumberEditText.text.toString(),
                    pageLink = binding.pageLinkEditText.text.toString(),
                    locationName = currentManagedPlace.locationName,
                    placeLongitude = currentManagedPlace.placeLongitude,
                    placeLatitude = currentManagedPlace.placeLatitude
                ),
                selectedPlacePictureFile
            )
        }

    override fun fetchPlaceDataTriggerEmitter(): Observable<Boolean> = placeDataFetchTriggerSubject

    override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                PLACE_PICK_REQUEST_CODE -> handlePlacePickResult(resultCode, data)
//                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> handlePlacePictureResult(
//                    resultCode,
//                    data
//                )
            }
        }
    }

    private fun launchPlacePickScreen() {
        startActivityForResult(Intent(this, PickPlaceActivity::class.java), PLACE_PICK_REQUEST_CODE)
    }

    private fun handlePlacePictureResult(resultCode: Int, data: Intent) {
//        val result = CropImage.getActivityResult(data)
//        if (resultCode == RESULT_OK) {
//            val imageFile = File(result.uri.path)
//            selectedPlacePictureFile = imageFile
//            loadImageFromFile(binding.placeImageView, imageFile)
//        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//            Toast.makeText(this, R.string.generic_error, Toast.LENGTH_SHORT).show()
//        }
    }

    private fun handlePlacePickResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = data.getParcelableExtra<SearchResultData>(PICKED_SEARCH_RESULT)
                with(place) {
                    currentManagedPlace.placeLatitude = this?.additionalInfo?.get(LATITUDE)?.toDouble() ?: 0.0
                    currentManagedPlace.placeLongitude = this?.additionalInfo?.get(LATITUDE)?.toDouble()
                        ?: 0.0
                    currentManagedPlace.locationName = this?.title ?: ""
                    binding.placeTextView.text = title

                    // Somehow default Places API Activity does not trigger onStop() of EventActivity
                    // so it's ok to emit event here
                    placePickEventSubject.onNext(true)
                }
            }

            PlaceAutocomplete.RESULT_ERROR -> showErrorToast(R.string.generic_error)
            Activity.RESULT_CANCELED -> hideSoftKeyboard()
        }
    }


    private fun showErrorToast(@StringRes errorTextId: Int) {
        Toast.makeText(this, errorTextId, Toast.LENGTH_LONG).show()
    }

    private fun askForImage() {
//        CropImage.activity()
//            .setAspectRatio(1, 1)
//            .start(this)
    }
}