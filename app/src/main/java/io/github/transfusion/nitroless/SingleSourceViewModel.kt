package io.github.transfusion.nitroless

import android.util.Log
import androidx.lifecycle.*
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.enums.LOADINGSTATUS
import io.github.transfusion.nitroless.network.NitrolessRepoEndpoints
import io.github.transfusion.nitroless.network.ServiceBuilder
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.storage.NitrolessRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SingleSourceViewModel(private val repoId: Int, private val repository: NitrolessRepository) :
    ViewModel() {

    private var _emotes: MutableLiveData<NitrolessRepoModel> = MutableLiveData()
    val emotes: LiveData<NitrolessRepoModel> = _emotes

    private var _status = MutableLiveData(LOADINGSTATUS.READY)
    val status: LiveData<LOADINGSTATUS> = _status

    val currentRepo: LiveData<NitrolessRepo> = repository.getRepoById(repoId).asLiveData()
    /*fun getRepoById(id: Int): LiveData<NitrolessRepo> {
        return repository.getRepoById(id).asLiveData()
    }*/

    init {
        viewModelScope.launch {
            loadEmotes()
        }
    }

    private suspend fun loadEmotes() {
        _status.value = LOADINGSTATUS.LOADING
        try {
            val url = repository.getRepoById(repoId).first().url
            val serviceBuilder = ServiceBuilder(url)
            val request = serviceBuilder.buildService(NitrolessRepoEndpoints::class.java)
            val indexResponse = request.getIndex()
            _emotes.value = indexResponse
            _status.value = LOADINGSTATUS.READY
        } catch (e: Exception) {
            Log.d(javaClass.name, e.toString())
            _status.value = LOADINGSTATUS.FAILED
        }
    }


}


class SingleSourceViewModelFactory(
    private val repoId: Int,
    private val repository: NitrolessRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleSourceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingleSourceViewModel(repoId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
