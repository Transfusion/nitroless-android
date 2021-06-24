package io.github.transfusion.nitroless.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import java.util.concurrent.Executors

@Database(
    entities = [NitrolessRepo::class, RecentlyUsedEmote::class],
    version = 4
)
@TypeConverters(DBTypeConverters::class)
abstract class NitrolessRepoDatabase : RoomDatabase() {
    abstract fun nitrolessDao(): NitrolessRepoDao
    abstract fun recentlyUsedEmoteDao(): RecentlyUsedEmoteDao


    companion object {
        @JvmField
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE recentlyusedemote (emote_id TEXT NOT NULL PRIMARY KEY, repoId TEXT NOT NULL, emote_path TEXT NOT NULL, emote_name TEXT NOT NULL, emote_type TEXT NOT NULL, emote_used INTEGER NOT NULL, FOREIGN KEY(`repoId`) REFERENCES `NitrolessRepo`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            }
        }

        @JvmField
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DELETE FROM recentlyusedemote")
                database.execSQL("CREATE UNIQUE INDEX index_unq_recently_used ON recentlyusedemote (repoId, emote_path, emote_name, emote_type)")
            }
        }

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


        var rdc: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // do something after database has been created
                Executors.newSingleThreadExecutor().execute {
                    INSTANCE?.nitrolessDao()?.insertAllSync(
                        NitrolessRepo(
                            name = "Amy's Repo",
                            url = "https://nitroless.github.io/ExampleNitrolessRepo"
                        ), NitrolessRepo(
                            name = "alpha's repo",
                            url = "https://thealphastream.github.io/emojis"
                        )
                    )
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                // do something every time database is open
            }
        }

        fun getDatabase(context: Context): NitrolessRepoDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NitrolessRepoDatabase::class.java,
                    "nitroless"
                ).addCallback(rdc).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}