package com.egoriku.ladyhappy.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.egoriku.ladyhappy.auth.model.UserLoginState
import com.egoriku.ladyhappy.extensions.common.Constants.EMPTY
import com.egoriku.ladyhappy.network.ResultOf
import com.egoriku.ladyhappy.network.firestore.awaitResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Authentication {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // TODO: 1/28/21 Migrate to StateFlow
    private val _userLoginState = MutableLiveData<UserLoginState>()

    val userLoginState: LiveData<UserLoginState> = _userLoginState

    init {
        invalidateUser()
    }

    private fun invalidateUser() {
        when (val user = auth.currentUser) {
            null -> _userLoginState.value = UserLoginState.Anon
            else -> _userLoginState.value = UserLoginState.LoggedIn(
                    userId = user.uid,
                    name = user.displayName ?: EMPTY,
                    email = user.email ?: EMPTY,
                    photoUrl = user.photoUrl?.toString() ?: EMPTY,
                    isEmailVerified = user.isEmailVerified
            )
        }
    }

    fun logOut() {
        auth.signOut()

        _userLoginState.value = UserLoginState.Anon
    }

    suspend fun authWithEmailAndPassword(
            email: String,
            password: String
    ): ResultOf<AuthResult> = withContext(Dispatchers.IO) {
        val resultOf: ResultOf<AuthResult> = auth.signInWithEmailAndPassword(email, password).awaitResult()

        withContext(Dispatchers.Main) {
            invalidateUser()
        }

        resultOf
    }

    suspend fun authWithToken(tokenId: String): ResultOf<AuthResult> = withContext(Dispatchers.IO) {
        val credential = GoogleAuthProvider.getCredential(tokenId, null)

        val resultOf: ResultOf<AuthResult> = auth.signInWithCredential(credential).awaitResult()

        withContext(Dispatchers.Main) {
            invalidateUser()
        }

        resultOf
    }
}