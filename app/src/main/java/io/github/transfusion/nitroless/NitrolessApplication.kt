package io.github.transfusion.nitroless

import android.app.Application
import io.github.transfusion.nitroless.storage.NitrolessRepoDatabase
import io.github.transfusion.nitroless.storage.NitrolessRepository

class NitrolessApplication : Application() {
    val database by lazy { NitrolessRepoDatabase.getDatabase(this) }
    val repository by lazy { NitrolessRepository(database.nitrolessDao()) }
}