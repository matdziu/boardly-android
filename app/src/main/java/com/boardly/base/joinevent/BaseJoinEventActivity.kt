package com.boardly.base.joinevent

import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.home.models.JoinEventData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BaseJoinEventActivity : BaseDrawerActivity(), BaseJoinEventView {

    lateinit var joinEventSubject: PublishSubject<JoinEventData>

    override fun joinEventEmitter(): Observable<JoinEventData> = joinEventSubject

    override fun onStart() {
        super.onStart()
        joinEventSubject = PublishSubject.create()
    }

    protected fun showJoinRequestSentToast(joinRequestSent: Boolean) {
        if (joinRequestSent) {
            Toast.makeText(this, R.string.join_request_sent, Toast.LENGTH_SHORT).show()
        }
    }
}