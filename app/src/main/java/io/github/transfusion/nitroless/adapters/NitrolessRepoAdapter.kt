package io.github.transfusion.nitroless.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import io.github.transfusion.nitroless.databinding.SourcesRowBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.viewholders.NitrolessRepoViewHolder

class NitrolessRepoAdapter(private val deleteFunction: (NitrolessRepo) -> Unit) :
    ListAdapter<NitrolessRepo, NitrolessRepoViewHolder>(NitrolessRepoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NitrolessRepoViewHolder {
        return NitrolessRepoViewHolder(
            SourcesRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), deleteFunction
        )
    }

    override fun onBindViewHolder(holder: NitrolessRepoViewHolder, position: Int) {
        val repo = getItem(position)
        holder.bind(repo)
    }

}

class NitrolessRepoDiffCallback : ItemCallback<NitrolessRepo>() {
    override fun areItemsTheSame(oldItem: NitrolessRepo, newItem: NitrolessRepo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NitrolessRepo, newItem: NitrolessRepo): Boolean {
        return oldItem.name == newItem.name && oldItem.url == newItem.url
    }

}