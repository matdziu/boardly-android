package com.boardly.editprofile

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EditProfileViewRobot(editProfileViewModel: EditProfileViewModel) : BaseViewRobot<EditProfileViewState>() {

    private val inputDataSubject = PublishSubject.create<InputData>()

    private val editProfileView = object : EditProfileView {
        override fun render(editProfileViewState: EditProfileViewState) {
            renderedStates.add(editProfileViewState)
        }

        override fun emitInputData(): Observable<InputData> = inputDataSubject
    }

    init {
        editProfileViewModel.bind(editProfileView)
    }

    fun emitInputData(inputData: InputData) {
        inputDataSubject.onNext(inputData)
    }
}