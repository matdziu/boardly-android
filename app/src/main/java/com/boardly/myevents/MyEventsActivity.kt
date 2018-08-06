package com.boardly.myevents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.factories.MyEventsViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MyEventsActivity : BaseDrawerActivity(), MyEventsView {

    private lateinit var fetchEventsSubject: PublishSubject<Boolean>

    private lateinit var myEventsViewModel: MyEventsViewModel

    @Inject
    lateinit var myEventsViewModelFactory: MyEventsViewModelFactory

    private var init = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_my_events)
        super.onCreate(savedInstanceState)

        myEventsViewModel = ViewModelProviders.of(this, myEventsViewModelFactory)[MyEventsViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.my_events_item)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        myEventsViewModel.bind(this)

        fetchEventsSubject.onNext(init)
        init = false
    }

    override fun onStop() {
        myEventsViewModel.unbind()
        super.onStop()
    }

    private fun initEmitters() {
        fetchEventsSubject = PublishSubject.create()
    }

    override fun fetchEventsTriggerEmitter(): Observable<Boolean> = fetchEventsSubject

    override fun render(myEventsViewState: MyEventsViewState) {

    }
}