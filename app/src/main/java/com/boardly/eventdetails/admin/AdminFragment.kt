package com.boardly.eventdetails.admin

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.events.EventUIRenderer
import com.boardly.common.events.models.Event
import com.boardly.constants.EVENT
import com.boardly.eventdetails.admin.list.AcceptedPlayersAdapter
import com.boardly.eventdetails.admin.list.PendingPlayersAdapter
import com.boardly.factories.AdminViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_admin.acceptedPlayersRecyclerView
import kotlinx.android.synthetic.main.fragment_admin.acceptedProgressBar
import kotlinx.android.synthetic.main.fragment_admin.noAcceptedPlayersTextView
import kotlinx.android.synthetic.main.fragment_admin.noPendingPlayersTextView
import kotlinx.android.synthetic.main.fragment_admin.pendingPlayersRecyclerView
import kotlinx.android.synthetic.main.fragment_admin.pendingProgressBar
import kotlinx.android.synthetic.main.item_event.seeDescriptionButton
import kotlinx.android.synthetic.main.layout_event.boardGameImageView
import kotlinx.android.synthetic.main.layout_event.eventNameTextView
import kotlinx.android.synthetic.main.layout_event.gameTextView
import kotlinx.android.synthetic.main.layout_event.levelTextView
import kotlinx.android.synthetic.main.layout_event.locationTextView
import kotlinx.android.synthetic.main.layout_event.timeTextView
import javax.inject.Inject

class AdminFragment : Fragment(), AdminView {

    @Inject
    lateinit var eventUIRenderer: EventUIRenderer

    @Inject
    lateinit var adminViewModelFactory: AdminViewModelFactory

    lateinit var adminViewModel: AdminViewModel

    private lateinit var fetchEventPlayersTriggerSubject: PublishSubject<Boolean>
    private var init = true
    private var event = Event()

    lateinit var acceptPlayerSubject: PublishSubject<String>
    lateinit var kickPlayerSubject: PublishSubject<String>

    private val acceptedPlayersAdapter = AcceptedPlayersAdapter(this)
    private val pendingPlayersAdapter = PendingPlayersAdapter(this)

    companion object {
        fun newInstance(event: Event): AdminFragment {
            val adminFragment = AdminFragment()
            val arguments = Bundle()
            arguments.putParcelable(EVENT, event)
            adminFragment.arguments = arguments
            return adminFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        event = arguments?.getParcelable(EVENT) ?: Event()

        adminViewModel = ViewModelProviders.of(this, adminViewModelFactory)[AdminViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventUIRenderer.displayEventInfo(event,
                eventNameTextView,
                gameTextView,
                locationTextView,
                boardGameImageView,
                seeDescriptionButton,
                levelTextView,
                timeTextView)
        initRecyclerViews()
    }

    private fun initRecyclerViews() {
        acceptedPlayersRecyclerView.layoutManager = LinearLayoutManager(context)
        pendingPlayersRecyclerView.layoutManager = LinearLayoutManager(context)

        acceptedPlayersRecyclerView.adapter = acceptedPlayersAdapter
        pendingPlayersRecyclerView.adapter = pendingPlayersAdapter
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        adminViewModel.bind(this, event.eventId)

        fetchEventPlayersTriggerSubject.onNext(init)
    }

    override fun onStop() {
        init = false
        adminViewModel.unbind()
        super.onStop()
    }

    private fun initEmitters() {
        fetchEventPlayersTriggerSubject = PublishSubject.create()
        acceptPlayerSubject = PublishSubject.create()
        kickPlayerSubject = PublishSubject.create()
    }

    override fun render(adminViewState: AdminViewState) {
        with(adminViewState) {
            showNoAcceptedPlayersText(false)
            showNoPendingPlayersText(false)

            showAcceptedProgressBar(acceptedProgress)
            showPendingProgressBar(pendingProgress)

            if (acceptedPlayersList.isNotEmpty() && !acceptedProgress) {
                acceptedPlayersAdapter.submitList(acceptedPlayersList)
            } else if (!acceptedProgress) {
                showNoAcceptedPlayersText(true)
            }

            if (pendingPlayersList.isNotEmpty() && !pendingProgress) {
                pendingPlayersAdapter.submitList(pendingPlayersList)
            } else if (!pendingProgress) {
                showNoPendingPlayersText(true)
            }
        }
    }

    private fun showAcceptedProgressBar(show: Boolean) {
        if (show) {
            acceptedPlayersRecyclerView.visibility = View.GONE
            acceptedProgressBar.visibility = View.VISIBLE
        } else {
            acceptedPlayersRecyclerView.visibility = View.VISIBLE
            acceptedProgressBar.visibility = View.GONE
        }
    }

    private fun showNoAcceptedPlayersText(show: Boolean) {
        if (show) {
            acceptedPlayersRecyclerView.visibility = View.GONE
            noAcceptedPlayersTextView.visibility = View.VISIBLE
        } else {
            acceptedPlayersRecyclerView.visibility = View.VISIBLE
            noAcceptedPlayersTextView.visibility = View.GONE
        }
    }

    private fun showPendingProgressBar(show: Boolean) {
        if (show) {
            pendingPlayersRecyclerView.visibility = View.GONE
            pendingProgressBar.visibility = View.VISIBLE
        } else {
            pendingPlayersRecyclerView.visibility = View.VISIBLE
            pendingProgressBar.visibility = View.GONE
        }
    }

    private fun showNoPendingPlayersText(show: Boolean) {
        if (show) {
            pendingPlayersRecyclerView.visibility = View.GONE
            noPendingPlayersTextView.visibility = View.VISIBLE
        } else {
            pendingPlayersRecyclerView.visibility = View.VISIBLE
            noPendingPlayersTextView.visibility = View.GONE
        }
    }

    override fun kickPlayerEmitter(): Observable<String> = kickPlayerSubject

    override fun acceptPlayerEmitter(): Observable<String> = acceptPlayerSubject

    override fun fetchEventPlayersTriggerEmitter(): Observable<Boolean> = fetchEventPlayersTriggerSubject
}