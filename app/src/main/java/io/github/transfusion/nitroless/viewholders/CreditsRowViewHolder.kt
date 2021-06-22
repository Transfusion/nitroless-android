package io.github.transfusion.nitroless.viewholders

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
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

        if (!_creditsDataItem.github_username.isNullOrEmpty())
            binding.githubLinkBtn.setOnClickListener {
                val url = "https://github.com/${_creditsDataItem.github_username}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(binding.githubLinkBtn.context, i, null)
            }
        if (!_creditsDataItem.twitter_username.isNullOrEmpty())
            binding.twitterLinkBtn.setOnClickListener {
                val url = "https://twitter.com/${_creditsDataItem.twitter_username}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(binding.githubLinkBtn.context, i, null)
            }

    }

    companion object {
        @JvmStatic
        @BindingAdapter("creditsDataItem")
        fun loadImage(
            view: androidx.appcompat.widget.AppCompatImageView,
            creditsDataItem: CreditsDataItem
        ) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (!creditsDataItem.github_username.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load("https://github.com/${creditsDataItem.github_username}.png")
                    .error(R.drawable.ic_baseline_help_outline_24)
                    .circleCrop().into(view)
            } else if (!creditsDataItem.twitter_username.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load("https://unavatar.now.sh/twitter/${creditsDataItem.twitter_username}")
                    .error(R.drawable.ic_baseline_help_outline_24)
                    .circleCrop().into(view)
            }
        }
    }

}