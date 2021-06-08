package io.github.transfusion.nitroless.viewholders

import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceMessageBinding

class HomeMessageItemViewHolder(private val binding: HomeRecyclerviewSourceMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(msg: String) {
        binding.apply {
            message = msg
            executePendingBindings()
        }
    }
}