package com.yourandroidguy.sapien.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.yourandroidguy.sapien.R
import com.yourandroidguy.sapien.state.AuthState
import com.yourandroidguy.sapien.ui.theme.WhiteAlpha25


@Composable
fun PasswordComplexityCheckUI(
    hasUppercase: Boolean,
    hasLowercase: Boolean,
    hasDigit: Boolean,
    atLeast8Chars: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle.Default.copy(fontStyle = FontStyle.Italic),
        text = stringResource(R.string.your_password_must_contain)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PasswordComplexityItem(completed = hasUppercase, text = stringResource(R.string.uppercase))
            PasswordComplexityItem(completed = hasLowercase, text = stringResource(R.string.lowercase))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PasswordComplexityItem(completed = hasDigit, text = stringResource(R.string.number))
            PasswordComplexityItem(completed = atLeast8Chars, text = stringResource(R.string.eight_or_more))
        }
    }
}

@Composable
fun PasswordComplexityItem(
    modifier: Modifier = Modifier,
    completed: Boolean,
    text: String,
) {
    Row(modifier) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = if (completed) Icons.Filled.CheckCircle else Icons.Filled.Cancel, contentDescription = null,
            tint = if (completed) Color.Green else MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = TextStyle.Default.copy(fontStyle = FontStyle.Italic))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(
    state: AuthState,
    modifier: Modifier = Modifier,
    onValueChanged: (String) -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    BasicTextField(
        value = state.email,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        keyboardActions = KeyboardActions(),
        onValueChange = {onValueChanged(it)},
        cursorBrush = SolidColor(Color.White),
        maxLines = 2,
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        decorationBox = {innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = state.email ,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(12.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Email,
                        contentDescription = "password",
                        tint = Color.White
                    )
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.your_email), color = Color.White)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                ),
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    state: AuthState,
    isVisible: Boolean,
    placeholderText: String = stringResource(id = R.string.password),
    onVisibilityClicked: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    BasicTextField(
        value = state.password,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = {onValueChanged(it)},
        cursorBrush = SolidColor(Color.White),
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        maxLines = 1,
        decorationBox = {innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = state.password ,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                interactionSource = interactionSource,
                contentPadding = PaddingValues(12.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Password,
                        contentDescription = "password",
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onVisibilityClicked){
                        Icon(imageVector =
                        if(isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                placeholder = {
                    Text(text = placeholderText, color = Color.White)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                ),
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPasswordTextField(
    modifier: Modifier = Modifier,
    state: AuthState,
    isError: Boolean,
    isVisible: Boolean,
    placeholderText: String = stringResource(id = R.string.password),
    onVisibilityClicked: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    BasicTextField(
        value = state.confPassword.value,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = {onValueChanged(it)},
        cursorBrush = SolidColor(Color.White),
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        maxLines = 1,
        decorationBox = {innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = state.confPassword.value ,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                isError = isError,
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                interactionSource = interactionSource,
                contentPadding = PaddingValues(12.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Password,
                        contentDescription = "password",
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onVisibilityClicked){
                        Icon(imageVector =
                        if(isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                placeholder = {
                    Text(text = placeholderText, color = Color.White)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                ),
            )
        }
    )
}

@Composable
fun OrDivider() {
    Row(
        modifier=Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.White,
            text = "Or")
        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ContinueWithGoogleButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit = {},
) {
    ContinueWith_Button(
        modifier = modifier,
        text = stringResource(R.string.continue_with_google),
        hasIcon = true,
        enabled = enabled,
        isLoading = isLoading,
        icon = {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.google_icon_48),
                contentDescription = "google icon")
        },
        onClick = onClick

    )
}

@Composable
fun ContinueWithEmailButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    isLoading: Boolean = false,
    onClick: () -> Unit = {},
) {
    ContinueWith_Button(
        modifier = modifier,
        enabled = enabled,
        text = stringResource(id = R.string._continue),
        onClick = onClick,
        isLoading = isLoading
    )
}

@Composable
private fun ContinueWith_Button(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    text: String,
    icon: @Composable ()->Unit = {},
    onClick: () -> Unit = {},
    hasIcon: Boolean = false
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        colors =
        if(hasIcon){
            ButtonColors(
                containerColor = WhiteAlpha25,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.DarkGray
            )
        }else{
            ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.DarkGray
            )
        }
    ) {
        if (hasIcon){
            icon()
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(text = text)
        Spacer(modifier = Modifier.width(8.dp))

        AnimatedVisibility(visible = isLoading){

            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        }

    }

}