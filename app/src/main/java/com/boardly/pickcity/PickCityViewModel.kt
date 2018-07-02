package com.boardly.pickcity

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class PickCityViewModel(private val pickCityInteractor: PickCityInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(PickCityViewState())

    fun bind(pickCityView: PickCityView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}