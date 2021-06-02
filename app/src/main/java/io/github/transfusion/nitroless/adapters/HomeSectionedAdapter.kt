package io.github.transfusion.nitroless.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceHeaderBinding
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceRowBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.util.GridAutofitLayoutManager
import io.github.transfusion.nitroless.viewholders.HomeHeaderViewHolder
import io.github.transfusion.nitroless.viewholders.HomeSourceViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_REPO_ITEM = 1

// https://developer.android.com/codelabs/kotlin-android-training-headers#2
// On the other hand, using a different ViewHolder by checking indexes for headers gives more freedom on the layout of the header

// this is considered the parent adapter
class HomeSectionedAdapter :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(HomeSectionedDiffCallback()) {

    private val viewPool = RecyclerView.RecycledViewPool()

    var mOriginalList: List<NitroLessRepoAndModel>? = null
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeadersAndSubmitList(list: List<NitroLessRepoAndModel>?) {
        mOriginalList = list
        adapterScope.launch {
            val dataItems: ArrayList<DataItem> = arrayListOf<DataItem>()
            if (list != null) {
                var i = 0
                for (item in list) {
                    val header = (DataItem.Header(item.nitrolessRepo))
                    dataItems.add(header)
                    header.id = i++
                    val content = DataItem.RepoItem(item.nitrolessRepo, item.nitrolessRepoModel)
                    dataItems.add(content)
                    content.id = i++
                }
            }
            withContext(Dispatchers.Main) {
                submitList(dataItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                val binding = HomeRecyclerviewSourceHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return HomeHeaderViewHolder(binding)
            }
            ITEM_VIEW_TYPE_REPO_ITEM -> {
                val binding = HomeRecyclerviewSourceRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return HomeSourceViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = getItem(position)
        // if header, then bind the text
        when (dataItem) {
            is DataItem.Header -> {
                (holder as HomeHeaderViewHolder)
                    .bind(dataItem.nitrolessRepo)
            }
            is DataItem.RepoItem -> {
                (holder as HomeSourceViewHolder).binding.homeRecyclerViewSourceRow.apply {
                    layoutManager = GridAutofitLayoutManager(this.context, 150)
//                    layoutManager = GridLayoutManager(this.context, 8)
                    val singleSourceAdapter = SingleSourceAdapter(dataItem.nitrolessRepo.url)
                    singleSourceAdapter.path = dataItem.nitrolessRepoModel.path
                    adapter = singleSourceAdapter
                    singleSourceAdapter.submitList(dataItem.nitrolessRepoModel.emotes)
//                    recycledViewPool = viewPool
                    this.setRecycledViewPool(viewPool)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.RepoItem -> ITEM_VIEW_TYPE_REPO_ITEM
        }
    }


}

class HomeSectionedDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


sealed class DataItem {
    data class Header(val nitrolessRepo: NitrolessRepo) : DataItem() {
        override var id = nitrolessRepo.id
    }

    data class RepoItem(
        val nitrolessRepo: NitrolessRepo,
        val nitrolessRepoModel: NitrolessRepoModel
    ) :
        DataItem() {
        override var id = nitrolessRepo.id
    }


    abstract val id: Int
}


// combination of NitrolessRepo and NitrolessRepoModel
data class NitroLessRepoAndModel(
    val nitrolessRepo: NitrolessRepo,
    val nitrolessRepoModel: NitrolessRepoModel
)