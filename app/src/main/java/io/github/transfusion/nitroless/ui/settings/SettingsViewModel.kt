package io.github.transfusion.nitroless.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.transfusion.nitroless.storage.RecentlyUsedEmoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val recentlyUsedEmoteRepository: RecentlyUsedEmoteRepository) :
    ViewModel() {

    fun resetRecentlyUsed() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                recentlyUsedEmoteRepository.deleteAll()
            }
        }
    }
}

class SettingsViewModelFactory(
    private val recentlyUsedEmoteRepository: RecentlyUsedEmoteRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(recentlyUsedEmoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

