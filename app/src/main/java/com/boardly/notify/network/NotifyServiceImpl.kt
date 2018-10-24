package com.boardly.notify.network

import com.boardly.base.BaseServiceImpl
import com.boardly.constants.NOTIFY_SETTINGS_NODE
import com.boardly.notify.models.NotifySettings
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NotifyServiceImpl : NotifyService, BaseServiceImpl() {

    override fun updateNotifySettings(notifySettings: NotifySettings): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        val userLatitude = notifySettings.userLatitude
        val userLongitude = notifySettings.userLongitude
        if (userLatitude != null && userLongitude != null) {
            val geoLocation = GeoLocation(userLatitude, userLongitude)
            setGeoLocationTask(geoLocation)
                    .continueWithTask { getUserNotifySettingsRef(currentUserId).updateChildren(notifySettings.toMap()) }
                    .addOnCompleteListener { resultSubject.onNext(true) }
        }
        return resultSubject
    }

    override fun deleteNotifications(): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        getUserNotifySettingsRef(currentUserId).setValue(null)
                .addOnCompleteListener { resultSubject.onNext(true) }
        return resultSubject
    }

    override fun fetchNotifySettings(): Observable<NotifySettings> {
        val resultSubject = PublishSubject.create<NotifySettings>()
        getUserNotifySettingsRef(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val notifySettings = dataSnapshot.getValue(NotifySettings::class.java)
                if (notifySettings != null) resultSubject.onNext(notifySettings)
                else resultSubject.onNext(NotifySettings())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })
        return resultSubject
    }

    private fun setGeoLocationTask(geoLocation: GeoLocation): Task<String> {
        val geoSource = TaskCompletionSource<String>()
        val geoTask = geoSource.task
        getGeoFire(NOTIFY_SETTINGS_NODE).setLocation(currentUserId, geoLocation) { key, _ ->
            geoSource.setResult(key)
        }
        return geoTask
    }
}