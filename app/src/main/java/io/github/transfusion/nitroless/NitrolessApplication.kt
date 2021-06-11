package io.github.transfusion.nitroless

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import io.github.transfusion.nitroless.storage.NitrolessRepoDatabase
import io.github.transfusion.nitroless.storage.NitrolessRepository
import io.github.transfusion.nitroless.storage.RecentlyUsedEmoteRepository

class NitrolessApplication : Application() {
    val database by lazy { NitrolessRepoDatabase.getDatabase(this) }
    val repository by lazy { NitrolessRepository(database.nitrolessDao()) }
    val recentlyUsedEmoteRepository by lazy { RecentlyUsedEmoteRepository(database.recentlyUsedEmoteDao()) }

    init {
        instance = this
    }

    companion object {
        private var instance: NitrolessApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}