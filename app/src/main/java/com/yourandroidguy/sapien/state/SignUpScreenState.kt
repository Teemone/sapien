package com.yourandroidguy.sapien.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class SignUpScreenState(
    private val _email: String,
    private val _password: String,
    private val _confPassword: String,
): AuthState(_email, _password){
    override var confPassword: MutableState<String> = mutableStateOf(_confPassword)
}

@Composable
fun rememberSignUpState(
    email: String = "",
    password: String = "",
    confPassword: String = ""
): SignUpScreenState =
    remember(email, password, confPassword) {
        SignUpScreenState(email, password, confPassword)
    }
