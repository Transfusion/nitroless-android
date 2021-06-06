package io.github.transfusion.nitroless.viewholders

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import io.github.transfusion.nitroless.databinding.SourcesRowBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.ui.sources.SourcesFragmentDirections

@SuppressLint("ClickableViewAccessibility")
class NitrolessRepoViewHolder(val binding: SourcesRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var swipedOpen = false
    private var mDetector: GestureDetectorCompat

    private class MyGestureListener(val binding: SourcesRowBinding) :
        GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            binding.repo?.id?.let { id ->
                val direction =
                    SourcesFragmentDirections.actionNavigationSourcesToNavigationSingleSource(
                        id
                    )
                binding.actualSourceItem.findNavController().navigate(direction)
            }
            return super.onSingleTapConfirmed(e)
        }

    }


    init {
        binding.tvRemove.setOnClickListener {
            // TODO: Implement removing a source
        }

        mDetector =
            GestureDetectorCompat(binding.actualSourceItem.context, MyGestureListener(binding))
        binding.actualSourceItem.setOnTouchListener { v, event ->
            return@setOnTouchListener mDetector.onTouchEvent(event)
        }
    }

    fun bind(item: NitrolessRepo) {
        binding.apply {
            repo = item
            executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(
            view: ShapeableImageView,
            url: String?
        ) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (!url.isNullOrEmpty())
                Glide.with(view.context).load(url).into(view)
        }
    }


}