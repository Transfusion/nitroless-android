package io.github.transfusion.nitroless.ui.home

import android.util.Log
import androidx.lifecycle.*
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.enums.LOADINGSTATUS
import io.github.transfusion.nitroless.network.NitrolessRepoEndpoints
import io.github.transfusion.nitroless.network.ServiceBuilder
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.storage.NitrolessRepository
import io.github.transfusion.nitroless.storage.RecentlyUsedEmote
import io.github.transfusion.nitroless.storage.RecentlyUsedEmoteRepository
import io.github.transfusion.nitroless.util.pmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// combination of NitrolessRepo and NitrolessRepoModel
data class NitrolessRepoAndModel(
    val nitrolessRepo: NitrolessRepo,
    val nitrolessRepoModel: NitrolessRepoModel?
)

class HomeViewModel(
    private val repository: NitrolessRepository,
    private val recentlyUsedEmoteRepository: RecentlyUsedEmoteRepository
) : ViewModel() {

    fun insertRecentlyUsed(recentlyUsedEmote: RecentlyUsedEmote) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            recentlyUsedEmoteRepository.insert(recentlyUsedEmote)
        }
    }

    private var _status = MutableLiveData(LOADINGSTATUS.READY)
    val status: LiveData<LOADINGSTATUS> = _status

    val repos: LiveData<List<NitrolessRepo>> = repository.repos.asLiveData()
    private val _nitrolessRepoAndModels: MutableLiveData<List<NitrolessRepoAndModel>> =
        MutableLiveData()

    val nitrolessRepoAndModels: LiveData<List<NitrolessRepoAndModel>> = _nitrolessRepoAndModels


    val recentlyUsedEmoteAndRepos = recentlyUsedEmoteRepository.recentlyUsedEmotes.asLiveData()

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
        refresh()
    }

    /*private suspend fun loadNitroLessRepoAndModels() {
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
    }*/

    fun refresh() {
        viewModelScope.launch {
            loadNitroLessRepoAndModels()
        }
    }

    private suspend fun loadNitroLessRepoAndModels() {
        _status.value = LOADINGSTATUS.LOADING
        // for each repository, get the emotes list
        val repoAndModels = repository.repos.first().pmap {
            val serviceBuilder = ServiceBuilder(it.url)
            val request = serviceBuilder.buildService(NitrolessRepoEndpoints::class.java)

            var indexResponse: NitrolessRepoModel? = null
            try {
                indexResponse = request.getIndex()
            } catch (e: Exception) {
                Log.d(javaClass.name, e.toString())
            }
            NitrolessRepoAndModel(nitrolessRepo = it, nitrolessRepoModel = indexResponse)
        }
        _nitrolessRepoAndModels.value = repoAndModels
        _status.value = LOADINGSTATUS.READY

    }

}


class HomeViewModelFactory(
    private val repository: NitrolessRepository,
    private val recentlyUsedEmoteRepository: RecentlyUsedEmoteRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, recentlyUsedEmoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
