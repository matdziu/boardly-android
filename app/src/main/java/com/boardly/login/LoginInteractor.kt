package com.boardly.login

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class LoginInteractor {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String): Observable<PartialLoginViewState> {
        val stateSubject: Subject<PartialLoginViewState> = PublishSubject.create()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        stateSubject.onNext(PartialLoginViewState.LoginSuccess())
                    } else {
                        emitErrorState(task.exception, stateSubject)
                    }
                })
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun isLoggedIn(): Observable<PartialLoginViewState.LoginSuccess> {
        return if (firebaseAuth.currentUser != null) {
            Observable.just(PartialLoginViewState.LoginSuccess())
        } else {
            Completable.complete().toObservable()
        }
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
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        stateSubject.onNext(PartialLoginViewState.LoginSuccess())
                    } else {
                        emitErrorState(task.exception, stateSubject)
                    }
                }
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }

    private fun emitErrorState(exception: Exception?, stateSubject: Subject<PartialLoginViewState>) {
        val errorState = PartialLoginViewState.ErrorState(exception)
        stateSubject.onNext(errorState)
        stateSubject.onNext(errorState.copy(dismissToast = true))
    }
}