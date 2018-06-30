package com.boardly.editprofile

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EditProfileViewModel(private val editProfileInteractor: EditProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(EditProfileViewState())

    fun bind(editProfileView: EditProfileView) {

    }

    private fun reduce(previousState: EditProfileViewState, partialState: PartialEditProfileViewState)
            : EditProfileViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}