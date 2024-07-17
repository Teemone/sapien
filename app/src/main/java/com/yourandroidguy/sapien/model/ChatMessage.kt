package com.yourandroidguy.sapien.model

import android.os.Parcelable
import com.yourandroidguy.sapien.components.Sender
import kotlinx.parcelize.Parcelize

/**
 * Represents a single message sent by either [Sender.USER] or [Sender.BOT]
 *
 * @see [Chat]
 */

@Parcelize
data class ChatMessage(
    val id: Int? = null,
    val message: String? = null,
    val sender: Sender? = null,
    val timestamp: Long? = null
): Parcelable
