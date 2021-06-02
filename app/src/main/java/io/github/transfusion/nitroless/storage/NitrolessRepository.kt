package io.github.transfusion.nitroless.storage

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class NitrolessRepository(private val nitrolessRepoDao: NitrolessRepoDao) {
    val repos: Flow<List<NitrolessRepo>> = nitrolessRepoDao.getAll();
    
    fun getRepoById(id: Int) = nitrolessRepoDao.getRepoById(id)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vararg repos: NitrolessRepo) {
        nitrolessRepoDao.insertAll(*repos)
    }

}