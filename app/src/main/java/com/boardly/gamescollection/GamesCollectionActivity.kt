package com.boardly.gamescollection

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseSearchActivity
import com.boardly.constants.COLLECTION_ID
import com.boardly.factories.GamesCollectionViewModelFactory
import com.boardly.gamescollection.list.CollectionGamesAdapter
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_games_collection.gamesCollectionRecyclerView
import kotlinx.android.synthetic.main.activity_games_collection.hintTextView
import kotlinx.android.synthetic.main.activity_games_collection.noGamesTextView
import kotlinx.android.synthetic.main.activity_games_collection.progressBar
import javax.inject.Inject

class GamesCollectionActivity : BaseSearchActivity(), GamesCollectionView {

    @Inject
    lateinit var gamesCollectionViewModelFactory: GamesCollectionViewModelFactory

    private lateinit var gamesCollectionViewModel: GamesCollectionViewModel

    private var collectionId = ""

    private var init = true

    private var progress = false

    private lateinit var initialFetchTriggerSubject: PublishSubject<Boolean>

    private val collectionGamesAdapter = CollectionGamesAdapter()

    override val searchHintResId: Int = R.string.search_game_hint

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
        initRecyclerView()
    }

    private fun initRecyclerView() {
        gamesCollectionRecyclerView.layoutManager = LinearLayoutManager(this)
        gamesCollectionRecyclerView.adapter = collectionGamesAdapter
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        gamesCollectionViewModel.bind(this, collectionId)
        if (init) initialFetchTriggerSubject.onNext(true)
    }

    override fun initEmitters() {
        super.initEmitters()
        initialFetchTriggerSubject = PublishSubject.create()
    }

    override fun onStop() {
        init = false
        gamesCollectionViewModel.unbind()
        super.onStop()
    }

    override fun render(gamesCollectionViewState: GamesCollectionViewState) = with(gamesCollectionViewState) {
        showProgressBar(progress)
        showSuccessToast(success)
        showNoGamesText(games.isEmpty() && !progress)
        collectionGamesAdapter.submitList(games)
    }

    private fun showProgressBar(show: Boolean) {
        progress = show
        if (show) {
            progressBar.visibility = View.VISIBLE
            hintTextView.visibility = View.GONE
            gamesCollectionRecyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            hintTextView.visibility = View.VISIBLE
            gamesCollectionRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showSuccessToast(show: Boolean) {
        if (show) {
            Toast.makeText(this, R.string.generic_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoGamesText(show: Boolean) {
        if (show) {
            noGamesTextView.visibility = View.VISIBLE
        } else {
            noGamesTextView.visibility = View.GONE
        }
    }

    override fun queryEmitter(): Observable<String> = searchInput.filter { !progress }

    override fun initialFetchTriggerEmitter(): Observable<Boolean> {
        return initialFetchTriggerSubject
    }
}