package io.github.transfusion.nitroless.viewholders

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.databinding.CreditsRowBinding
import io.github.transfusion.nitroless.ui.home.bottomsheet.CreditsDataItem

class CreditsRowViewHolder(private val binding: CreditsRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(_creditsDataItem: CreditsDataItem) {
        binding.apply {
            creditsDataItem = _creditsDataItem
            executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("githubImageUrl", "twitterImageUrl")
        fun loadImage(
            view: androidx.appcompat.widget.AppCompatImageView,
            url: String?, url2: String?
        ) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (!url.isNullOrEmpty()) {
                Glide.with(view.context).load(url).error(R.drawable.ic_baseline_help_outline_24)
                    .circleCrop().into(view)
            } else if (!url2.isNullOrEmpty()) {
                Glide.with(view.context).load(url2).error(R.drawable.ic_baseline_help_outline_24)
                    .circleCrop().into(view)
            }
        }
    }

}