package com.yourandroidguy.sapien.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yourandroidguy.sapien.R
import com.yourandroidguy.sapien.components.ConfirmPasswordTextField
import com.yourandroidguy.sapien.components.ContinueWithEmailButton
import com.yourandroidguy.sapien.components.ContinueWithGoogleButton
import com.yourandroidguy.sapien.components.EmailTextField
import com.yourandroidguy.sapien.components.OrDivider
import com.yourandroidguy.sapien.components.PasswordComplexityCheckUI
import com.yourandroidguy.sapien.components.PasswordTextField
import com.yourandroidguy.sapien.state.SignUpScreenState
import com.yourandroidguy.sapien.ui.theme.DarkGrey

@Composable
fun SignupScreen(
    isLoading: Boolean,
    isEnabled: Boolean,
    hasDigit: Boolean,
    hasUppercase: Boolean,
    hasLowercase: Boolean,
    atLeast8Chars: Boolean,
    signUpState: SignUpScreenState,
    continueWithGoogle: () -> Unit = {},
    createAccountWithEmailPw: () -> Unit = {},
    checkPasswordComplexity: (String) -> Unit = {}
) {
    var isPwVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isConfPwVisible by rememberSaveable {
        mutableStateOf(false)
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrey)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(90.dp))
        Text(
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            text = stringResource(id = R.string.sign_up)
        )
        Spacer(modifier = Modifier.height(50.dp))

        EmailTextField(
            state = signUpState,
            onValueChanged = {signUpState.email = it}
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordTextField(
            state = signUpState,
            isVisible = isPwVisible,
            onVisibilityClicked = {isPwVisible = !isPwVisible},
            onValueChanged = { password ->
                signUpState.password = password

                checkPasswordComplexity(password)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        PasswordComplexityCheckUI(
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasDigit = hasDigit,
            atLeast8Chars = atLeast8Chars
        )
        Spacer(modifier = Modifier.height(10.dp))
        ConfirmPasswordTextField(
            placeholderText = stringResource(id = R.string.confirm_password),
            isError = false,
            state = signUpState,
            isVisible = isConfPwVisible,
            onVisibilityClicked = {isConfPwVisible = !isConfPwVisible},
            onValueChanged = {signUpState.confPassword.value = it}
        )
        Spacer(modifier = Modifier.height(10.dp))
        ContinueWithEmailButton(modifier = Modifier.fillMaxWidth(), enabled = isEnabled, isLoading = isLoading){
            createAccountWithEmailPw()
        }
        Spacer(modifier = Modifier.height(20.dp))
        OrDivider()
        Spacer(modifier = Modifier.height(20.dp))
        ContinueWithGoogleButton(modifier = Modifier.fillMaxWidth()){continueWithGoogle()}
        Spacer(modifier = Modifier.height(10.dp))
    }
}
