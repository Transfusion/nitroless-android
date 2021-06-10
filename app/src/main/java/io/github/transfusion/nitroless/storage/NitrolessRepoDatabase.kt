package io.github.transfusion.nitroless.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Database(entities = [NitrolessRepo::class], version = 2)
@TypeConverters(DBTypeConverters::class)
abstract class NitrolessRepoDatabase : RoomDatabase() {
    abstract fun nitrolessDao(): NitrolessRepoDao


    companion object {
        @JvmField
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE nitrolessrepo_new (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, url TEXT NOT NULL)")
                // copy the data
                val cursor = database.query("SELECT * from nitrolessrepo")
                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))
                    // insert into the new tbl
                    // generate a random UUID
                    val id = UUID.randomUUID().toString()
                    database.execSQL("INSERT INTO nitrolessrepo_new VALUES (\"$id\", \"$name\", \"$url\")")
                }
                database.execSQL("DROP TABLE nitrolessrepo")
                database.execSQL("ALTER TABLE nitrolessrepo_new RENAME TO nitrolessrepo")
            }

        }

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
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}