package com.boardly.gamescollection

import android.app.Activity
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
import com.boardly.constants.MODE
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_FIRST_GAME_REQUEST_CODE
import com.boardly.factories.GamesCollectionViewModelFactory
import com.boardly.gamescollection.list.CollectionGamesAdapter
import com.boardly.gamescollection.models.CollectionGame
import com.boardly.pickgame.PickGameActivity
import com.boardly.pickgame.dialog.addGameDialog
import com.boardly.retrofit.gameservice.models.SearchResult
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_games_collection.addGameButton
import kotlinx.android.synthetic.main.activity_games_collection.gamesCollectionRecyclerView
import kotlinx.android.synthetic.main.activity_games_collection.hintTextView
import kotlinx.android.synthetic.main.activity_games_collection.noGamesTextView
import kotlinx.android.synthetic.main.activity_games_collection.progressBar
import kotlinx.android.synthetic.main.activity_games_collection.rootView
import javax.inject.Inject

enum class Mode {
    VIEW, MANAGE
}

class GamesCollectionActivity : BaseSearchActivity(), GamesCollectionView {

    @Inject
    lateinit var gamesCollectionViewModelFactory: GamesCollectionViewModelFactory

    private lateinit var gamesCollectionViewModel: GamesCollectionViewModel

    private var collectionId = ""

    private var init = true

    private var progress = false

    private lateinit var initialFetchTriggerSubject: PublishSubject<Boolean>
    private lateinit var newGameSubject: PublishSubject<CollectionGame>
    private lateinit var deleteGameSubject: PublishSubject<String>

    private lateinit var collectionGamesAdapter: CollectionGamesAdapter

    override val searchHintResId: Int = R.string.search_game_hint

    private lateinit var mode: Mode

    private var recentlyPickedGame: CollectionGame? = null

    companion object {
        fun startViewMode(context: Context, collectionId: String) {
            val intent = Intent(context, GamesCollectionActivity::class.java)
            intent.putExtra(COLLECTION_ID, collectionId)
            intent.putExtra(MODE, Mode.VIEW)
            context.startActivity(intent)
        }

        fun startManageMode(context: Context, collectionId: String) {
            val intent = Intent(context, GamesCollectionActivity::class.java)
            intent.putExtra(COLLECTION_ID, collectionId)
            intent.putExtra(MODE, Mode.MANAGE)
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
        mode = intent.getSerializableExtra(MODE) as Mode
        initRecyclerView()
        prepareMode(mode)
        addGameButton.setOnClickListener { addGameDialog(rootView) { handlePickGameResult(it) } }
        initWithKeyboard = false
    }

    private fun launchGamePickScreen(requestCode: Int) {
        val pickGameIntent = Intent(this, PickGameActivity::class.java)
        startActivityForResult(pickGameIntent, requestCode)
    }

    private fun prepareMode(mode: Mode) {
        if (mode == Mode.VIEW) {
            addGameButton.visibility = View.GONE
        } else if (mode == Mode.MANAGE) {
            addGameButton.visibility = View.VISIBLE
        }
    }

    private fun initRecyclerView() {
        collectionGamesAdapter = CollectionGamesAdapter(mode)
        gamesCollectionRecyclerView.layoutManager = LinearLayoutManager(this)
        gamesCollectionRecyclerView.adapter = collectionGamesAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                PICK_FIRST_GAME_REQUEST_CODE -> handlePickGameResult(resultCode, data)
            }
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                val collectionGame = CollectionGame(
                        pickedGame.id.toString(),
                        pickedGame.name,
                        pickedGame.yearPublished)
                recentlyPickedGame = collectionGame
            }
        }
    }

    private fun handlePickGameResult(gameName: String) {
        val collectionGame = CollectionGame(name = gameName)
        newGameSubject.onNext(collectionGame)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        gamesCollectionViewModel.bind(this, collectionId)
        if (init) initialFetchTriggerSubject.onNext(true)
        if (recentlyPickedGame != null) {
            newGameSubject.onNext(recentlyPickedGame!!)
            recentlyPickedGame = null
        }
    }

    override fun initEmitters() {
        super.initEmitters()
        initialFetchTriggerSubject = PublishSubject.create()
        newGameSubject = PublishSubject.create()
        deleteGameSubject = PublishSubject.create()
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
        showNoMoreLimitToast(noMoreLimit)
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
            initialFetchTriggerSubject.onNext(false)
        }
    }

    private fun showNoMoreLimitToast(show: Boolean) {
        if (show) {
            Toast.makeText(this, R.string.no_more_limit, Toast.LENGTH_SHORT).show()
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

    override fun emitGameDeletion(gameId: String) {
        deleteGameSubject.onNext(gameId)
    }

    override fun initialFetchTriggerEmitter(): Observable<Boolean> {
        return initialFetchTriggerSubject
    }

    override fun newGameEmitter(): Observable<CollectionGame> {
        return newGameSubject
    }

    override fun deleteGameEmitter(): Observable<String> {
        return deleteGameSubject
    }
}