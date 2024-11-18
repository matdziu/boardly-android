package com.boardly.pickplace

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.boardly.R
import com.boardly.base.BaseSearchActivity
import com.boardly.common.search.SearchResultsAdapter
import com.boardly.databinding.ActivityPickPlaceBinding
import com.boardly.factories.PickPlaceViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PickPlaceActivity : BaseSearchActivity(), PickPlaceView {

    override val searchHintResId: Int = R.string.search_place_hint

    @Inject
    lateinit var pickPlaceViewModelFactory: PickPlaceViewModelFactory

    private lateinit var pickPlaceViewModel: PickPlaceViewModel

    private val searchResultsAdapter = SearchResultsAdapter()

    private lateinit var binding: ActivityPickPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityPickPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        initRecyclerView()

        pickPlaceViewModel =
            ViewModelProviders.of(this, pickPlaceViewModelFactory)[PickPlaceViewModel::class.java]
    }

    private fun initRecyclerView() {
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchResultsRecyclerView.adapter = searchResultsAdapter
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        pickPlaceViewModel.bind(this)
    }

    override fun onStop() {
        pickPlaceViewModel.unbind()
        super.onStop()
    }

    override fun render(pickPlaceViewState: PickPlaceViewState) {
        with(pickPlaceViewState) {
            showProgressBar(progress)
            showNoResultsPrompt(searchResults.isEmpty() && !progress && error == null)
            showContent(searchResults.isNotEmpty() && !progress && error == null)
            showTimeOutPrompt(error is SocketTimeoutException, unacceptedQuery)
            searchResultsAdapter.submitList(searchResults)
        }
    }

    private fun showProgressBar(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        binding.progressBar.visibility = visibility
    }

    private fun showNoResultsPrompt(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        binding.noSearchResultsTextView.visibility = visibility
        binding.noSearchResultsTextView.text = getString(R.string.no_search_results_text)
    }

    private fun showContent(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        binding.searchResultsRecyclerView.visibility = visibility
    }

    private fun showTimeOutPrompt(show: Boolean, query: String) {
        val visibility = if (show) View.VISIBLE else View.GONE
        binding.timeOutTextView.visibility = visibility
        binding.timeOutTextView.text = getString(R.string.be_more_specific_error_text, query)
    }

    override fun queryEmitter(): Observable<String> =
        searchInput.debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
}