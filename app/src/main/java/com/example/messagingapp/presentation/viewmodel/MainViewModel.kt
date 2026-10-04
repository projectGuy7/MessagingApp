package com.example.messagingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.mapper.toDataMessageUI
import com.example.messagingapp.domain.messaging.DataMessage
import com.example.messagingapp.domain.repository.DataMessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    dataMessagesRepository: DataMessagesRepository
): ViewModel() {

    private val _dataMessages = dataMessagesRepository.observeDataMessages()
        .map { it.map(DataMessage::toDataMessageUI) }
    private val _state = MutableStateFlow(MainState())
    val state = combine(_dataMessages, _state) { dataMessages, currentState ->
        currentState.copy(dataMessages = dataMessages)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainState()
    )

    fun onIntent(intent: MainIntent) {
        viewModelScope.launch {
            when(intent) {
                is MainIntent.SetNotificationData -> {
                    _state.value = _state.value.copy(notificationData = intent.notificationData)
                }
                MainIntent.RemoveNotificationData -> {
                    _state.value = _state.value.copy(notificationData = null)
                }
            }
        }
    }

}