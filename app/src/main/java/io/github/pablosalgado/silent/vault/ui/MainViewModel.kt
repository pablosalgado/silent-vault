package io.github.pablosalgado.silent.vault.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.pablosalgado.silent.vault.data.NotificationRepository
import io.github.pablosalgado.silent.vault.data.local.NotificationEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    val notifications: StateFlow<List<NotificationEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val unreviewedCount: StateFlow<Int> = repository.getUnreviewedCount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun markAsReviewed(id: Long) {
        viewModelScope.launch {
            repository.markAsReviewed(id)
        }
    }

    companion object {
        fun factory(repository: NotificationRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(repository) as T
                }
            }
        }
    }
}
