package com.yourandroidguy.sapien

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.yourandroidguy.sapien.components.AppDrawer
import com.yourandroidguy.sapien.screens.SignInScreen
import com.yourandroidguy.sapien.screens.SignupScreen
import com.yourandroidguy.sapien.state.SignInScreenState
import com.yourandroidguy.sapien.state.SignUpScreenState
import com.yourandroidguy.sapien.state.rememberSignInState
import com.yourandroidguy.sapien.state.rememberSignUpState
import com.yourandroidguy.sapien.viewmodel.SapienViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun App(
    auth: FirebaseAuth,
    modifier: Modifier = Modifier,
    viewModel: SapienViewModel = viewModel()
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val credentialManager = CredentialManager.create(context)
    val startDestination = if(auth.currentUser == null) Routes.SIGN_IN else Routes.HOME
    val signupState = rememberSignUpState()
    val signInState = rememberSignInState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val toast: (String) -> Unit = {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_LONG
            ).show()
        }

        NavHost(navController = navController, startDestination = startDestination) {

            composable(route = Routes.HOME){
                AppDrawer(user = auth.currentUser){ hideDialog ->

                    scope.launch {
                        delay(500)
                        navController.popBackStack()
                        navController.navigate(Routes.SIGN_IN)
                        delay(1000)
                        auth.signOut()
                        credentialManager.clearCredentialState(
                            ClearCredentialStateRequest()
                        )
                    }

                    hideDialog()
                }
            }

            composable(route = Routes.SIGN_IN){

                var isLoading by rememberSaveable {
                    mutableStateOf(false)
                }
                val textFieldIsNotEmpty by checkSignInTextFieldsNotEmpty(signInState = signInState).collectAsState()
                var enableBtn by rememberSaveable(textFieldIsNotEmpty) {
                    mutableStateOf(textFieldIsNotEmpty && !isLoading)
                }

                SignInScreen(
                    signInState = signInState,
                    isLoading = isLoading,
                    enabled = enableBtn,
                    continueWithEmail = {
                        if (isValidEmail(signInState.email)){
                            isLoading = true
                            enableBtn = false
                            auth.signInWithEmailAndPassword(
                                signInState.email,
                                signInState.password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "signInWithEmail:success")
                                        val user = auth.currentUser
                                        if(user?.isEmailVerified == true){
                                            scope.launch {
                                                viewModel.updateUser(user)
                                                delay(500)
                                                navController.popBackStack()
                                                navController.navigate("home")
                                                delay(500)
                                                isLoading = false
                                                enableBtn = true

                                            }
                                        }else{
                                            Toast.makeText(
                                                context,
                                                "Account not yet verified!",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            auth.signOut()
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInWithEmail:failure", task.exception)

                                    }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    enableBtn = true
                                    when(e){
                                        is FirebaseNetworkException -> {
                                            toast("Please check your internet connection and try again")
                                        }
                                        is FirebaseTooManyRequestsException -> {
                                            toast(
                                                "Too many tries. Please wait before trying again."
                                            )
                                        }
                                        is FirebaseAuthInvalidCredentialsException -> {
                                            toast("Incorrect username or password")
                                        }
                                        is FirebaseAuthUserCollisionException -> {
                                            toast(e.message.toString())
                                        }
                                        else -> toast(e.message.toString())

                                    }

                                }
                        }else{
                            toast("Invalid email address")
                        }

                    },
                    continueWithGoogle = {

                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.web_client_id))
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        scope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )
                                val credential = result.credential
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val googleIdToken = googleIdTokenCredential.idToken
                                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener { task ->
                                        if(task.isSuccessful){
                                            viewModel.updateUser(auth.currentUser)
                                            navController.popBackStack()
                                            navController.navigate(Routes.HOME)
                                        }
                                    }
                                    .addOnFailureListener{e ->
                                        toast(e.message.toString())
                                    }
                            }catch (e: Exception){
                                toast(e.message.toString())
                                e.printStackTrace()
                            }
                        }
                    },
                    navigateToSignUp = {
                        navController.navigate(Routes.SIGN_UP)
                    }
                )
            }

            composable(Routes.SIGN_UP){
                var hasUppercase by rememberSaveable {
                    mutableStateOf(false)
                }
                var hasLowercase by rememberSaveable {
                    mutableStateOf(false)
                }
                var hasDigit by rememberSaveable {
                    mutableStateOf(false)
                }
                var atLeast8Chars by rememberSaveable {
                    mutableStateOf(false)
                }
                var isLoading by rememberSaveable {
                    mutableStateOf(false)
                }
                val complexityReqIsMet by rememberSaveable(hasUppercase, hasLowercase, hasDigit, atLeast8Chars) {
                    mutableStateOf(hasUppercase && hasLowercase && hasDigit && atLeast8Chars)
                }
                val textFieldIsNotEmpty by checkSignUpTextFieldsNotEmpty(signUpState = signupState).collectAsState()
                var enableBtn by rememberSaveable(textFieldIsNotEmpty, complexityReqIsMet, !isLoading) {
                    mutableStateOf(textFieldIsNotEmpty && complexityReqIsMet && !isLoading)
                }

                SignupScreen(
                    signUpState = signupState,
                    isEnabled = enableBtn,
                    isLoading = isLoading,
                    hasUppercase = hasUppercase,
                    hasLowercase = hasLowercase,
                    hasDigit = hasDigit,
                    atLeast8Chars = atLeast8Chars,
                    checkPasswordComplexity = {password ->
                        hasUppercase = password.any{it.isUpperCase()}
                        hasLowercase = password.any{it.isLowerCase()}
                        hasDigit = password.any{it.isDigit()}
                        atLeast8Chars = password.length >= 8
                    },
                    createAccountWithEmailPw = {
                        if (isValidEmail(signupState.email)){

                            if (signupState.password == signupState.confPassword.value){

                                isLoading = true
                                enableBtn = false

                                auth.createUserWithEmailAndPassword(
                                    signupState.email, signupState.password
                                ).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {sendEmail ->
                                            if (sendEmail.isSuccessful){
                                                scope.launch{
                                                    delay(500)
                                                    navController.popBackStack()
                                                    navController.navigate(Routes.SIGN_IN)
                                                    delay(500)
                                                    isLoading = false
                                                    enableBtn = true
                                                }

                                            }else{
                                                toast("Encountered a problem sending verification email. Please try again later")
                                                auth.signOut()
                                            }
                                        }

                                    }
                                }.addOnFailureListener { e ->
                                    isLoading = false
                                    enableBtn = true
                                    toast(e.message.toString())
                                    e.printStackTrace()
                                }
                            }else{
                                toast("Passwords do not match")
                            }

                        }else
                            toast("Invalid email address")

                    },
                    continueWithGoogle = {

                    }
                )
            }
        }

    }

}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    return email.matches(emailRegex)
}

fun checkSignInTextFieldsNotEmpty(signInState: SignInScreenState): StateFlow<Boolean> {
    val tmp = MutableStateFlow(false)

    tmp.update {
        signInState.let {
            when {
                it.email.isEmpty() || it.password.isEmpty() -> false
                else -> return@let true
            }
        }
    }
    return tmp.asStateFlow()
}

fun checkSignUpTextFieldsNotEmpty(signUpState: SignUpScreenState): StateFlow<Boolean> {
    val tmp = MutableStateFlow(false)

    tmp.update {
        signUpState.let {
            when {
                it.email.isEmpty() || it.password.isEmpty() || it.confPassword.value.isEmpty() -> false
                else ->return@let true
            }

        }
    }
    return tmp.asStateFlow()
}

object Routes{
    const val HOME = "home"
    const val SIGN_UP = "sign_up"
    const val SIGN_IN = "sign_in"
}