package io.github.transfusion.nitroless.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NitrolessRepoDao {
    @Query("SELECT * FROM nitrolessrepo WHERE id = :id")
    fun getRepoById(id: UUID): Flow<NitrolessRepo>

    @Query("SELECT * FROM nitrolessrepo")
    fun getAll(): Flow<List<NitrolessRepo>>

    @Insert
    suspend fun insertAll(vararg repos: NitrolessRepo)

    /**
     * To be used only during initial population
     */
    @Insert
    fun insertAllSync(vararg repos: NitrolessRepo)

    @Delete
    suspend fun delete(repo: NitrolessRepo)
}