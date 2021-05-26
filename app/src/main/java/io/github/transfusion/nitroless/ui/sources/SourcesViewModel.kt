package io.github.transfusion.nitroless.ui.sources

import androidx.lifecycle.*
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.storage.NitrolessRepository
import kotlinx.coroutines.launch

// https://developer.android.com/codelabs/android-room-with-a-view-kotlin
class SourcesViewModel(private val repository: NitrolessRepository) : ViewModel() {
    val repos: LiveData<List<NitrolessRepo>> = repository.repos.asLiveData()

    fun insert(repo: NitrolessRepo) = viewModelScope.launch {
        repository.insert(repo)
    }


    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}

class SourcesViewModelFactory(private val repository: NitrolessRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SourcesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SourcesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
