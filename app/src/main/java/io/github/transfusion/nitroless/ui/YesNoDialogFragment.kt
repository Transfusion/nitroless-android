package io.github.transfusion.nitroless.ui

import android.app.Activity
import android.os.Bundle

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class YesNoDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle? = arguments
        val title = args?.getString(ARG_TITLE)
        val message = args?.getString(ARG_MESSAGE)
        val extra = args?.getString(ARG_EXTRA_STRING)
        return activity?.let {
            val intent = Intent();
            intent.putExtra(RESULT_EXTRA_STRING, extra)

            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    targetFragment?.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        intent
                    )
                }
                .setNegativeButton(
                    "No"
                ) { dialog, which ->
                    targetFragment?.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_CANCELED,
                        null
                    )
                }
                .create()
        }!!
    }

    companion object {
        const val ARG_TITLE = "title"
        const val ARG_MESSAGE = "message"
        const val ARG_EXTRA_STRING = "extra"

        const val RESULT_EXTRA_STRING = "extra"
    }
}