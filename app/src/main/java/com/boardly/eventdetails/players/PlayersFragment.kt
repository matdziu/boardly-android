package com.boardly.eventdetails.players

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.boardly.R
import com.boardly.base.eventdetails.BaseEventDetailsFragment
import com.boardly.common.events.EventUIRenderer
import com.boardly.common.events.models.Event
import com.boardly.constants.EVENT_ID
import com.boardly.eventdetails.players.list.AcceptedPlayersAdapter
import com.boardly.factories.PlayersViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_players.acceptedPlayersRecyclerView
import kotlinx.android.synthetic.main.fragment_players.eventLayout
import kotlinx.android.synthetic.main.fragment_players.eventProgressBar
import kotlinx.android.synthetic.main.fragment_players.leaveEventButton
import kotlinx.android.synthetic.main.fragment_players.noPlayersTextView
import kotlinx.android.synthetic.main.fragment_players.playersProgressBar
import kotlinx.android.synthetic.main.layout_event.boardGameImageView
import kotlinx.android.synthetic.main.layout_event.boardGameImageView2
import kotlinx.android.synthetic.main.layout_event.boardGameImageView3
import kotlinx.android.synthetic.main.layout_event.eventNameTextView
import kotlinx.android.synthetic.main.layout_event.gameTextView
import kotlinx.android.synthetic.main.layout_event.gameTextView2
import kotlinx.android.synthetic.main.layout_event.gameTextView3
import kotlinx.android.synthetic.main.layout_event.locationImageView
import kotlinx.android.synthetic.main.layout_event.locationTextView
import kotlinx.android.synthetic.main.layout_event.seeDescriptionButton
import kotlinx.android.synthetic.main.layout_event.timeImageView
import kotlinx.android.synthetic.main.layout_event.timeTextView
import javax.inject.Inject

class PlayersFragment : BaseEventDetailsFragment(), PlayersView {

    @Inject
    lateinit var playersViewModelFactory: PlayersViewModelFactory

    @Inject
    lateinit var eventUIRenderer: EventUIRenderer

    private lateinit var playersViewModel: PlayersViewModel

    private lateinit var fetchEventDetailsTriggerSubject: PublishSubject<Boolean>
    private lateinit var leaveEventSubject: PublishSubject<Boolean>
    private var init = true

    private val acceptedPlayersAdapter = AcceptedPlayersAdapter(this)

    companion object {
        fun newInstance(eventId: String): PlayersFragment {
            val playersFragment = PlayersFragment()
            val arguments = Bundle()
            arguments.putString(EVENT_ID, eventId)
            playersFragment.arguments = arguments
            return playersFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString(EVENT_ID, "") ?: ""

        playersViewModel = ViewModelProviders.of(this, playersViewModelFactory)[PlayersViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_players, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        leaveEventButton.setOnClickListener { launchLeaveEventDialog() }
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        playersViewModel.bind(this, eventId)
        fetchEventDetailsTriggerSubject.onNext(init)
    }

    override fun onStop() {
        init = false
        playersViewModel.unbind()
        super.onStop()
    }

    override fun initEmitters() {
        super.initEmitters()
        fetchEventDetailsTriggerSubject = PublishSubject.create()
        leaveEventSubject = PublishSubject.create()
    }

    private fun initRecyclerView() {
        acceptedPlayersRecyclerView.isNestedScrollingEnabled = false
        acceptedPlayersRecyclerView.layoutManager = LinearLayoutManager(context)
        acceptedPlayersRecyclerView.adapter = acceptedPlayersAdapter
    }

    override fun render(playersViewState: PlayersViewState) {
        with(playersViewState) {
            showNoPlayersText(false)
            showPlayersProgressBar(playersProgress)
            showEventProgressBar(eventProgress)
            if (acceptedPlayersList.isNotEmpty() && !playersProgress) {
                acceptedPlayersAdapter.submitList(acceptedPlayersList)
            } else if (!playersProgress) {
                showNoPlayersText(true)
            }
            if (kicked) {
                Toast.makeText(context, getString(R.string.you_were_kicked_text), Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
            if (left) {
                Toast.makeText(context, getString(R.string.you_left_event), Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
            initEventView(event)
        }
    }

    private fun initEventView(event: Event) {
        eventUIRenderer.displayEventInfo(event,
                eventNameTextView,
                gameTextView,
                locationTextView,
                locationImageView,
                boardGameImageView,
                seeDescriptionButton,
                timeTextView,
                timeImageView,
                gameTextView2,
                boardGameImageView2,
                gameTextView3,
                boardGameImageView3)
    }

    override fun fetchEventDetailsTriggerEmitter(): Observable<Boolean> = fetchEventDetailsTriggerSubject

    override fun leaveEventEmitter(): Observable<Boolean> = leaveEventSubject

    private fun showPlayersProgressBar(show: Boolean) {
        if (show) {
            acceptedPlayersRecyclerView.visibility = View.GONE
            playersProgressBar.visibility = View.VISIBLE
        } else {
            acceptedPlayersRecyclerView.visibility = View.VISIBLE
            playersProgressBar.visibility = View.GONE
        }
    }

    private fun showEventProgressBar(show: Boolean) {
        if (show) {
            eventLayout.visibility = View.GONE
            eventProgressBar.visibility = View.VISIBLE
        } else {
            eventLayout.visibility = View.VISIBLE
            eventProgressBar.visibility = View.GONE
        }
    }

    private fun showNoPlayersText(show: Boolean) {
        if (show) {
            acceptedPlayersRecyclerView.visibility = View.GONE
            noPlayersTextView.visibility = View.VISIBLE
        } else {
            acceptedPlayersRecyclerView.visibility = View.VISIBLE
            noPlayersTextView.visibility = View.GONE
        }
    }

    private fun launchLeaveEventDialog() {
        AlertDialog.Builder(context)
                .setTitle(getString(R.string.leave_event_title))
                .setMessage(getString(R.string.are_you_sure_to_leave))
                .setPositiveButton(R.string.leave_event, { _, _ -> leaveEventSubject.onNext(true) })
                .setNegativeButton(R.string.leave_cancel, null)
                .create()
                .show()
    }
}