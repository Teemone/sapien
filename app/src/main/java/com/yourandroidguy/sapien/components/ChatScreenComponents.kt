package com.yourandroidguy.sapien.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourandroidguy.sapien.components.Sender.BOT
import com.yourandroidguy.sapien.components.Sender.USER

@Composable
fun RequestBubble(
    modifier: Modifier = Modifier,
    text: String = ""
) {
    Surface(
        modifier = modifier.widthIn(min = 100.dp, max = 300.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            text = text, color = MaterialTheme.colorScheme.onBackground)
    }

}

@Composable
fun ResponseBubble(
    modifier: Modifier = Modifier,
    text: String = ""
) {
    Column {
        Surface(
            modifier = modifier.widthIn(min = 100.dp, max = 300.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                text = text, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

/**
 * Represents the sender of a [ChatMessage]
 * @property USER represents the actual user
 * @property BOT represents the chat-bot API service
 */
enum class Sender{
    USER, BOT
}