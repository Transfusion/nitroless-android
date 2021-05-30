package io.github.transfusion.nitroless.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.EmoteCellBinding
import io.github.transfusion.nitroless.viewholders.EmoteCellViewHolder

class EmoteCellAdapter(private val baseUrl: String) :
    ListAdapter<NitrolessRepoEmoteModel, EmoteCellViewHolder>(NitrolessRepoEmoteModelDiffCallback()) {

    var path: String? = null // in the index.json, must be set before submitList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmoteCellViewHolder {
        return EmoteCellViewHolder(
            EmoteCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EmoteCellViewHolder, position: Int) {
        val emoteModel = getItem(position)
        holder.bind(baseUrl, path!!, emoteModel)
    }
}

class NitrolessRepoEmoteModelDiffCallback : ItemCallback<NitrolessRepoEmoteModel>() {
    override fun areItemsTheSame(
        oldItem: NitrolessRepoEmoteModel,
        newItem: NitrolessRepoEmoteModel
    ): Boolean {
        return oldItem.name == newItem.name && oldItem.type == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: NitrolessRepoEmoteModel,
        newItem: NitrolessRepoEmoteModel
    ): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

}