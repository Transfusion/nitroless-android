package io.github.transfusion.nitroless.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.github.transfusion.nitroless.R

class AddNitrolessUrlFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputLayout = FrameLayout(requireContext())
        inputLayout.setPaddingRelative(45, 0, 45, 0)


        val args: Bundle? = arguments
        val title = args?.getString(ARG_TITLE)
        val message = args?.getString(ARG_MESSAGE)

        val input = EditText(context)
        input.hint = getString(R.string.sample_nitroless_repo_url)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        inputLayout.addView(input)

        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setView(inputLayout)
                .setPositiveButton(
                    "Confirm"
                ) { dialog, which ->
                    val intent = Intent();
                    intent.putExtra(RESULT_URL, input.text.toString());

                    targetFragment?.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        intent
                    )
                }
                .setNegativeButton(
                    "Cancel"
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
        const val ARG_TITLE = "Title"
        const val ARG_MESSAGE = "Message"

        const val RESULT_URL = "url"
    }
}
