package io.github.transfusion.nitroless.viewholders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
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


}