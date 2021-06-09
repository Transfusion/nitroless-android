package io.github.transfusion.nitroless

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.github.transfusion.nitroless.adapters.SingleSourceAdapter
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.FragmentSingleSourceBinding
import io.github.transfusion.nitroless.enums.LOADINGSTATUS

class SingleSourceFragment : Fragment(),
    SearchView.OnQueryTextListener {

    companion object {
        fun newInstance() = SingleSourceFragment()
    }

    /*private val viewModel: SingleSourceViewModel by viewModels {
        SingleSourceViewModelFactory((activity?.application as NitrolessApplication).repository)
    }*/

    private var mSearchQuery: String? = null
    private val viewModel: SingleSourceViewModel by viewModels {
        SingleSourceViewModelFactory(
            requireArguments().getInt("NitrolessRepoId"),
            (requireActivity().application as NitrolessApplication).repository
        )
    }

    private var singleSourceAdapter: SingleSourceAdapter? = null

    private var _binding: FragmentSingleSourceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(javaClass.name, arguments.toString())
        arguments?.getInt("NitrolessRepoId")?.let {
            Log.d(javaClass.name, it.toString())
//            viewModel
        }

        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString("searchQuery");
        }


        super.onCreate(savedInstanceState)
    }

    private fun onEmoteClicked(emoteModel: NitrolessRepoEmoteModel) {
        Log.d(javaClass.name, "clicked on ${emoteModel.name}")
        val mySnackbar =
            Snackbar.make(
                binding.singleSourceCoordinatorLayout,
                "Copied ${emoteModel.name}",
                Snackbar.LENGTH_SHORT
            ).setAction("OK") {
                // Responds to click on the action
            }
        // https://stackoverflow.com/questions/31746300/how-to-show-snackbar-at-top-of-the-screen/36768267
        // not material-spec compliant but better than obscuring the
        // bottom row
        val view: View = mySnackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.width = CoordinatorLayout.LayoutParams.WRAP_CONTENT
        view.layoutParams = params

        mySnackbar.show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSingleSourceBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        (binding.emotesRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
//            false
        // https://stackoverflow.com/questions/42379660/how-to-prevent-recyclerview-item-from-blinking-after-notifyitemchangedpos
        binding.emotesRecyclerView.itemAnimator = null

        val screenWidth = requireContext().resources.displayMetrics.widthPixels

        val emoteCellSide = resources.getDimension(R.dimen.emote_cell_side)
        val emoteCellPadding = resources.getDimension(R.dimen.emote_cell_padding)
        val totalEmoteCellWidth = emoteCellSide + emoteCellPadding * 2
        Log.d(javaClass.name, "width of screen $screenWidth")
        Log.d(javaClass.name, "side of emote cell $emoteCellSide")
        val noOfSpans = (screenWidth / totalEmoteCellWidth).toInt()
        Log.d(javaClass.name, "calculated spans $noOfSpans")


        val layoutManager = GridLayoutManager(requireContext(), noOfSpans)

//        val layoutManager = GridAutofitLayoutManager(requireContext(), 150)
        binding.emotesRecyclerView.layoutManager = layoutManager
        /*binding.emotesRecyclerView.clearDecorations()
        binding.emotesRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                layoutManager.spanCount,
                30,
                false,
                0,
                false
            )
        )*/

        viewModel.currentRepo.observe(viewLifecycleOwner) { repo ->
            binding.collapsingToolbarLayout.title = repo.name
            singleSourceAdapter = SingleSourceAdapter({ emoteModel: NitrolessRepoEmoteModel ->
                onEmoteClicked(emoteModel)
            }, repo.url)
            subscribeSingleSourceAdapter(singleSourceAdapter!!)

            if (mSearchQuery != null) {
                this.onQueryTextChange(mSearchQuery)
            }
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                LOADINGSTATUS.LOADING -> {
                    binding.progressBarVisible = true
                }
                LOADINGSTATUS.FAILED -> {
                    binding.progressBarVisible = false
                    binding.errorMessageVisible = true
                }
                else -> {
                    Log.d(javaClass.name, "Setting progress bar visible to FALSE")
                    binding.progressBarVisible = false
                }
            }
        }

        // bind SearchView
        binding.emoteSearch.setOnQueryTextListener(this)

//        return inflater.inflate(R.layout.fragment_single_source, container, false)
        return root
    }

    private fun subscribeSingleSourceAdapter(adapter: SingleSourceAdapter) {
        viewModel.emotes.observe(viewLifecycleOwner) {
            Log.d("emotes changed", it.emotes.toString())
            adapter.path = it.path
            adapter.submitList(it.emotes)
        }
        binding.emotesRecyclerView.adapter = adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_sources
//                , R.id.navigation_notifications
            )
        )
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
//        toolbar.inflateMenu(R.menu.single_source_toolbar_menu)
        /*toolbar.inflateMenu(R.menu.sources_toolbar_menu)
        toolbar.setOnMenuItemClickListener { it ->
            when (it.itemId) {
                R.id.action_add_repo -> {
                    addRepo();
                }
            }
            Log.d(javaClass.name, it.toString());
            false
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("searchQuery", mSearchQuery);
        super.onSaveInstanceState(outState)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        this.onQueryTextChange(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        mSearchQuery = newText
        singleSourceAdapter?.filter?.filter(newText);
        return false
    }


}