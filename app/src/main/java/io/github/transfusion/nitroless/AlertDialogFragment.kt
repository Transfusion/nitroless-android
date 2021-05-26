package io.github.transfusion.nitroless

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AlertDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle? = arguments
        val title = args?.getString(ARG_TITLE)
        val message = args?.getString(ARG_MESSAGE)
        return activity?.let {

            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                    "OK"
                ) { dialog, which ->
                }
                .create()
        }!!

    }

    companion object {
        const val ARG_TITLE = "title"
        const val ARG_MESSAGE = "message"
    }

}