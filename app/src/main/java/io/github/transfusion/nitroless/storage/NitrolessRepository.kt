package io.github.transfusion.nitroless.storage

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import java.util.*

class NitrolessRepository(private val nitrolessRepoDao: NitrolessRepoDao) {
    val repos: Flow<List<NitrolessRepo>> = nitrolessRepoDao.getAll();

    fun getRepoById(id: UUID) = nitrolessRepoDao.getRepoById(id)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vararg repos: NitrolessRepo) {
        nitrolessRepoDao.insertAll(*repos)
    }

    suspend fun removeRepo(repo: NitrolessRepo) {
        nitrolessRepoDao.delete(repo)
    }

}