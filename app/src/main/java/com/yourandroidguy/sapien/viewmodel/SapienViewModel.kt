package com.yourandroidguy.sapien.viewmodel

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
import com.yourandroidguy.sapien.model.Chat
import com.yourandroidguy.sapien.model.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SapienViewModel: ViewModel() {

    private val database = Firebase.database.reference

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user = _user.asStateFlow()

    private val _chatMessageList = MutableStateFlow(listOf<ChatMessage>())
    val chatMessageList
        get() =  _chatMessageList.asStateFlow()

    private val _chats = MutableStateFlow(listOf<Chat>())
    val chatsList: StateFlow<List<Chat>>
        get() = _chats

    private val _chatMessageMap = MutableStateFlow(mapOf<String, List<ChatMessage>>())
    val chatMessageMap
        get() =  _chatMessageMap.asStateFlow()

    private var _loadingState = MutableStateFlow(LoadingState.CANCELLED)
    val loadingState = _loadingState.asStateFlow()


    /**
     * Backing property to store a reference to the current [Chat]
     */
    private val _currentChat = MutableStateFlow<Chat?>(null)

    /**
     * Public reference to the current [Chat]
     */
    val currentChat = _currentChat.asStateFlow()

    init {
        fetchChatList()
    }

    enum class LoadingState{
        LOADING, COMPLETED, CANCELLED
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
        viewModelScope.launch {
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
        viewModelScope.launch {
            delay(2000)

            user.value?.uid?.let { uid ->
                database
                    .child("users")
                    .child(uid)
                    .child("messages")
                    .child(chatId.toString())
                    .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val tmpList = mutableListOf<ChatMessage>()
                                try {
                                    for (snap in snapshot.children){
                                        snap.getValue<ChatMessage>()?.let { tmpList.add(it) }
                                        Log.i(snap.key, (snap.getValue<ChatMessage>()).toString())
                                    }
                                }catch (e: Exception){e.printStackTrace()}
                                finally {
                                    if (tmpList.isNotEmpty())
                                        updateLoadingState(LoadingState.COMPLETED)
                                    _chatMessageList.value = tmpList.toList()
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
        viewModelScope.launch {
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

        }
    }

    fun updateCurrentChat(chat: Chat?){_currentChat.update { chat }}

    fun updateLoadingState(loadingState: LoadingState){_loadingState.update { loadingState }}

    fun updateUser(user: FirebaseUser?) {
        _user.update { user }
        Log.i("USER", user?.uid.toString())
    }

    fun addChatToChatsList(chat: Chat) {
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