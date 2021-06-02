package io.github.transfusion.nitroless.ui.home

import android.util.Log
import androidx.lifecycle.*
import io.github.transfusion.nitroless.LOADINGSTATUS
import io.github.transfusion.nitroless.adapters.NitroLessRepoAndModel
import io.github.transfusion.nitroless.network.NitrolessRepoEndpoints
import io.github.transfusion.nitroless.network.ServiceBuilder
import io.github.transfusion.nitroless.storage.NitrolessRepository
import io.github.transfusion.nitroless.util.pmap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


enum class LOADINGSTATUS { READY, LOADING, FAILED }
class HomeViewModel(private val repository: NitrolessRepository) : ViewModel() {

    private var _status = MutableLiveData(LOADINGSTATUS.READY)
    val status: LiveData<LOADINGSTATUS> = _status

    //    val repos: LiveData<List<NitrolessRepo>> = repository.repos.asLiveData()
    private val _nitrolessRepoAndModels: MutableLiveData<List<NitroLessRepoAndModel>> =
        MutableLiveData()

    val nitrolessRepoAndModels: LiveData<List<NitroLessRepoAndModel>> = _nitrolessRepoAndModels
    /*val nitrolessRepoAndModels = repository.repos.map { repo ->
        repo.pmap {
            val serviceBuilder = ServiceBuilder(it.url)
            val request = serviceBuilder.buildService(NitrolessRepoEndpoints::class.java)
            val indexResponse = request.getIndex()
            NitroLessRepoAndModel(nitrolessRepo = it, nitrolessRepoModel = indexResponse)
        }
    }.asLiveData()*/

    /* private val _text = MutableLiveData<String>().apply {
         value = "This is home Fragment"
     }
     val text: LiveData<String> = _text*/


    init {
        viewModelScope.launch {
            loadNitroLessRepoAndModels()
        }
    }

    private suspend fun loadNitroLessRepoAndModels() {
        _status.value = LOADINGSTATUS.LOADING
        // for each repository, get the emotes list
        try {
            val repoAndModels = repository.repos.first().pmap {
                val serviceBuilder = ServiceBuilder(it.url)
                val request = serviceBuilder.buildService(NitrolessRepoEndpoints::class.java)
                val indexResponse = request.getIndex()
                NitroLessRepoAndModel(nitrolessRepo = it, nitrolessRepoModel = indexResponse)
            }
            _nitrolessRepoAndModels.value = repoAndModels
            _status.value = LOADINGSTATUS.READY
        } catch (e: Exception) {
            Log.d(javaClass.name, e.toString())
            _status.value = LOADINGSTATUS.FAILED
        }
    }
}


class HomeViewModelFactory(private val repository: NitrolessRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
