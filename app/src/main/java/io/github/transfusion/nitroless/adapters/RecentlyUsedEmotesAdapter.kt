package io.github.transfusion.nitroless.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.EmoteCellBinding
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewRecentHeaderBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.storage.RecentlyUsedEmoteAndRepo
import io.github.transfusion.nitroless.ui.home.ITEM_VIEW_TYPE_RECENT_EMOTE_ITEM
import io.github.transfusion.nitroless.ui.home.ITEM_VIEW_TYPE_RECENT_HEADER
import io.github.transfusion.nitroless.ui.home.RecentlyUsedDataItem
import io.github.transfusion.nitroless.ui.home.RecentlyUsedDiffCallback
import io.github.transfusion.nitroless.viewholders.EmoteCellViewHolder
import io.github.transfusion.nitroless.viewholders.HomeRecentlyUsedHeaderViewHolder
import kotlinx.coroutines.*

class RecentlyUsedEmotesAdapter(val onItemClicked: (NitrolessRepo, String, NitrolessRepoEmoteModel) -> Job) :
    ListAdapter<RecentlyUsedDataItem, RecyclerView.ViewHolder>(RecentlyUsedDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun massageDataAndSubmitList(list: List<RecentlyUsedEmoteAndRepo>) {
        val dataItems = ArrayList<RecentlyUsedDataItem>()
        adapterScope.launch {
            dataItems.add(RecentlyUsedDataItem.HeaderItem(0))
            var i = 1
            for (recentlyUsedEmoteAndRepo in list) {
                dataItems.add(RecentlyUsedDataItem.EmoteItem(i++, recentlyUsedEmoteAndRepo))
            }

            withContext(Dispatchers.Main) {
                super.submitList(dataItems)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_RECENT_HEADER -> HomeRecentlyUsedHeaderViewHolder(
                HomeRecyclerviewRecentHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            ITEM_VIEW_TYPE_RECENT_EMOTE_ITEM -> EmoteCellViewHolder(
                EmoteCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return item.type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeRecentlyUsedHeaderViewHolder -> {
                // nothing to bind here
            }
            is EmoteCellViewHolder -> {
                val item = getItem(position) as RecentlyUsedDataItem.EmoteItem
                val url = item.recentlyUsedEmoteAndRepo.repo.url
                val emotePath = item.recentlyUsedEmoteAndRepo.recentlyUsedEmote.emote_path

                val emoteName = item.recentlyUsedEmoteAndRepo.recentlyUsedEmote.emote_name
                val emoteType = item.recentlyUsedEmoteAndRepo.recentlyUsedEmote.emote_type

                val nitrolessRepoEmoteModel =
                    NitrolessRepoEmoteModel(name = emoteName, type = emoteType)
                holder.bind(
                    url,
                    emotePath,
                    nitrolessRepoEmoteModel
                ) {
                    onItemClicked(
                        item.recentlyUsedEmoteAndRepo.repo,
                        emotePath,
                        nitrolessRepoEmoteModel
                    )
                }
            }
        }
    }

}