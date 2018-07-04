package com.boardly.pickgame

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.boardly.R
import com.boardly.base.BaseSearchActivity
import com.boardly.factories.PickGameViewModelFactory
import com.boardly.pickgame.list.SearchResultsAdapter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_pick_game.searchResultsRecyclerView
import javax.inject.Inject

class PickGameActivity : BaseSearchActivity(), PickGameView {

    @Inject
    lateinit var pickGameViewModelFactory: PickGameViewModelFactory

    private lateinit var pickGameViewModel: PickGameViewModel

    private val searchResultsAdapter = SearchResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_pick_game)
        super.onCreate(savedInstanceState)
        initRecyclerView()

        pickGameViewModel = ViewModelProviders.of(this, pickGameViewModelFactory)[PickGameViewModel::class.java]
    }

    private fun initRecyclerView() {
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsRecyclerView.adapter = searchResultsAdapter
    }

    override fun onStart() {
        super.onStart()
        pickGameViewModel.bind(this)
    }

    override fun onStop() {
        pickGameViewModel.unbind()
        super.onStop()
    }

    override fun render(pickGameViewState: PickGameViewState) {

    }
}