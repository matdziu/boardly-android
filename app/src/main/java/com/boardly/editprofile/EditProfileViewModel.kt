package com.boardly.editprofile

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class EditProfileViewModel(private val editProfileInteractor: EditProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(EditProfileViewState())

    fun bind(editProfileView: EditProfileView) {
        val inputDataObservable = editProfileView.emitInputData()
                .flatMap {
                    if (it.name.isNotEmpty()) {
                        editProfileInteractor
                                .saveProfileChanges(it)
                                .startWith(PartialEditProfileViewState.ProgressState())
                    } else {
                        Observable.just(PartialEditProfileViewState.NameFieldEmptyState())
                    }
                }
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(inputDataObservable.subscribe { editProfileView.render(it) })
    }

    private fun reduce(previousState: EditProfileViewState, partialState: PartialEditProfileViewState)
            : EditProfileViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}