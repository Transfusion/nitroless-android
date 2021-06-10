package io.github.transfusion.nitroless.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.databinding.EmoteCellBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.viewholders.EmoteCellViewHolder
import java.util.*

class SingleSourceAdapter(
    val onEmoteClicked: (NitrolessRepo, NitrolessRepoModel, NitrolessRepoEmoteModel) -> Unit,
    private val nitrolessRepo: NitrolessRepo
) : Filterable,
    ListAdapter<NitrolessRepoEmoteModel, EmoteCellViewHolder>(NitrolessRepoEmoteModelDiffCallback()) {

    var mListRef: List<NitrolessRepoEmoteModel>? = null
    var mFilteredList: List<NitrolessRepoEmoteModel>? = null

    var nitrolessRepoModel: NitrolessRepoModel? =
        null // in the index.json, must be set before submitList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmoteCellViewHolder {
        return EmoteCellViewHolder(
            EmoteCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        )
    }

    override fun submitList(list: List<NitrolessRepoEmoteModel>?) {
        if (mListRef == null) {
            mListRef = list
        }
        super.submitList(list)
    }

    override fun onBindViewHolder(holder: EmoteCellViewHolder, position: Int) {
        val emoteModel = getItem(position)
        holder.bind(nitrolessRepo.url, nitrolessRepoModel!!.path, emoteModel) {
            onEmoteClicked(nitrolessRepo, nitrolessRepoModel!!, emoteModel)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    mFilteredList = mListRef
                } else {
                    mListRef?.let {
                        val filteredList = arrayListOf<NitrolessRepoEmoteModel>()
                        for (dataItem in mListRef!!) {
//                            if (baseDataItem is BaseDataItem.DataItemWrapper) {
                            if (charString.toLowerCase(Locale.ENGLISH) in dataItem.name.toLowerCase(
                                    Locale.ENGLISH
                                )
                            ) {
                                filteredList.add(dataItem)
                            }
//                            }
                        }

                        mFilteredList = filteredList
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                if (results.values == null) return
                mFilteredList = results.values as List<NitrolessRepoEmoteModel>
                submitList(mFilteredList)
            }
        }
    }
}

class NitrolessRepoEmoteModelDiffCallback : ItemCallback<NitrolessRepoEmoteModel>() {
    override fun areItemsTheSame(
        oldItem: NitrolessRepoEmoteModel,
        newItem: NitrolessRepoEmoteModel
    ): Boolean {
        return oldItem.name == newItem.name && oldItem.type == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: NitrolessRepoEmoteModel,
        newItem: NitrolessRepoEmoteModel
    ): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

}