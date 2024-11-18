package com.boardly.eventdetails.admin

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.base.eventdetails.BaseEventDetailsFragment
import com.boardly.common.events.EventUIRenderer
import com.boardly.common.events.models.Event
import com.boardly.constants.EDIT_EVENT_REQUEST_CODE
import com.boardly.constants.EVENT_EDITED_RESULT_CODE
import com.boardly.constants.EVENT_ID
import com.boardly.constants.EVENT_REMOVED_RESULT_CODE
import com.boardly.databinding.FragmentAdminBinding
import com.boardly.event.EventActivity
import com.boardly.eventdetails.admin.list.AcceptedPlayersAdapter
import com.boardly.eventdetails.admin.list.PendingPlayersAdapter
import com.boardly.factories.AdminViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class AdminFragment : BaseEventDetailsFragment(), AdminView {

    @Inject
    lateinit var eventUIRenderer: EventUIRenderer

    @Inject
    lateinit var adminViewModelFactory: AdminViewModelFactory

    private lateinit var adminViewModel: AdminViewModel

    private lateinit var fetchEventDetailsTriggerSubject: PublishSubject<Boolean>
    private var init = true

    private var event = Event()

    lateinit var acceptPlayerSubject: PublishSubject<String>
    lateinit var kickPlayerSubject: PublishSubject<String>

    private val acceptedPlayersAdapter = AcceptedPlayersAdapter(this)
    private val pendingPlayersAdapter = PendingPlayersAdapter(this)

    private lateinit var binding: FragmentAdminBinding

    companion object {
        fun newInstance(eventId: String): AdminFragment {
            val adminFragment = AdminFragment()
            val arguments = Bundle()
            arguments.putString(EVENT_ID, eventId)
            adminFragment.arguments = arguments
            return adminFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString(EVENT_ID, "") ?: ""

        adminViewModel =
            ViewModelProviders.of(this, adminViewModelFactory)[AdminViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editEventButton.setOnClickListener { EventActivity.startEditMode(this, event) }
        initRecyclerViews()
    }

    private fun initEventView(event: Event) {
        this.event = event
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

    private fun initRecyclerViews() {
        binding.acceptedPlayersRecyclerView.isNestedScrollingEnabled = false
        binding.pendingPlayersRecyclerView.isNestedScrollingEnabled = false

        binding.acceptedPlayersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.pendingPlayersRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.acceptedPlayersRecyclerView.adapter = acceptedPlayersAdapter
        binding.pendingPlayersRecyclerView.adapter = pendingPlayersAdapter
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        adminViewModel.bind(this, eventId)

        fetchEventDetailsTriggerSubject.onNext(init)
    }

    override fun onStop() {
        init = false
        adminViewModel.unbind()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            EDIT_EVENT_REQUEST_CODE -> handleEditEventResult(resultCode)
        }
    }

    private fun handleEditEventResult(resultCode: Int) {
        when (resultCode) {
            EVENT_EDITED_RESULT_CODE -> init = true
            EVENT_REMOVED_RESULT_CODE -> activity?.finish()
        }
    }

    override fun initEmitters() {
        super.initEmitters()
        fetchEventDetailsTriggerSubject = PublishSubject.create()
        acceptPlayerSubject = PublishSubject.create()
        kickPlayerSubject = PublishSubject.create()
    }

    override fun render(adminViewState: AdminViewState) {
        with(adminViewState) {
            showNoAcceptedPlayersText(false)
            showNoPendingPlayersText(false)

            showAcceptedProgressBar(acceptedProgress)
            showPendingProgressBar(pendingProgress)
            showEventProgressBar(eventProgress)

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

            initEventView(event)
        }
    }

    private fun showAcceptedProgressBar(show: Boolean) {
        if (show) {
            binding.acceptedPlayersRecyclerView.visibility = View.GONE
            binding.acceptedProgressBar.visibility = View.VISIBLE
        } else {
            binding.acceptedPlayersRecyclerView.visibility = View.VISIBLE
            binding.acceptedProgressBar.visibility = View.GONE
        }
    }

    private fun showNoAcceptedPlayersText(show: Boolean) {
        if (show) {
            binding.acceptedPlayersRecyclerView.visibility = View.GONE
            binding.noAcceptedPlayersTextView.visibility = View.VISIBLE
        } else {
            binding.acceptedPlayersRecyclerView.visibility = View.VISIBLE
            binding.noAcceptedPlayersTextView.visibility = View.GONE
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

    private fun showPendingProgressBar(show: Boolean) {
        if (show) {
            binding.pendingPlayersRecyclerView.visibility = View.GONE
            binding.pendingProgressBar.visibility = View.VISIBLE
        } else {
            binding.pendingPlayersRecyclerView.visibility = View.VISIBLE
            binding.pendingProgressBar.visibility = View.GONE
        }
    }

    private fun showNoPendingPlayersText(show: Boolean) {
        if (show) {
            binding.pendingPlayersRecyclerView.visibility = View.GONE
            binding.noPendingPlayersTextView.visibility = View.VISIBLE
        } else {
            binding.pendingPlayersRecyclerView.visibility = View.VISIBLE
            binding.noPendingPlayersTextView.visibility = View.GONE
        }
    }

    override fun kickPlayerEmitter(): Observable<String> = kickPlayerSubject

    override fun acceptPlayerEmitter(): Observable<String> = acceptPlayerSubject

    override fun fetchEventDetailsTriggerEmitter(): Observable<Boolean> =
        fetchEventDetailsTriggerSubject
}