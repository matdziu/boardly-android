package com.boardly.home

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.boardly.R
import com.boardly.addevent.AddEventActivity
import com.boardly.base.BaseDrawerActivity
import com.boardly.factories.HomeViewModelFactory
import com.boardly.home.list.EventsAdapter
import com.boardly.home.models.Event
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_home.addEventButton
import kotlinx.android.synthetic.main.activity_home.eventsRecyclerView
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity(), HomeView {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var initialFetchSubject: PublishSubject<Boolean>

    private val eventsAdapter = EventsAdapter()

    private var init = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)
        initRecyclerView()

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]
        addEventButton.setOnClickListener { startActivity(Intent(this, AddEventActivity::class.java)) }

        requestLocationPermission()
    }

    private fun initRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter

        eventsAdapter.submitList(listOf(
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, ""),
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, ""),
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, ""),
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, ""),
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, ""),
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, ""),
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, ""),
                Event("1", "Zapraszam wszystkich na granie", "Eurobusiness", "2", "Domówka", 11112223344, 5, 3, "")
        ))
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        homeViewModel.bind(this)
        initialFetchSubject.onNext(init)
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.events_item)
    }

    override fun onStop() {
        init = false
        homeViewModel.unbind()
        super.onStop()
    }

    private fun initEmitters() {
        initialFetchSubject = PublishSubject.create()
    }

    override fun emitInitialFetchTrigger(): Observable<Boolean> = initialFetchSubject

    override fun render(homeViewState: HomeViewState) {

    }

    private fun requestLocationPermission() {
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe {
                    if (!it) {
                        finish()
                        Toast.makeText(this, R.string.location_permission_denied_text, Toast.LENGTH_LONG).show()
                    }
                }
    }
}