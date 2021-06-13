package io.github.transfusion.nitroless.ui.home

import androidx.recyclerview.widget.DiffUtil
import io.github.transfusion.nitroless.storage.RecentlyUsedEmoteAndRepo

const val ITEM_VIEW_TYPE_RECENT_HEADER = 10020
const val ITEM_VIEW_TYPE_RECENT_EMOTE_ITEM = 10021

sealed class RecentlyUsedDataItem {
    // only one header needed..
    data class HeaderItem(
        override var id: Int
    ) : RecentlyUsedDataItem() {
        override val type = ITEM_VIEW_TYPE_RECENT_HEADER
    }

    data class EmoteItem(
        override var id: Int,
        val recentlyUsedEmoteAndRepo: RecentlyUsedEmoteAndRepo
    ) : RecentlyUsedDataItem() {
        override val type = ITEM_VIEW_TYPE_RECENT_EMOTE_ITEM
    }

    abstract val id: Int
    abstract val type: Int
}

class RecentlyUsedDiffCallback : DiffUtil.ItemCallback<RecentlyUsedDataItem>() {
    override fun areItemsTheSame(
        oldItem: RecentlyUsedDataItem,
        newItem: RecentlyUsedDataItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RecentlyUsedDataItem,
        newItem: RecentlyUsedDataItem
    ): Boolean {
        if (oldItem.type != newItem.type) return false
        return oldItem == newItem
    }

}