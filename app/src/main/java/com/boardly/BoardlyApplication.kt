package com.boardly

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import com.boardly.injection.DaggerAppComponent
import com.boardly.notifications.NotificationChannelsBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class BoardlyApplication : Application(), HasActivityInjector {

    var visibleActivity: Activity? = null
    var online: Boolean = false

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        initNotificationChannels()
        initInjection()
        initFirebaseConnectionListener()
        initVisibleActivityListener()
    }

    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelsBuilder(this).buildChannels()
        }
    }

    private fun initInjection() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
    }

    private fun initFirebaseConnectionListener() {
        FirebaseDatabase.getInstance().getReference(".info/connected")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.getValue(Boolean::class.java)?.let { online = it }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        online = false
                    }
                })
    }

    private fun initVisibleActivityListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityStarted(activity: Activity?) {
                visibleActivity = activity
            }

            override fun onActivityStopped(activity: Activity?) {
                if (activity == visibleActivity) visibleActivity = null
            }

            override fun onActivityPaused(activity: Activity?) {
                // unused
            }

            override fun onActivityResumed(activity: Activity?) {
                // unused
            }

            override fun onActivityDestroyed(activity: Activity?) {
                // unused
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                // unused
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                // unused
            }
        })
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityDispatchingAndroidInjector
}