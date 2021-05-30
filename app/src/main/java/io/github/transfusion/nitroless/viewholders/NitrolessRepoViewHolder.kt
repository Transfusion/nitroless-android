package io.github.transfusion.nitroless.viewholders

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import io.github.transfusion.nitroless.databinding.SourcesRowBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.ui.sources.SourcesFragmentDirections

class NitrolessRepoViewHolder(private val binding: SourcesRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.setClickListener {
            Log.d(javaClass.name, "clicked on ${binding.repo}")

            binding.repo?.id?.let { id ->
                val direction =
                    SourcesFragmentDirections.actionNavigationSourcesToNavigationSingleSource(
                        id
                    )

                it.findNavController().navigate(direction)
            }

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