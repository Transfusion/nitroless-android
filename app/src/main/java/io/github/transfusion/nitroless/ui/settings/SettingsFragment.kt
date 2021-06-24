package io.github.transfusion.nitroless.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R

class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceManager.OnPreferenceTreeClickListener {

    private lateinit var settingsFragmentCoordinatorLayout: CoordinatorLayout

    private val settingsViewModel: SettingsViewModel by viewModels {
        val app = (activity?.application as NitrolessApplication)
        SettingsViewModelFactory(app.recentlyUsedEmoteRepository)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsFragmentCoordinatorLayout =
            view.findViewById(R.id.settingsFragmentCoordinatorLayout)
        setupActionBar(view.findViewById(R.id.toolbarSettings))
    }

    private fun setupActionBar(toolbarSettings: Toolbar) {
        setHasOptionsMenu(true)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_sources
            )
        )

        toolbarSettings.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) {
            return when (preference.key) {
                "reset_recently_used" -> {
                    settingsViewModel.resetRecentlyUsed()
                    showResettedSnackbar()
                    true
                }
                "manage_keyboards" -> {
                    val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        return false
    }

    fun showResettedSnackbar() {
        val mySnackbar =
            Snackbar.make(
                settingsFragmentCoordinatorLayout,
                "Cleared all recently used emotes.",
                Snackbar.LENGTH_SHORT
            ).setAction("OK") {
                // Responds to click on the action
            }

        val view: View = mySnackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT
        view.layoutParams = params
        mySnackbar.show()
    }

}