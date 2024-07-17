package com.yourandroidguy.sapien.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

abstract class AuthState(authEmail: String, authPw: String){
    var email by mutableStateOf(authEmail)
    var password by mutableStateOf(authPw)
    var error: AuthError by mutableStateOf(AuthError())
        private set

    abstract var confPassword: MutableState<String>

    data class AuthError(
        val message: String = "",
        val isError: Boolean = false
    )
}