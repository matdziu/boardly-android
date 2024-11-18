package com.boardly.base.eventdetails

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Switch
import com.boardly.R
import com.boardly.base.eventdetails.models.RateInput
import com.boardly.constants.TOGGLE_CHAT_NOTIFICATIONS_KEY_PREFIX
import com.boardly.extensions.readAppSetting
import com.boardly.extensions.saveAppSetting
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class BaseEventDetailsFragment : Fragment(), EventDetailsView {

    protected var eventId = ""

    private lateinit var ratingSubject: PublishSubject<RateInput>
    private lateinit var fetchEventTriggerSubject: PublishSubject<String>

    open fun initEmitters() {
        ratingSubject = PublishSubject.create()
        fetchEventTriggerSubject = PublishSubject.create()
    }

    override fun ratingEmitter(): Observable<RateInput> = ratingSubject

    override fun emitRating(rateInput: RateInput) {
        ratingSubject.onNext(rateInput)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toggleChatNotificationsSwitch =
            view.findViewById<Switch>(R.id.toggleChatNotificationsSwitch)
        toggleChatNotificationsSwitch.isChecked =
            PreferenceManager.getDefaultSharedPreferences(context)
                .readAppSetting("$TOGGLE_CHAT_NOTIFICATIONS_KEY_PREFIX$eventId")
        toggleChatNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleChatNotifications(
                eventId,
                isChecked
            )
        }
    }

    private fun toggleChatNotifications(eventId: String, enable: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .saveAppSetting("$TOGGLE_CHAT_NOTIFICATIONS_KEY_PREFIX$eventId", enable)
    }
}