package com.yourandroidguy.sapien.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester

data class PromptTextState(
    private val promptTextFieldText: String,
    private val focusRequester: FocusRequester,
){
    var text by mutableStateOf(promptTextFieldText)
    val getFocusRequester
        get() = focusRequester

    fun clearText() {text = ""}
}



@Composable
fun rememberPromptTextState(
    promptText: String = "",
    focusRequester: FocusRequester = FocusRequester()
): PromptTextState =
    remember(promptText, focusRequester) {
        PromptTextState(promptText, focusRequester)
    }
