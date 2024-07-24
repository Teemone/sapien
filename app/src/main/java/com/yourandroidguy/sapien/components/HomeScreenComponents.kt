package com.yourandroidguy.sapien.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import com.yourandroidguy.sapien.R
import com.yourandroidguy.sapien.model.ChatMessage
import com.yourandroidguy.sapien.state.PromptTextState
import com.yourandroidguy.sapien.ui.theme.BlackAlpha25
import com.yourandroidguy.sapien.ui.theme.MidGrey
import com.yourandroidguy.sapien.ui.theme.SemiDarkGrey
import com.yourandroidguy.sapien.ui.theme.WhiteAlpha70


@Composable
fun ChatContent(
    chatMessages: SnapshotStateList<ChatMessage>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 56.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(all = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        state = state
    ) {
        items(chatMessages){ message ->
            when (message.sender) {
                Sender.USER -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ){
                        message.message?.let {
                            RequestBubble(
                                text = it
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ){
                        message.message?.let {
                            ResponseBubble(
                                text = it
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeContent(
    modifier: Modifier=Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center

        ) {
            Image(
                modifier = Modifier.size(80.dp),
                painter = painterResource(id = R.drawable.larai_plain),
                contentDescription = null)

        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item{
                QuestionCard(
                    textBody = stringResource(id = R.string.sample_a),
                    onQuestionCardClicked = {}
                )
            }
            item {
                QuestionCard(
                    textBody = stringResource(id = R.string.sample_b),
                    onQuestionCardClicked = {}
                )
            }
        }
    }
}





@Composable
fun AppTopBar(
    user: FirebaseUser?,
    modifier: Modifier=Modifier,
    onDrawerClicked: () -> Unit = {},
    onProfilePicClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onDrawerClicked){
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "menu",
                tint = Color.White)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            LanguageSelector()
            Spacer(modifier = Modifier.width(16.dp))

            if (user?.photoUrl != null){
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .clickable { onProfilePicClicked() },
                    contentScale = ContentScale.Crop,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null)
            }

        }

    }
}

@Composable
fun PromptTextFieldRow(
    modifier: Modifier = Modifier,
    imageUrl: Uri?,
    textState: PromptTextState,
    enableSndBtn: Boolean,
    onImportImageClicked: () -> Unit = {},
    onSendClicked: (String, () -> Unit) -> Unit,
    onCancelSelectedImageClicked: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(color = SemiDarkGrey)
            .padding(start = 4.dp, end = 4.dp, top = 12.dp, bottom = 16.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUrl?.let {
            Box(
                modifier = Modifier.padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it)
                        .crossfade(true)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .build(),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(WhiteAlpha70)
                        .clickable { onCancelSelectedImageClicked() }
                ){
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
        ){

            IconButton(onClick = onImportImageClicked) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint= MaterialTheme.colorScheme.onBackground)
            }

            PromptTextField(
                modifier = Modifier.weight(1f),
                textState = textState){
                isFocused = it.isFocused
            }

            AnimatedContent(targetState = enableSndBtn, label = "enable send button") {
                if(it){
                    IconButton(
                        enabled = enableSndBtn,
                        onClick = { onSendClicked(textState.text){textState.clearText()} }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = null,
                            tint= MaterialTheme.colorScheme.onBackground)
                    }
                }else{
                    CircularProgressIndicator(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color.White,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth / 2
                    )
                }
            }


        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptTextField(
    modifier: Modifier = Modifier,
    textState: PromptTextState,
    onFocusChanged: (FocusState) -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    BasicTextField(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .fillMaxWidth()
            .heightIn(min = 32.dp)
            .onFocusChanged { onFocusChanged(it) }
            .focusRequester(textState.getFocusRequester)
        ,
        value = textState.text,
        textStyle = TextStyle.Default.copy(
            fontSize = TextUnit(18f, TextUnitType.Sp)
        ),
        maxLines = 6,
        onValueChange = {textState.text = it},
        decorationBox = {innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = textState.text ,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = false,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(12.dp),
                placeholder = {
                    Text(text = stringResource(id = R.string.im_looking_for), color = Color.Black)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
            )
        }
    )
}

@Composable
fun QuestionCard(
    modifier: Modifier = Modifier,
    textBody: String = "loremIpsum",
    onQuestionCardClicked: () -> Unit = {}
) {
    val interactionSource = remember{
        MutableInteractionSource()
    }
    val customRipple = rememberRipple(
        color = BlackAlpha25
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = customRipple
            ) { onQuestionCardClicked() }
    ) {
        Column(
            modifier = modifier
                .background(MidGrey, RoundedCornerShape(6.dp))
                .padding(8.dp)
                .widthIn(max = 250.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = textBody, color = WhiteAlpha70)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun LanguageSelector(modifier: Modifier = Modifier) {
    var toggleEnglish by rememberSaveable {
        mutableStateOf(true)
    }
    var toggleHausa by rememberSaveable {
        mutableStateOf(false)
    }
    val color by animateColorAsState(targetValue = Color.White, label = "colorAsState")

    Row(
        modifier = modifier
            .background(Color.Black, RoundedCornerShape(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,

        ){
        Box(
            Modifier
                .clickable {
                    toggleEnglish = true
                    toggleHausa = false
                }
                .background(
                    if (toggleEnglish) color else Color.Transparent,
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.english),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                color = if(toggleEnglish) Color.Black else Color.White
            )
        }

        Box(
            Modifier
                .clickable {
                    toggleHausa = true
                    toggleEnglish = false
                }
                .background(
                    if (toggleHausa) color else Color.Transparent,
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.hausa),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                color = if(toggleHausa) Color.Black else Color.White
            )
        }
    }
}