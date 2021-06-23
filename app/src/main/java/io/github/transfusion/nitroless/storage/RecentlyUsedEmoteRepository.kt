package io.github.transfusion.nitroless.storage

import androidx.annotation.WorkerThread
import io.github.transfusion.nitroless.util.Constants
import kotlinx.coroutines.flow.Flow

class RecentlyUsedEmoteRepository(private val recentlyUsedEmoteDao: RecentlyUsedEmoteDao) {

    val recentlyUsedEmotes: Flow<List<RecentlyUsedEmoteAndRepo>> =
        recentlyUsedEmoteDao.getAllSorted()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vararg recentlyUsedEmotes: RecentlyUsedEmote) {
        recentlyUsedEmoteDao.insertAll(*recentlyUsedEmotes)
        // now truncate
        recentlyUsedEmoteDao.deleteEntriesOutsideLastN(Constants.RECENTLY_USED_MAX)
    }

    @WorkerThread
    fun deleteAll() {
        recentlyUsedEmoteDao.deleteAll()
    }
}