package com.boardly.pickcity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseSearchActivity
import com.boardly.factories.PickCityViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class PickCityActivity : BaseSearchActivity(), PickCityView {

    @Inject
    lateinit var pickCityViewModelFactory: PickCityViewModelFactory

    lateinit var pickCityViewModel: PickCityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_pick_city)
        super.onCreate(savedInstanceState)

        pickCityViewModel = ViewModelProviders.of(this, pickCityViewModelFactory)[PickCityViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        pickCityViewModel.bind(this)
    }

    override fun onStop() {
        pickCityViewModel.unbind()
        super.onStop()
    }

    override fun render(pickCityViewState: PickCityViewState) {

    }
}