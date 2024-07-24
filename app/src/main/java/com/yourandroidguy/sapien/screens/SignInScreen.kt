package com.yourandroidguy.sapien.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yourandroidguy.sapien.R
import com.yourandroidguy.sapien.components.ContinueWithEmailButton
import com.yourandroidguy.sapien.components.ContinueWithGoogleButton
import com.yourandroidguy.sapien.components.EmailTextField
import com.yourandroidguy.sapien.components.OrDivider
import com.yourandroidguy.sapien.components.PasswordTextField
import com.yourandroidguy.sapien.state.SignInScreenState
import com.yourandroidguy.sapien.ui.theme.DarkGrey

@Composable
fun SignInScreen(
    emailBtnEnabled: Boolean,
    emailBtnIsLoading: Boolean,
    googleBtnEnabled: Boolean,
    googleBtnIsLoading: Boolean,
    signInState: SignInScreenState,
    navigateToSignUp: () -> Unit = {},
    continueWithEmail: () -> Unit = {},
    continueWithGoogle: () -> Unit = {}
) {
    var isPwVisible by rememberSaveable {
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
            text = stringResource(id = R.string.sign_in))
        Spacer(modifier = Modifier.height(50.dp))

        EmailTextField(
            state = signInState,
            onValueChanged = {signInState.email = it}
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordTextField(
            state = signInState,
            isVisible = isPwVisible,
            onVisibilityClicked = {isPwVisible = !isPwVisible},
            onValueChanged = {signInState.password = it}

        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier =
            Modifier
                .align(Alignment.End)
                .clickable { navigateToSignUp() },
            color = MaterialTheme.colorScheme.onBackground,
            text = buildAnnotatedString {
                append(stringResource(id = R.string.dont_have_an_acct))
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)){
                    append(stringResource(id = R.string.sign_up ))
                }

            })
        Spacer(modifier = Modifier.height(10.dp))
        ContinueWithEmailButton(
            modifier = Modifier.fillMaxWidth(),
            isLoading = emailBtnIsLoading,
            enabled = emailBtnEnabled){continueWithEmail()}
        Spacer(modifier = Modifier.height(20.dp))
        OrDivider()
        Spacer(modifier = Modifier.height(20.dp))
        ContinueWithGoogleButton(
            enabled = googleBtnEnabled,
            isLoading = googleBtnIsLoading,
            modifier = Modifier.fillMaxWidth()){continueWithGoogle()}
        Spacer(modifier = Modifier.height(50.dp))
    }
}