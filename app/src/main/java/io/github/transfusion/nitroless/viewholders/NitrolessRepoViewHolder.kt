package io.github.transfusion.nitroless.viewholders

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import io.github.transfusion.nitroless.databinding.SourcesRowBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo

class NitrolessRepoViewHolder(private val binding: SourcesRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.setClickListener {
            Log.d(javaClass.name, "clicked on ${binding.repo}")
        }
    }

    fun bind(item: NitrolessRepo) {
        binding.apply {
            repo = item
            executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(
            view: ShapeableImageView,
            url: String?
        ) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (!url.isNullOrEmpty())
                Glide.with(view.context).load(url).into(view)
        }
    }


}