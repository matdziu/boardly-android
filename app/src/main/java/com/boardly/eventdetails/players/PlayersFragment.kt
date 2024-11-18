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
import com.boardly.databinding.FragmentPlayersBinding
import com.boardly.eventdetails.players.list.AcceptedPlayersAdapter
import com.boardly.factories.PlayersViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
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

    private lateinit var binding: FragmentPlayersBinding

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

        playersViewModel =
            ViewModelProviders.of(this, playersViewModelFactory)[PlayersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        binding.leaveEventButton.setOnClickListener { launchLeaveEventDialog() }
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
        binding.acceptedPlayersRecyclerView.isNestedScrollingEnabled = false
        binding.acceptedPlayersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.acceptedPlayersRecyclerView.adapter = acceptedPlayersAdapter
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
                Toast.makeText(
                    context,
                    getString(R.string.you_were_kicked_text),
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
            if (left) {
                Toast.makeText(context, getString(R.string.you_left_event), Toast.LENGTH_SHORT)
                    .show()
                activity?.finish()
            }
            initEventView(event)
        }
    }

    private fun initEventView(event: Event) {
        eventUIRenderer.displayEventInfo(
            event,
            binding.eventLayout.eventNameTextView,
            binding.eventLayout.gameTextView,
            binding.eventLayout.locationTextView,
            binding.eventLayout.locationImageView,
            binding.eventLayout.boardGameImageView,
            binding.eventLayout.seeDescriptionButton,
            binding.eventLayout.timeTextView,
            binding.eventLayout.timeImageView,
            binding.eventLayout.gameTextView2,
            binding.eventLayout.boardGameImageView2,
            binding.eventLayout.gameTextView3,
            binding.eventLayout.boardGameImageView3
        )
    }

    override fun fetchEventDetailsTriggerEmitter(): Observable<Boolean> =
        fetchEventDetailsTriggerSubject

    override fun leaveEventEmitter(): Observable<Boolean> = leaveEventSubject

    private fun showPlayersProgressBar(show: Boolean) {
        if (show) {
            binding.acceptedPlayersRecyclerView.visibility = View.GONE
            binding.playersProgressBar.visibility = View.VISIBLE
        } else {
            binding.acceptedPlayersRecyclerView.visibility = View.VISIBLE
            binding.playersProgressBar.visibility = View.GONE
        }
    }

    private fun showEventProgressBar(show: Boolean) {
        if (show) {
            binding.eventLayout.eventLayoutInternal.visibility = View.GONE
            binding.eventProgressBar.visibility = View.VISIBLE
        } else {
            binding.eventLayout.eventLayoutInternal.visibility = View.VISIBLE
            binding.eventProgressBar.visibility = View.GONE
        }
    }

    private fun showNoPlayersText(show: Boolean) {
        if (show) {
            binding.acceptedPlayersRecyclerView.visibility = View.GONE
            binding.noPlayersTextView.visibility = View.VISIBLE
        } else {
            binding.acceptedPlayersRecyclerView.visibility = View.VISIBLE
            binding.noPlayersTextView.visibility = View.GONE
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