package io.github.transfusion.nitroless.ui.interfaces

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.storage.NitrolessRepo
import java.net.URI

interface EmoteClickedInterface {

    fun onEmoteClicked(
        nitrolessRepo: NitrolessRepo,
        nitrolessRepoModel: NitrolessRepoModel,
        emote: NitrolessRepoEmoteModel,

        coordinatorLayout: CoordinatorLayout,
        context: Context
    ) {
        Log.d(javaClass.name, "clicked on ${emote.name}")
        val mySnackbar =
            Snackbar.make(
                coordinatorLayout,
                "Copied ${emote.name}",
                Snackbar.LENGTH_SHORT
            ).setAction("OK") {
                // Responds to click on the action
            }
        // https://stackoverflow.com/questions/31746300/how-to-show-snackbar-at-top-of-the-screen/36768267
        // not material-spec compliant but better than obscuring the
        // bottom row
        val view: View = mySnackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.width = CoordinatorLayout.LayoutParams.WRAP_CONTENT
        view.layoutParams = params

        mySnackbar.show()

        // construct the URI
        var javaURI = URI(nitrolessRepo.url)
        val newPath =
            "${javaURI.path}/${nitrolessRepoModel.path}/${emote.name}${emote.type}"
        javaURI = javaURI.resolve(newPath)

        // copy the URI to clipboard
        val clip: ClipData = ClipData.newPlainText("Nitroless Emote", javaURI.toString())

        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.setPrimaryClip(clip)

    }
}