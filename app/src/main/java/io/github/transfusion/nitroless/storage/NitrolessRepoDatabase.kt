package io.github.transfusion.nitroless.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(NitrolessRepo::class), version = 1)
abstract class NitrolessRepoDatabase : RoomDatabase() {
    abstract fun nitrolessDao(): NitrolessRepoDao


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NitrolessRepoDatabase? = null

        fun getDatabase(context: Context): NitrolessRepoDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NitrolessRepoDatabase::class.java,
                    "nitroless"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}