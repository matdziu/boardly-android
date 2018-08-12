package com.boardly.eventdetails.players

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.boardly.R
import com.boardly.common.events.models.Event
import com.boardly.constants.EVENT
import com.boardly.constants.LEVEL_STRINGS_MAP
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.formatForMaxOf
import com.boardly.factories.PlayersViewModelFactory
import com.boardly.injection.modules.GlideApp
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_players.contentViewGroup
import kotlinx.android.synthetic.main.fragment_players.progressBar
import kotlinx.android.synthetic.main.layout_event.boardGameImageView
import kotlinx.android.synthetic.main.layout_event.eventNameTextView
import kotlinx.android.synthetic.main.layout_event.gameTextView
import kotlinx.android.synthetic.main.layout_event.levelTextView
import kotlinx.android.synthetic.main.layout_event.locationTextView
import kotlinx.android.synthetic.main.layout_event.numberOfPlayersTextView
import kotlinx.android.synthetic.main.layout_event.seeDescriptionButton
import kotlinx.android.synthetic.main.layout_event.timeTextView
import java.util.*
import javax.inject.Inject

class PlayersFragment : Fragment(), PlayersView {

    @Inject
    lateinit var playersViewModelFactory: PlayersViewModelFactory

    private lateinit var playersViewModel: PlayersViewModel

    private lateinit var fetchEventPlayersTriggerSubject: PublishSubject<String>
    private var init = true
    private var event = Event()

    companion object {
        fun newInstance(event: Event): PlayersFragment {
            val playersFragment = PlayersFragment()
            val arguments = Bundle()
            arguments.putParcelable(EVENT, event)
            playersFragment.arguments = arguments
            return playersFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        event = arguments?.getParcelable(EVENT) ?: Event()

        playersViewModel = ViewModelProviders.of(this, playersViewModelFactory)[PlayersViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_players, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayEventInfo(event)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        playersViewModel.bind(this)
        if (init) {
            fetchEventPlayersTriggerSubject.onNext(event.eventId)
            init = false
        }
    }

    override fun onStop() {
        playersViewModel.unbind()
        super.onStop()
    }

    private fun initEmitters() {
        fetchEventPlayersTriggerSubject = PublishSubject.create()
    }

    override fun render(playersViewState: PlayersViewState) {
        with(playersViewState) {
            showProgressBar(progress)
        }
    }

    override fun fetchEventPlayersTriggerEmitter(): Observable<String> = fetchEventPlayersTriggerSubject

    private fun displayEventInfo(event: Event) {
        with(event) {
            eventNameTextView.text = eventName
            gameTextView.text = gameName
            locationTextView.text = placeName
            numberOfPlayersTextView.text = currentNumberOfPlayers.toString().formatForMaxOf(maxPlayers.toString())
            loadImageFromUrl(boardGameImageView, gameImageUrl, R.drawable.board_game_placeholder)

            setSeeDescriptionButton(description)
            setLevelTextView(levelId)
            setDateTextView(timestamp)
        }
    }

    private fun loadImageFromUrl(imageView: ImageView, pictureUrl: String, @DrawableRes placeholderId: Int) {
        GlideApp.with(this)
                .load(pictureUrl)
                .placeholder(placeholderId)
                .into(imageView)
    }

    private fun setSeeDescriptionButton(description: String) {
        if (description.isNotEmpty()) {
            seeDescriptionButton.visibility = View.VISIBLE
            seeDescriptionButton.setOnClickListener { launchDescriptionDialog(description) }
        } else {
            seeDescriptionButton.visibility = View.GONE
        }
    }

    private fun launchDescriptionDialog(description: String) {
        android.app.AlertDialog.Builder(activity)
                .setMessage(description)
                .setTitle(R.string.description_text)
                .setPositiveButton(R.string.close_dialog) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
    }

    private fun setLevelTextView(levelId: String) {
        levelTextView.text = context?.getString(LEVEL_STRINGS_MAP[levelId]
                ?: com.boardly.R.string.empty)
    }

    private fun setDateTextView(timestamp: Long) {
        if (timestamp > 0) {
            timeTextView.text = Date(timestamp).formatForDisplay()
        } else {
            timeTextView.text = context?.getString(R.string.date_to_be_added)
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            contentViewGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentViewGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}