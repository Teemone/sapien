package com.yourandroidguy.sapien.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class SignInScreenState(
    private var _email: String,
    private var _password: String
): AuthState(_email, _password){
    override var confPassword: MutableState<String> = mutableStateOf("")
}

@Composable
fun rememberSignInState(
    email: String = "",
    password: String = ""
): SignInScreenState =
    remember(email, password) {
        SignInScreenState(email, password)
    }