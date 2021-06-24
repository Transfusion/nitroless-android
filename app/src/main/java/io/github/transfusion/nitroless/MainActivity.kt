package io.github.transfusion.nitroless

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.transfusion.nitroless.databinding.ActivityMainBinding


class EnableKeyboardDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val callbacks = requireActivity() as EnableKeyboardDialogCallback
        return AlertDialog.Builder(requireActivity())
            .setTitle("Enable Nitroless Keyboard")
            .setMessage("Send emotes directly from the Nitroless keyboard into Discord and other apps! If you're just looking around, you may enable it in the in-app or system settings later. Enable now?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                callbacks.positiveClick()
            }.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                callbacks.negativeClick()
            }.create()
    }
}

interface EnableKeyboardDialogCallback {
    fun positiveClick()
    fun negativeClick()
}

class MainActivity : AppCompatActivity(), EnableKeyboardDialogCallback {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_sources
//                , R.id.navigation_notifications
            )
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(this.localClassName, "Destination $destination");
        }


//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        checkKeyboardEnabled()
    }

    private fun checkKeyboardEnabled() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).let {
            val enabledKeyboard = it.enabledInputMethodList.any { it.packageName == packageName }
            val dialogShown =
                supportFragmentManager.findFragmentByTag("ENABLE_KEYBOARD_DIALOG_TAG") != null
            if (!enabledKeyboard && !dialogShown) {
                // Show setting dialog
                showEnableKeyboardDialog()
            }
        }
    }

    private fun showEnableKeyboardDialog() {
        val fragment = EnableKeyboardDialogFragment()
        fragment.show(supportFragmentManager, "ENABLE_KEYBOARD_DIALOG_TAG")
    }

    override fun positiveClick() {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
        startActivity(intent)
    }

    override fun negativeClick() {

    }

}