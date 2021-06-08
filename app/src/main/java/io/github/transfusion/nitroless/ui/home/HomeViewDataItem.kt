package io.github.transfusion.nitroless.ui.home

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.storage.NitrolessRepo

const val ITEM_VIEW_TYPE_HEADER = 10010
const val ITEM_VIEW_TYPE_EMOTE_ITEM = 10011
const val ITEM_VIEW_TYPE_MESSAGE_ITEM = 10012 // e.g. "repo failed to load"

sealed class DataItem {
    data class HeaderItem(
        override var id: Int, val groupIndex: Int, val nitrolessRepo: NitrolessRepo,
        val subItems: Int
    ) : DataItem() {
        override val type = ITEM_VIEW_TYPE_HEADER

        var expanded = true

        override fun equals(other: Any?): Boolean {
//            return super.equals(other)
            if (other !is HeaderItem) return false
            val castedOther = other as HeaderItem
            return castedOther.nitrolessRepo == nitrolessRepo && castedOther.expanded == expanded &&
                    id == castedOther.id
        }
    }

    data class EmoteItem(
        override var id: Int,
        val nitrolessRepo: NitrolessRepo,
        val nitrolessRepoModel: NitrolessRepoModel,
        val emote: NitrolessRepoEmoteModel
    ) : DataItem() {
        override val type = ITEM_VIEW_TYPE_EMOTE_ITEM

        override fun equals(other: Any?): Boolean {
//            return super.equals(other)
            if (other !is EmoteItem) return false
            val castedOther = other as EmoteItem
            return castedOther.emote == other.emote &&
                    castedOther.nitrolessRepoModel == other.nitrolessRepoModel
                    && castedOther.nitrolessRepo == other.nitrolessRepo &&
                    id == castedOther.id
        }
    }

    data class MessageItem(override var id: Int, val message: String) : DataItem() {
        override val type = ITEM_VIEW_TYPE_MESSAGE_ITEM

        override fun equals(other: Any?): Boolean {
            if (other !is MessageItem) return false
            val castedOther = other as MessageItem
            return other.message == castedOther.message && id == castedOther.id

        }
    }


    abstract val id: Int
    abstract val type: Int
}

class HomeSectionedDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        if (oldItem.type != newItem.type) return false
        return oldItem == newItem
    }
}
