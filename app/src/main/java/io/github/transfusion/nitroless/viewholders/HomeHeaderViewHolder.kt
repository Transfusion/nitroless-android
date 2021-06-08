package io.github.transfusion.nitroless.viewholders

import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceHeaderBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo

class HomeHeaderViewHolder(private val binding: HomeRecyclerviewSourceHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(_repo: NitrolessRepo, function: () -> Unit) {
        binding.apply {
            repo = _repo
            executePendingBindings()
        }
        binding.setClickListener {
            function()
        }
    }


}