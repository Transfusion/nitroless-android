package io.github.transfusion.nitroless.viewholders

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.EmoteCellBinding
import java.net.URI

class EmoteCellViewHolder(private val binding: EmoteCellBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.setClickListener {
            Log.d(javaClass.name, "clicked on ${binding.emoteModel?.name}")
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


    companion object {
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

            if (!url.isNullOrEmpty())
                Glide.with(view.context).load(url).into(view)
        }
    }

}