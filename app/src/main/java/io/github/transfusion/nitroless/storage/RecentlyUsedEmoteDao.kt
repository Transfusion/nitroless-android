package io.github.transfusion.nitroless.storage

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyUsedEmoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg recentlyUsedEmotes: RecentlyUsedEmote)

    @Transaction
    @Query("SELECT * FROM recentlyusedemote")
    fun getAll(): Flow<List<RecentlyUsedEmoteAndRepo>>

//    @Transaction
//    @Query("SELECT * FROM recentlyusedemote WHERE id = :id")
//    fun getRecentlyUsedEmoteById(id: UUID): Flow<RecentlyUsedEmoteAndRepo>

    // "largest" date is the most recent
    @Transaction
    @Query("SELECT * FROM recentlyusedemote ORDER BY emote_used DESC")
    fun getAllSorted(): Flow<List<RecentlyUsedEmoteAndRepo>>

    @Query("DELETE FROM recentlyusedemote WHERE emote_id NOT IN (SELECT emote_id from recentlyusedemote ORDER BY emote_used DESC LIMIT :n)")
    fun deleteEntriesOutsideLastN(n: Int): Int
}