package io.github.transfusion.nitroless

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import io.github.transfusion.nitroless.storage.NitrolessRepoDatabase
import io.github.transfusion.nitroless.storage.NitrolessRepository
import io.github.transfusion.nitroless.storage.RecentlyUsedEmoteRepository

class NitrolessApplication : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
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
        // https://stackoverflow.com/questions/64941799/howto-access-preferences-created-by-oncreatepreferences-in-another-part-of-app
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this)

        val value = prefs.getString("theme", "dark")
        onThemeChanged(value)
        super.onCreate()
//        Fresco.initialize(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val value = sharedPreferences?.getString(key, "dark")
        if (key == "theme") {
            onThemeChanged(value)
        }
    }

    private fun onThemeChanged(value: String?) {
        when (value) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}