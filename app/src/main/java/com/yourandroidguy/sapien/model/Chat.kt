package com.yourandroidguy.sapien.model

/**
 * A chat is a single room where [Sender.USER] & [Sender.BOT] can exchange messages
 *
 * @see [ChatMessage]
 */

data class Chat(
    val id: Int? = null,
    val title: String? = null,
    val timestamp: Long? = null
)

