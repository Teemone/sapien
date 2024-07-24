package com.yourandroidguy.sapien.components

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import com.yourandroidguy.sapien.R
import com.yourandroidguy.sapien.model.Chat
import com.yourandroidguy.sapien.screens.HomeScreen
import com.yourandroidguy.sapien.state.rememberChatMessageListState
import com.yourandroidguy.sapien.state.rememberPromptTextState
import com.yourandroidguy.sapien.viewmodel.SapienViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AppDrawer(
    user: FirebaseUser?,
    modifier: Modifier=Modifier,
    viewModel: SapienViewModel = viewModel(),
    onSignOutClicked: (() -> Unit) -> Unit = {}

) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed){
        if (it == DrawerValue.Open)
            keyboardController?.hide()
        true
    }
    val scope = rememberCoroutineScope()
    val chatMessageList = rememberChatMessageListState(
        viewModel.chatMessageList.collectAsStateWithLifecycle().value.toMutableStateList()
    )
    val textState = rememberPromptTextState()
    val chatList by viewModel.chatsList.collectAsStateWithLifecycle()
    val currentChat by viewModel.currentChat.collectAsStateWithLifecycle()
    var userInfoDialog by rememberSaveable{ mutableStateOf(false) }
    var showClearAllItemsConfirmationDialog by rememberSaveable{ mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.updateUser(user)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = modifier.fillMaxWidth(0.8f),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                drawerShape = RectangleShape,
                drawerTonalElevation = 0.dp
            ) {
                DrawerContent(
                    chatList = chatList,
                    user = user,
                    onNewChatClicked = {
                        chatMessageList.clear()
                        viewModel.updateCurrentChat(null)
                        scope.launch {
                            drawerState.close()
                            textState.getFocusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    },
                    clearAll = {showClearAllItemsConfirmationDialog = true},
                    onProfileClicked = {
                        scope.launch {
                            drawerState.close()
                            delay(100)
                            userInfoDialog = true
                        }
                    },
                    onRecentSearchItemClicked = {chatItem ->

                        try {
                            if (chatItem.id == currentChat?.id){
                                scope.launch {
                                    drawerState.close()
                                }
                                return@DrawerContent
                            }else{
                                viewModel.updateLoadingState(SapienViewModel.LoadingState.LOADING)
                                chatMessageList.clear()

                                scope.launch {
                                    drawerState.close()
                                    viewModel.updateCurrentChat(chatItem)
                                    viewModel.currentChat.value?.toString()
                                        ?.let { Log.i("Current Chat", it) }

                                    viewModel.fetchMessagesByChatId(chatItem.id!!)
                                }
                            }
                        }catch (e: NoSuchElementException){
                            viewModel.updateLoadingState(SapienViewModel.LoadingState.LOADING)
                            chatMessageList.clear()

                            scope.launch {
                                drawerState.close()
                                delay(100)
                                textState.getFocusRequester.requestFocus()

                                Log.i("Recent Search scope", "You're here")
                                viewModel.updateCurrentChat(chatItem)
                                viewModel.currentChat.value?.toString()
                                    ?.let { Log.i("Current Chat", it) }

                                viewModel.fetchMessagesByChatId(chatItem.id!!)
                            }
                        }catch (e: Exception){e.printStackTrace()}


                    }
                )
            }
        },
    ) {
        when{
            userInfoDialog -> {
                DialogWithImage(
                    user = user,
                    onDismissRequest = { userInfoDialog = false },
                    onSignOutClicked = {
                        onSignOutClicked{
                            userInfoDialog = false
                        }
                    }
                )
            }
        }

        AlertDialogExample(
            showDialog = showClearAllItemsConfirmationDialog,
            dialogTitle = stringResource(R.string.clear_all_chats),
            dialogText = stringResource(R.string.delete_chat_entries),
            onDismissRequest = { showClearAllItemsConfirmationDialog = false},
            onConfirmation = {
                showClearAllItemsConfirmationDialog = false
                scope.launch {
                    delay(500)
                    viewModel.clearAllChats()
                }
            })

        HomeScreen(
            chatMessageList = chatMessageList.value ,
            textState = textState,
            viewmodel = viewModel,
            onDrawerClicked = {
                scope.launch {
                    drawerState.apply {
                        if (isClosed){
                            keyboardController?.hide()
                            open()
                        } else close()
                    }
                }
            },
            onProfileClicked = {userInfoDialog = true},
            sendRequestToAiService = {message ->
                viewModel.sendRequestToAi(message)
            }
        )
    }
}


@Composable
fun DrawerContent(
    chatList: List<Chat>,
    user: FirebaseUser?,
    modifier: Modifier = Modifier,
    onNewChatClicked: () -> Unit = {},
    clearAll: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onRecentSearchItemClicked: (Chat) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                modifier = Modifier.padding(vertical = 24.dp),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = TextUnit(30f, TextUnitType.Sp)),
                text = stringResource(id = R.string.app_name))
            HorizontalDivider()
            Button(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                onClick = onNewChatClicked) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = null)
                Text(text = stringResource(R.string.new_chat))
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.recent_chats).uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = LocalTextStyle.current.copy(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1 )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = clearAll) {
                    Text(
                        text = stringResource(R.string.clear_all),
                        color = Color.White
                    )
                }
            }

            AnimatedContent(targetState = chatList.isEmpty(), label = "show content") { isEmpty->
                if (isEmpty){

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center)
                    {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }

                }
                else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        items(chatList){
                            RecentSearchItem(chat = it){ chatItem ->
                                onRecentSearchItemClicked(chatItem)
                            }
                        }
                    }
                }
            }

        }

        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ){
            if (user?.displayName != null && user.photoUrl != null){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProfileClicked() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp),
                        contentScale = ContentScale.Crop,
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.photoUrl!!)
                            .crossfade(true)
                            .build(),
                        contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = user.displayName!!,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun RecentSearchItem(
    chat: Chat,
    modifier: Modifier=Modifier,
    onClick: (Chat) -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable { onClick(chat) },
        color = Color.Transparent
    ) {
        chat.title?.let {
            Text(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 4.dp),
                text = it,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }

}
