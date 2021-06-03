package io.github.transfusion.nitroless.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import io.github.transfusion.nitroless.NitrolessApplication.Companion.applicationContext
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceMessageBinding

class BindingMessageItem(private val messageResourceId: Int) :
    AbstractBindingItem<HomeRecyclerviewSourceMessageBinding>(),
    IExpandable<BindingViewHolder<HomeRecyclerviewSourceMessageBinding>> {


    override var parent: IParentItem<*>? = null
    override var isExpanded: Boolean = false

    override fun bindView(binding: HomeRecyclerviewSourceMessageBinding, payloads: List<Any>) {
        binding.apply {
            val s = applicationContext().resources.getString(messageResourceId)
            message = s
            executePendingBindings()
        }
    }

    override val type: Int
        get() = ITEM_VIEW_TYPE_MESSAGE_ITEM
    override val isAutoExpanding: Boolean
        get() = true
    override var subItems: MutableList<ISubItem<*>>
        get() = mutableListOf()
        set(value) {}

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): HomeRecyclerviewSourceMessageBinding {
        return HomeRecyclerviewSourceMessageBinding.inflate(inflater, parent, false)
    }
}