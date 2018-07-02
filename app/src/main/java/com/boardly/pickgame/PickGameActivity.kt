package com.boardly.pickgame

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseSearchActivity
import com.boardly.factories.PickGameViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class PickGameActivity : BaseSearchActivity(), PickGameView {

    @Inject
    lateinit var pickGameViewModelFactory: PickGameViewModelFactory

    lateinit var pickGameViewModel: PickGameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_pick_game)
        super.onCreate(savedInstanceState)

        pickGameViewModel = ViewModelProviders.of(this, pickGameViewModelFactory)[PickGameViewModel::class.java]
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