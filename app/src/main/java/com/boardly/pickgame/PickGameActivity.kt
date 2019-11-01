package com.boardly.pickgame

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.boardly.R
import com.boardly.base.BaseSearchActivity
import com.boardly.common.search.SearchResultsAdapter
import com.boardly.factories.PickGameViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_pick_game.noSearchResultsTextView
import kotlinx.android.synthetic.main.activity_pick_game.progressBar
import kotlinx.android.synthetic.main.activity_pick_game.searchResultsRecyclerView
import kotlinx.android.synthetic.main.activity_pick_game.timeOutTextView
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Deprecated("Can't use BGG API")
class PickGameActivity : BaseSearchActivity(), PickGameView {

    override val searchHintResId: Int = R.string.search_game_hint

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
        initEmitters()
        pickGameViewModel.bind(this)
    }

    override fun onStop() {
        pickGameViewModel.unbind()
        super.onStop()
    }

    override fun render(pickGameViewState: PickGameViewState) {
        with(pickGameViewState) {
            showProgressBar(progress)
            showNoResultsPrompt(searchResults.isEmpty() && !progress && error == null)
            showContent(searchResults.isNotEmpty() && !progress && error == null)
            showTimeOutPrompt(error is SocketTimeoutException, unacceptedQuery)
//            searchResultsAdapter.submitList(searchResults)
        }
    }

    private fun showProgressBar(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private fun showNoResultsPrompt(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        noSearchResultsTextView.visibility = visibility
        noSearchResultsTextView.text = getString(R.string.no_search_results_text)
    }

    private fun showContent(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        searchResultsRecyclerView.visibility = visibility
    }

    private fun showTimeOutPrompt(show: Boolean, query: String) {
        val visibility = if (show) View.VISIBLE else View.GONE
        timeOutTextView.visibility = visibility
        timeOutTextView.text = getString(R.string.be_more_specific_error_text, query)
    }

    override fun queryEmitter(): Observable<String> =
            searchInput.debounce(300, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
}