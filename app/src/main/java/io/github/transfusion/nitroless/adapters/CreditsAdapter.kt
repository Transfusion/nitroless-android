package io.github.transfusion.nitroless.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.github.transfusion.nitroless.databinding.CreditsRowBinding
import io.github.transfusion.nitroless.ui.home.bottomsheet.CreditsDataItem
import io.github.transfusion.nitroless.viewholders.CreditsRowViewHolder

class CreditsAdapter :
    ListAdapter<CreditsDataItem, CreditsRowViewHolder>(CreditsAdapterDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditsRowViewHolder {
        return CreditsRowViewHolder(
            CreditsRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CreditsRowViewHolder, position: Int) {
        val creditsDataItem = getItem(position)
        holder.bind(creditsDataItem)
    }

}

class CreditsAdapterDiffCallback : DiffUtil.ItemCallback<CreditsDataItem>() {
    override fun areItemsTheSame(oldItem: CreditsDataItem, newItem: CreditsDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CreditsDataItem, newItem: CreditsDataItem): Boolean {
        return oldItem == newItem
    }

}