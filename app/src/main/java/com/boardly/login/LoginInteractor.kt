package com.boardly.login

import com.boardly.base.BaseInteractor
import com.boardly.constants.NAME_CHILD
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject


class LoginInteractor : BaseInteractor() {

    fun login(email: String, password: String): Observable<PartialLoginViewState> {
        val stateSubject: Subject<PartialLoginViewState> = PublishSubject.create()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .continueWithTask { checkIfProfileIsFilled(it.result.user.uid) }
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        stateSubject.onNext(PartialLoginViewState.LoginSuccess(task.result))
                    } else {
                        emitErrorState(task.exception, stateSubject)
                    }
                })
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun isLoggedIn(): Observable<PartialLoginViewState> {
        val resultSubject = PublishSubject.create<PartialLoginViewState>()
        if (firebaseAuth.currentUser != null) {
            checkIfProfileIsFilled(currentUserId).addOnSuccessListener {
                resultSubject.onNext(PartialLoginViewState.LoginSuccess(it))
            }
        } else {
            return Observable.just(PartialLoginViewState.NotLoggedIn())
        }
        return resultSubject
    }

    fun login(googleAccount: GoogleSignInAccount): Observable<PartialLoginViewState> {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        return signInWithCredential(credential)
    }

    fun login(accessToken: AccessToken): Observable<PartialLoginViewState> {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        return signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: AuthCredential): Observable<PartialLoginViewState> {
        val stateSubject: Subject<PartialLoginViewState> = PublishSubject.create()
        firebaseAuth.signInWithCredential(credential)
                .continueWithTask { checkIfProfileIsFilled(it.result.user.uid) }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        stateSubject.onNext(PartialLoginViewState.LoginSuccess(task.result))
                    } else {
                        emitErrorState(task.exception, stateSubject)
                    }
                }
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }

    private fun checkIfProfileIsFilled(userId: String): Task<Boolean> {
        val dbSource = TaskCompletionSource<Boolean>()
        val dbTask = dbSource.task
        getUserNodeRef(userId)
                .child(NAME_CHILD)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dbSource.setResult(dataSnapshot.getValue(String::class.java) != null)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // unused
                    }
                })
        return dbTask
    }

    private fun emitErrorState(exception: Exception?, stateSubject: Subject<PartialLoginViewState>) {
        val errorState = PartialLoginViewState.ErrorState(exception)
        stateSubject.onNext(errorState)
        stateSubject.onNext(errorState.copy(dismissToast = true))
    }
}