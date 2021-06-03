package io.github.transfusion.nitroless.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.EmoteCellBinding

// needed because of FastAdapter
class BindingEmoteCellItem(
    val baseUrl: String,
    val path: String,
    private val nitrolessRepoEmoteModel: NitrolessRepoEmoteModel
) : AbstractBindingItem<EmoteCellBinding>(),
    IExpandable<BindingViewHolder<EmoteCellBinding>> {

    override var parent: IParentItem<*>? = null
    override var isExpanded: Boolean = false


    override fun bindView(binding: EmoteCellBinding, payloads: List<Any>) {
        binding.apply {
            baseUrl = this@BindingEmoteCellItem.baseUrl
            path = this@BindingEmoteCellItem.path
            emoteModel = nitrolessRepoEmoteModel
            executePendingBindings()
        }
        /*icon?.let {
            binding.icon.icon = IconicsDrawable(binding.icon.context, it).apply {
                colorInt = binding.root.context.getThemeColor(R.attr.colorOnSurface)
            }
        }
        binding.name.text = icon?.name*/
    }

    override fun unbindView(binding: EmoteCellBinding) {

    }


    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): EmoteCellBinding {
        return EmoteCellBinding.inflate(inflater, parent, false)
    }

    override val isAutoExpanding: Boolean
        get() = true

    override val type: Int
        get() = ITEM_VIEW_TYPE_EMOTE_ITEM
    override var subItems: MutableList<ISubItem<*>>
        get() = mutableListOf()
        set(value) {}


}