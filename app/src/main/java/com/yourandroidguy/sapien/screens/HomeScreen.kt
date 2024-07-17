package com.yourandroidguy.sapien.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import com.yourandroidguy.sapien.R
import com.yourandroidguy.sapien.components.AppTopBar
import com.yourandroidguy.sapien.components.ChatContent
import com.yourandroidguy.sapien.components.PromptTextFieldRow
import com.yourandroidguy.sapien.components.Sender
import com.yourandroidguy.sapien.components.WelcomeContent
import com.yourandroidguy.sapien.model.Chat
import com.yourandroidguy.sapien.model.ChatMessage
import com.yourandroidguy.sapien.state.PromptTextState
import com.yourandroidguy.sapien.ui.theme.BlackAlpha85
import com.yourandroidguy.sapien.viewmodel.SapienViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    chatMessageList: SnapshotStateList<ChatMessage>,
    textState: PromptTextState,
    modifier: Modifier = Modifier,
    viewmodel: SapienViewModel,
    onDrawerClicked: () -> Unit= {},
    onProfileClicked: () -> Unit={},
    sendRequestToAiService: (String) -> Unit = {}
) {
    val layoutDirection = LocalLayoutDirection.current
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var imageUrl by remember {
        mutableStateOf<Uri?>(null)
    }
    val loadingState by viewmodel.loadingState.collectAsState()
    val user by viewmodel.user.collectAsState()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            imageUrl = uri
        }


    Scaffold(
        modifier = modifier
            .imePadding()
        ,
        topBar = {
            AppTopBar(
                user = user,
                modifier = Modifier
                    .statusBarsPadding(),
                onDrawerClicked = onDrawerClicked,
                onProfilePicClicked = onProfileClicked)
        },
        bottomBar = {
            PromptTextFieldRow(
                textState = textState,
                imageUrl = imageUrl,

                onCancelSelectedImageClicked = {imageUrl = null},

                onImportImageClicked = {launcher.launch("image/*")},
                onSendClicked = {
                        text, clearText ->
                    val validText = text.isNotBlank() && text.isNotEmpty()
                    when{

                        validText -> {
                            val chatListNextIndex = viewmodel.chatsList.value.lastIndex + 1

                            // populate chatList with new chat
                            // iff chatMessageList is empty
                            if (chatMessageList.isEmpty()){
                                val chat = Chat(
                                    id = chatListNextIndex,
                                    title = text)
                                viewmodel.addChatToChatsList(chat)
                                viewmodel.updateCurrentChat(chat)
                                viewmodel.addChatToDb(chat)
                            }

                            // populate chatMessageList (holds all messages for a particular chat)
                            // when a new message is sent
                            chatMessageList.add(
                                ChatMessage(
                                    id = chatMessageList.lastIndex + 1,
                                    message = text,
                                    sender = Sender.USER
                                )
                            )

                            viewmodel.insertMessage(
                                chatMessageList.last(),
                                viewmodel.currentChat.value?.id!!)

                            // forward a request to the chat-bot service
                            sendRequestToAiService(text)
                        }
                    }

                    when{
                        lazyListState.canScrollForward && chatMessageList.isNotEmpty() && validText -> {
                            coroutineScope.launch {
                                lazyListState.scrollToItem(chatMessageList.lastIndex)
                            }
                        }
                    }

                    clearText()
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = chatMessageList.isNotEmpty() && lazyListState.canScrollForward,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                SmallFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(chatMessageList.lastIndex)
                        }

                    }) {
                    Icon(imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null)
                }
            }

        }
    ) {paddingValues ->
        Box(
            modifier =
            Modifier.padding(
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection),
                bottom = paddingValues.calculateBottomPadding()
            )
        ){
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(BlackAlpha85, BlendMode.SrcAtop),
                painter = painterResource(id = R.drawable.pattern),
                contentDescription = null)

            AnimatedContent(targetState = loadingState, label = "loading state") { ls ->
                when{
                    chatMessageList.isEmpty() && ls == SapienViewModel.LoadingState.CANCELLED -> {
                        WelcomeContent()
                    }
                    chatMessageList.isEmpty() && ls == SapienViewModel.LoadingState.LOADING -> {
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center){
                            CircularProgressIndicator(
                                color = Color.White
                            )
                        }

                    }
                    else -> {
                        ChatContent(
                            chatMessages = chatMessageList,
                            state = lazyListState
                        )
                    }
                }
            }

        }

    }
}