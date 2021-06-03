package io.github.transfusion.nitroless.ui.home.expandable

import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IClickable
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceHeaderBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.ui.home.ITEM_VIEW_TYPE_HEADER

// https://github.com/mikepenz/FastAdapter/issues/752
// issue with data binding
open class HomeExpandableHeaderItem(private val repo: NitrolessRepo) :
    AbstractExpandableItem<BindingViewHolder<HomeRecyclerviewSourceHeaderBinding>>(),
    IClickable<HomeExpandableHeaderItem>,
    ISubItem<BindingViewHolder<HomeRecyclerviewSourceHeaderBinding>> {

    private var mOnClickListener: ClickListener<HomeExpandableHeaderItem>? = null

    override val type: Int
        get() = ITEM_VIEW_TYPE_HEADER

    override val layoutRes: Int
        get() = R.layout.home_recyclerview_source_header


    override fun bindView(
        holder: BindingViewHolder<HomeRecyclerviewSourceHeaderBinding>,
        payloads: List<Any>
    ) {
        super.bindView(holder, payloads)
        holder.binding.apply {
            repo = this@HomeExpandableHeaderItem.repo
        }
    }

    override fun unbindView(holder: BindingViewHolder<HomeRecyclerviewSourceHeaderBinding>) {
        super.unbindView(holder)
    }
    /*override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        val ctx = holder.itemView.context
        holder.sourceNameTextView.text = name
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.sourceNameTextView.text = null
    }*/


    override var onItemClickListener: ClickListener<HomeExpandableHeaderItem>? =
        { view: View?, iAdapter: IAdapter<HomeExpandableHeaderItem>, homeExpandableHeaderItem: HomeExpandableHeaderItem, position: Int ->
            Log.d(javaClass.name, "clicked home header position $position")


            mOnClickListener?.invoke(view, iAdapter, homeExpandableHeaderItem, position) ?: true
        }
        set(onClickListener) {
            this.mOnClickListener = onClickListener // on purpose
        }

    override var onPreItemClickListener: ClickListener<HomeExpandableHeaderItem>?
        get() = null
        set(_) {}

    override fun getViewHolder(v: View): BindingViewHolder<HomeRecyclerviewSourceHeaderBinding> {
        val binding = DataBindingUtil.bind<HomeRecyclerviewSourceHeaderBinding>(v)
        return BindingViewHolder(binding!!)
    }


}