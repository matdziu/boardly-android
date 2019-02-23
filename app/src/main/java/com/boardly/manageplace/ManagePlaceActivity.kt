package com.boardly.manageplace

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.discover.models.Place
import com.boardly.factories.ManagePlaceViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ManagePlaceActivity : BaseDrawerActivity(), ManagePlaceView {

    @Inject
    lateinit var managePlaceViewModelFactory: ManagePlaceViewModelFactory

    private lateinit var managePlaceViewModel: ManagePlaceViewModel

    private lateinit var placeDataSubject: PublishSubject<Place>
    private lateinit var placeDataFetchTriggerSubject: PublishSubject<Boolean>

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ManagePlaceActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_manage_place)
        super.onCreate(savedInstanceState)

        managePlaceViewModel = ViewModelProviders.of(this, managePlaceViewModelFactory)[ManagePlaceViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.manage_place_item)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        managePlaceViewModel.bind(this)
    }

    private fun initEmitters() {
        placeDataSubject = PublishSubject.create()
        placeDataFetchTriggerSubject = PublishSubject.create()
    }

    override fun onStop() {
        managePlaceViewModel.unbind()
        super.onStop()
    }

    override fun render(managePlaceViewState: ManagePlaceViewState) {

    }

    override fun placeDataEmitter(): Observable<Place> = placeDataSubject

    override fun fetchPlaceDataTriggerEmitter(): Observable<Boolean> = placeDataFetchTriggerSubject
}