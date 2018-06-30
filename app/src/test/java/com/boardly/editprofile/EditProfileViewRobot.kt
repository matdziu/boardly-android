package com.boardly.editprofile

import com.boardly.base.BaseViewRobot
import com.boardly.editprofile.models.InputData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EditProfileViewRobot(editProfileViewModel: EditProfileViewModel) : BaseViewRobot<EditProfileViewState>() {

    private val inputDataSubject = PublishSubject.create<InputData>()
    private val fetchProfileDataSubject = PublishSubject.create<Boolean>()

    private val editProfileView = object : EditProfileView {
        override fun render(editProfileViewState: EditProfileViewState) {
            renderedStates.add(editProfileViewState)
        }

        override fun emitInputData(): Observable<InputData> = inputDataSubject

        override fun emitFetchProfileDataTrigger(): Observable<Boolean> = fetchProfileDataSubject
    }

    init {
        editProfileViewModel.bind(editProfileView)
    }

    fun emitInputData(inputData: InputData) {
        inputDataSubject.onNext(inputData)
    }

    fun emitFetchTrigger() {
        fetchProfileDataSubject.onNext(true)
    }
}