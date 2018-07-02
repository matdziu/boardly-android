package com.boardly.addevent

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.boardly.factories.AddEventViewModelFactory
import com.boardly.pickgame.PickGameActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_event.pickGameButton
import kotlinx.android.synthetic.main.activity_add_event.pickPlaceButton
import javax.inject.Inject

class AddEventActivity : BaseActivity(), AddEventView {

    @Inject
    lateinit var addEventViewModelFactory: AddEventViewModelFactory

    private lateinit var addEventViewModel: AddEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_add_event)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        addEventViewModel = ViewModelProviders.of(this, addEventViewModelFactory)[AddEventViewModel::class.java]

        pickGameButton.setOnClickListener { startActivity(Intent(this, PickGameActivity::class.java)) }
        pickPlaceButton.setOnClickListener { launchPlaceSearch() }
    }

    override fun onStart() {
        super.onStart()
        addEventViewModel.bind(this)
    }

    override fun onStop() {
        addEventViewModel.unbind()
        super.onStop()
    }

    override fun render(addEventViewState: AddEventViewState) {

    }

    private fun launchPlaceSearch() {
        try {
            val placeSearchIntent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this)
            startActivityForResult(placeSearchIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {

        } catch (e: GooglePlayServicesNotAvailableException) {

        }
    }
}