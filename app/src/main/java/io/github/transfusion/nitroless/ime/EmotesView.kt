package io.github.transfusion.nitroless.ime

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.adapters.HomeFragmentAdapter
import io.github.transfusion.nitroless.adapters.RecentlyUsedEmotesAdapter
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.KeyboardEmotesViewBinding
import io.github.transfusion.nitroless.enums.LOADINGSTATUS
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.ui.home.*

/**
 * Encapsulates everything to do with emojis
 * Currently only contains the RecyclerView as a POC
 * Could include horizontal ViewPagers, in-IME settings, etc. in the future
 */
class EmotesView : ConstraintLayout {

    private lateinit var _homeFragmentAdapter: HomeFragmentAdapter
    public val homeFragmentAdapter get() = _homeFragmentAdapter
    private lateinit var recentlyUsedEmotesAdapter: RecentlyUsedEmotesAdapter
    private lateinit var _concatAdapter: ConcatAdapter
    public val concatAdapter get() = _concatAdapter

    constructor(ctx: Context) : super(ctx) {
        performInflate(ctx)
    }

    constructor(ctx: Context, attributeSet: AttributeSet?) : super(ctx, attributeSet) {
        performInflate(ctx)
    }

    constructor(
        ctx: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(ctx, attributeSet, defStyleAttr) {
        performInflate(ctx)
    }


    private var _binding: KeyboardEmotesViewBinding? = null
    val binding get() = _binding!!

    private fun performInflate(ctx: Context) {
        _binding = DataBindingUtil.inflate(
            LayoutInflater.from(ctx),
            R.layout.keyboard_emotes_view,
            this,
            true
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun setStatus(status: LOADINGSTATUS) {
        when (status) {
            LOADINGSTATUS.LOADING -> {
                binding.progressBarVisible = true
            }
            else -> {
                binding.progressBarVisible = false
            }
        }
    }

    fun initialize(
        nitrolessInputMethodService: NitrolessInputMethodService
    ) {
        _homeFragmentAdapter =
            HomeFragmentAdapter { nitrolessRepo: NitrolessRepo, path: String, emote: NitrolessRepoEmoteModel ->
                nitrolessInputMethodService.onEmoteClicked(
                    nitrolessRepo,
                    path,
                    emote,
//                    nitrolessMainKeyboardView,
//                    context
                )
                // insert into recently used
                /*val recentlyUsedEmote = RecentlyUsedEmote(
                    repoId = nitrolessRepo.id,
                    emote_path = path,
                    emote_name = emote.name,
                    emote_type = emote.type,
                    emote_used = Date()
                )
                homeViewModel.insertRecentlyUsed(recentlyUsedEmote)*/
            }

        recentlyUsedEmotesAdapter =
            RecentlyUsedEmotesAdapter { nitrolessRepo: NitrolessRepo, path: String, emote: NitrolessRepoEmoteModel ->
                nitrolessInputMethodService.onEmoteClicked(
                    nitrolessRepo,
                    path,
                    emote
//                    binding.homeCoordinatorLayout,
//                    requireContext()
                )
                // insert into recently used
                /*val recentlyUsedEmote = RecentlyUsedEmote(
                    repoId = nitrolessRepo.id,
                    emote_path = path,
                    emote_name = emote.name,
                    emote_type = emote.type,
                    emote_used = Date()
                )
                homeViewModel.insertRecentlyUsed(recentlyUsedEmote)*/
            }


        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false).setStableIdMode(ConcatAdapter.Config.StableIdMode.NO_STABLE_IDS)
        }.build()

        _concatAdapter = ConcatAdapter(config, recentlyUsedEmotesAdapter, homeFragmentAdapter)

        val screenWidth = nitrolessInputMethodService.resources.displayMetrics.widthPixels
        val emoteCellSide = resources.getDimension(R.dimen.emote_cell_side)
        val emoteCellPadding = resources.getDimension(R.dimen.emote_cell_padding)
        val totalEmoteCellWidth = emoteCellSide + emoteCellPadding * 2
        Log.d(javaClass.name, "width of screen $screenWidth")
        Log.d(javaClass.name, "side of emote cell $emoteCellSide")
        val noOfSpans = (screenWidth / totalEmoteCellWidth).toInt()
        Log.d(javaClass.name, "calculated spans $noOfSpans")


        val gridLayoutManager = GridLayoutManager(nitrolessInputMethodService, noOfSpans)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getItemViewType(position)) {
                    ITEM_VIEW_TYPE_EMOTE_ITEM -> 1
                    ITEM_VIEW_TYPE_RECENT_EMOTE_ITEM -> 1
                    ITEM_VIEW_TYPE_MESSAGE_ITEM -> noOfSpans
                    ITEM_VIEW_TYPE_HEADER -> noOfSpans
                    ITEM_VIEW_TYPE_RECENT_HEADER -> noOfSpans
                    else -> -1
                }
            }
        }
        binding.homeRecyclerView.layoutManager = gridLayoutManager
        subscribeHomeFragmentAdapter(nitrolessInputMethodService, homeFragmentAdapter)
        subscribeRecentlyUsedEmotesAdapter(nitrolessInputMethodService, recentlyUsedEmotesAdapter)
        binding.homeRecyclerView.adapter = concatAdapter
    }

    private fun subscribeHomeFragmentAdapter(
        nitrolessInputMethodService: NitrolessInputMethodService,
        adapter: HomeFragmentAdapter
    ) {
        nitrolessInputMethodService.homeViewModel.nitrolessRepoAndModels.observe(
            nitrolessInputMethodService
        ) {
            adapter.massageDataAndSubmitList(it)
        }
    }

    private fun subscribeRecentlyUsedEmotesAdapter(
        nitrolessInputMethodService: NitrolessInputMethodService,
        adapter: RecentlyUsedEmotesAdapter
    ) {
        nitrolessInputMethodService.homeViewModel.recentlyUsedEmoteAndRepos.observe(
            nitrolessInputMethodService
        ) {
            adapter.massageDataAndSubmitList(it)
        }
    }

}

