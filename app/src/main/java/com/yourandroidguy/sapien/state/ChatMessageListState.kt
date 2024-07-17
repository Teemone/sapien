package com.yourandroidguy.sapien.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.yourandroidguy.sapien.model.ChatMessage
import com.yourandroidguy.sapien.model.Chat

/**
 * Retains a list of [ChatMessage]s collected for a particular [Chat]
 * this list is used to populate a LazyList to display current or previous [ChatMessage]s
 *
 * @param chatMessageList populated with previous or current [ChatMessage]s
 *
 * @see Chat
 * @see ChatMessage
 */

data class ChatMessageListState(
    private val chatMessageList: SnapshotStateList<ChatMessage>
){
    val value
        get() = chatMessageList

    fun clear() = chatMessageList.clear()
}


@Composable
fun rememberChatMessageListState(
    chatMessageList: SnapshotStateList<ChatMessage> = mutableStateListOf()
): ChatMessageListState {
    return remember(chatMessageList) {
        ChatMessageListState(chatMessageList)
    }
}

