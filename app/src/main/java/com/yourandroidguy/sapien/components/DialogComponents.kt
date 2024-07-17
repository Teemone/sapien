package com.yourandroidguy.sapien.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import com.yourandroidguy.sapien.R

@Composable
fun AlertDialogExample(
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    if (showDialog){
        AlertDialog(
            modifier = Modifier.fillMaxWidth(0.9f),
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

}

@Composable
fun DialogWithImage(
    user: FirebaseUser?,
    onDismissRequest: () -> Unit,
    onSignOutClicked: () -> Unit = {},
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center
            ) {
                Row (Modifier.padding(horizontal = 16.dp )){
                    if (user != null){
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.photoUrl)
                                .crossfade(true)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .build(),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(Modifier.padding(4.dp)) {
                            Text(
                                text = user.displayName!!,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.email!!,
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)

                SignOutOption{onSignOutClicked()}

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)

                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Box(modifier = Modifier
                        .size(4.dp)
                        .background(MaterialTheme.colorScheme.onSurface, CircleShape))
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SignOutOption(
    modifier: Modifier=Modifier,
    onClick: () -> Unit = {},
) {
    OptionListItem(
        modifier = modifier,
        icon = Icons.AutoMirrored.Outlined.Logout,
        text = "Sign out",
        onClick = onClick
    )
}

@Composable
fun OptionListItem(
    modifier: Modifier=Modifier,
    onClick: () -> Unit = {},
    text: String = "",
    icon: ImageVector
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis)
        }
    }

}