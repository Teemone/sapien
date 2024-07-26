package com.yourandroidguy.sapien.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import com.yourandroidguy.sapien.components.Sender
import com.yourandroidguy.sapien.model.Chat
import com.yourandroidguy.sapien.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SapienViewModel: ViewModel() {

    private val database = Firebase.database.reference

    private val generativeModel = com.google.firebase.Firebase.vertexAI.generativeModel("gemini-1.5-flash")


    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user = _user.asStateFlow()

    private val _chatMessageList = MutableStateFlow(listOf<ChatMessage>())
    val chatMessageList
        get() =  _chatMessageList.asStateFlow()

    private val _chats = MutableStateFlow(listOf<Chat>())
    val chatsList: StateFlow<List<Chat>>
        get() = _chats

    private var _loadingState = MutableStateFlow(LoadingState.CANCELLED)
    val loadingState = _loadingState.asStateFlow()

    private var _enableSendButton = MutableStateFlow(true)
    val enableSendButton = _enableSendButton.asStateFlow()

    private var _apiRespError = MutableStateFlow(false)
    val apiRespError = _apiRespError.asStateFlow()

    /**
     * Backing property to store a reference to the current [Chat]
     */
    private val _currentChat = MutableStateFlow<Chat?>(null)
    val currentChat = _currentChat.asStateFlow()

    init {
        fetchChatList()
    }

    enum class LoadingState{
        LOADING, COMPLETED, CANCELLED
    }


    fun sendRequestToAi(context: Context, chatMessage: ChatMessage){
        val contentResolver = context.contentResolver
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = try{
                contentResolver.openInputStream(Uri.parse(chatMessage.imageUrl)).use {
                    BitmapFactory.decodeStream(it)
                }
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
            val message = chatMessage.message!!
            try {
                _enableSendButton.update { false }

                val resp =
                    if(bitmap == null){
                    generativeModel.generateContent(message) }
                    else{
                        generativeModel.generateContent(
                            content {
                                image(bitmap)
                                text(message)
                            }
                        )
                    }


                delay(3000)

                if (resp.text != null){
                    val cm = ChatMessage(
                        id = chatMessage.id?.plus(1),
                        message = resp.text,
                        sender = Sender.BOT
                    )
                    insertMessage(cm, currentChat.value?.id!!)

                    _enableSendButton.update { true }
                }else{
                    _enableSendButton.update { true }
                }
            }catch (e: Exception){
                _enableSendButton.update { true }
                _apiRespError.update { true }
                e.printStackTrace()}


        }
    }

    /**
     * Get a list of all previously created [Chat]s
     *
     * @see Chat
     */

    private fun fetchChatList() {
        val eventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    for (snap in snapshot.children){
                        snap.getValue<Chat>()?.let { addChatToChatsList(it) }
                        Log.i(snap.key, (snap.getValue<Chat>()).toString())
                    }
                }catch (e: Exception){e.printStackTrace()}

            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        }
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            user.value?.uid?.let { uid ->
                database
                    .child("users")
                    .child(uid)
                    .child("chats")
                    .addValueEventListener(eventListener)
            }
        }
    }

    /**
     * Get all messages for a particular [Chat] using the [chatId]
     *
     * @param chatId unique identifier for every chat created
     *
     * @see Chat
     */

    fun fetchMessagesByChatId(chatId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)

            user.value?.uid?.let { uid ->
                database
                    .child("users")
                    .child(uid)
                    .child("messages")
                    .child(chatId.toString())
                    .orderByChild("id")
                    .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val tmpList = mutableListOf<ChatMessage>()
                                try {
                                    for (snap in snapshot.children){
                                        if (snap.getValue<ChatMessage>() != null)
                                            tmpList.add(snap.getValue<ChatMessage>()!!)
                                    }
                                    _chatMessageList.value = tmpList.toList()
                                }catch (e: Exception){e.printStackTrace()}
                                finally {
                                    if (_chatMessageList.value.isNotEmpty())
                                        updateLoadingState(LoadingState.COMPLETED)
                                    Log.i("Chat Message List Aft Retrieval from db", _chatMessageList.value.toString())
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                error.toException().printStackTrace()
                            }

                        }
                    )
            }
        }

    }

    fun clearAllChats(){
        viewModelScope.launch(Dispatchers.IO) {
            database.apply {
                user.value?.uid?.let { uid ->
                    child("users")
                        .child(uid)
                        .child("chats")
                        .removeValue()
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    child("users")
                        .child(uid)
                        .child("messages")
                        .removeValue()
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                }
            }
            _chats.update { emptyList() }
            _chatMessageList.update { emptyList() }
            _currentChat.update { Chat() }
        }
    }

    /**
     * Populate firebase db with each message i.e. messages from [Sender.USER] and [Sender.BOT]
     *
     * @see Chat
     * @see ChatMessage
     */
    fun insertMessage(message: ChatMessage, chatId: Int){
        viewModelScope.launch {
            user.value?.uid?.let {uid ->
                database
                    .child("users")
                    .child(uid)
                    .child("messages")
                    .child(chatId.toString())
                    .child("m${message.id.toString()}")
                    .setValue(message)
            }
            fetchMessagesByChatId(chatId)
            Log.i("Insert Msg to Db", "Mssg: $message === Id: $chatId")
        }
    }

    fun updateCurrentChat(chat: Chat?){_currentChat.update { chat }}

    fun updateLoadingState(loadingState: LoadingState){_loadingState.update { loadingState }}

    fun updateApiRespError(isError: Boolean){_apiRespError.update { isError }}

    fun updateUser(user: FirebaseUser?) { _user.update { user } }




    private fun addChatToChatsList(chat: Chat) {
        _chats.update {
            val newList = mutableListOf<Chat>()
            if (it.isNotEmpty())
                newList.addAll(it)
            if (chat !in it)
                newList.add(chat)
            newList.toList()
        }
    }

    fun addChatToDb(chat: Chat){
        viewModelScope.launch {
            user.value?.uid?.let {uid ->
                database
                    .child("users")
                    .child(uid)
                    .child("chats")
                    .child(chat.id.toString())
                    .setValue(chat) }
        }
    }

}