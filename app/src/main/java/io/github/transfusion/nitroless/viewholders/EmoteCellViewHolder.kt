package io.github.transfusion.nitroless.viewholders

import android.util.Log
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.EmoteCellBinding
import java.net.URI


class EmoteCellViewHolder(
    private val binding: EmoteCellBinding,
    onEmoteClicked: (NitrolessRepoEmoteModel) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.setClickListener {
            if (binding.emoteModel != null)
                onEmoteClicked(binding.emoteModel!!)
            /*toast?.cancel()
            Log.d(javaClass.name, "clicked on ${binding.emoteModel?.name}")

            val text = "Copied ${binding.emoteModel?.name}"
            val duration = Toast.LENGTH_SHORT
            toast = Toast.makeText(it.context, text, duration)
            toast?.show()*/
        }
    }

    fun bind(_baseUrl: String, _path: String, item: NitrolessRepoEmoteModel) {
        binding.apply {
            baseUrl = _baseUrl
            path = _path
            emoteModel = item
            executePendingBindings()
        }
    }

    // this also applies to BindingEmoteCellItem.kt! it manages its
    // own ViewHolder
    companion object {
        private var toast: Toast? = null

        @JvmStatic
        @BindingAdapter(value = ["bind:baseUrl", "bind:path", "bind:imageUrl"])
        fun loadImage(
            view: ShapeableImageView,
            baseUrl: String?,
            path: String?,
            imageUrl: String?
        ) { // This methods should not have any return type, = declaration would make it return that object declaration.
            val uri = URI(baseUrl)
            val newPath = "${uri.path}/$path/$imageUrl"
//            val newPath = uri.path + '/' + imageUrl
            val newUri = uri.resolve(newPath)
            val url = newUri.normalize().toURL().toString()

            if (!url.isNullOrEmpty()) {
                /*view.hierarchy.setFailureImage(
                    ContextCompat.getDrawable(
                        view.context,
                        R.drawable.ic_baseline_help_outline_24
                    )
                )
                view.controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(ImageRequest.fromUri(url))
                    .setAutoPlayAnimations(true)
                    .build();*/

                Glide.with(view.context).load(url)
                    .error(R.drawable.ic_baseline_help_outline_24).into(view)
            }

        }
    }

}