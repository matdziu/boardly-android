package com.boardly.login.network

import com.boardly.base.BaseServiceImpl
import com.boardly.constants.NAME_CHILD
import com.boardly.login.models.LoginData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class LoginServiceImpl : LoginService, BaseServiceImpl() {

    override fun login(email: String, password: String): Observable<Boolean> {
        val resultSubject: Subject<Boolean> = PublishSubject.create()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .continueWithTask { checkIfProfileIsFilled(it.result.user.uid) }
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        resultSubject.onNext(task.result)
                    } else {
                        task.exception?.let { resultSubject.onError(it) }
                    }
                })
        return resultSubject.observeOn(AndroidSchedulers.mainThread())
    }

    override fun signInWithCredential(credential: AuthCredential): Observable<Boolean> {
        val resultSubject: Subject<Boolean> = PublishSubject.create()
        firebaseAuth.signInWithCredential(credential)
                .continueWithTask { checkIfProfileIsFilled(it.result.user.uid) }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resultSubject.onNext(task.result)
                    } else {
                        task.exception?.let { resultSubject.onError(it) }
                    }
                }
        return resultSubject.observeOn(AndroidSchedulers.mainThread())
    }

    override fun isLoggedIn(): Observable<LoginData> {
        val resultSubject = PublishSubject.create<LoginData>()
        if (firebaseAuth.currentUser != null) {
            checkIfProfileIsFilled(currentUserId).addOnSuccessListener {
                resultSubject.onNext(LoginData(true, it))
            }
        } else {
            return Observable.just(LoginData(false))
        }
        return resultSubject
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
}