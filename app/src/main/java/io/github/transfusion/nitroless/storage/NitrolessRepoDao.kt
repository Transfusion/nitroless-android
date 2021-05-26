package io.github.transfusion.nitroless.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NitrolessRepoDao {
    @Query("SELECT * FROM nitrolessrepo")
    fun getAll(): Flow<List<NitrolessRepo>>

    @Insert
    suspend fun insertAll(vararg repos: NitrolessRepo)

    @Delete
    suspend fun delete(repo: NitrolessRepo)
}