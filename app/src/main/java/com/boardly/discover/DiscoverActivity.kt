package com.boardly.discover

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.factories.DiscoverViewModelFactory
import com.boardly.filter.models.Filter
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class DiscoverActivity : BaseActivity(), DiscoverView {

    @Inject
    lateinit var discoverViewModelFactory: DiscoverViewModelFactory

    private lateinit var discoverViewModel: DiscoverViewModel

    private lateinit var fetchPlacesListTriggerSubject: PublishSubject<Filter>

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, DiscoverActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_discover)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        discoverViewModel = ViewModelProviders.of(this, discoverViewModelFactory)[DiscoverViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        discoverViewModel.bind(this)

    }

    private fun initEmitters() {
        fetchPlacesListTriggerSubject = PublishSubject.create()
    }

    override fun onStop() {
        discoverViewModel.unbind()
        super.onStop()
    }

    override fun fetchPlacesListTrigger(): Observable<Filter> {
        return fetchPlacesListTriggerSubject
    }

    override fun render(discoverViewState: DiscoverViewState) {

    }
}