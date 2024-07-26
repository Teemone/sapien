package com.yourandroidguy.sapien.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yourandroidguy.sapien.R
import com.yourandroidguy.sapien.components.Sender.BOT
import com.yourandroidguy.sapien.components.Sender.USER
import com.yourandroidguy.sapien.model.ChatMessage

@Composable
fun RequestBubble(
    modifier: Modifier = Modifier,
    image: Uri? = null,
    text: String = ""
) {
    Surface(
        modifier = modifier.widthIn(min = 100.dp, max = 300.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background
    ) {
        if (image != null) {
            Column {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                    text = text, color = MaterialTheme.colorScheme.onBackground)
            }
        }
        else{
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                text = text, color = MaterialTheme.colorScheme.onBackground)
        }

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