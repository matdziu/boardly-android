package com.boardly.gamescollection

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.COLLECTION_ID
import com.boardly.factories.GamesCollectionViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class GamesCollectionActivity : BaseActivity(), GamesCollectionView {

    @Inject
    lateinit var gamesCollectionViewModelFactory: GamesCollectionViewModelFactory

    private lateinit var gamesCollectionViewModel: GamesCollectionViewModel

    private var collectionId = ""

    companion object {
        fun start(context: Context, collectionId: String) {
            val intent = Intent(context, GamesCollectionActivity::class.java)
            intent.putExtra(COLLECTION_ID, collectionId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_games_collection)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        gamesCollectionViewModel = ViewModelProviders.of(this, gamesCollectionViewModelFactory)[GamesCollectionViewModel::class.java]

        collectionId = intent.getStringExtra(COLLECTION_ID)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        gamesCollectionViewModel.bind(this, collectionId)
    }

    private fun initEmitters() {

    }

    override fun onStop() {
        gamesCollectionViewModel.unbind()
        super.onStop()
    }

    override fun render(gamesCollectionViewState: GamesCollectionViewState) {

    }
}