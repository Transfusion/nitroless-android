package io.github.transfusion.nitroless

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.adapters.EmoteCellAdapter
import io.github.transfusion.nitroless.databinding.FragmentSingleSourceBinding
import io.github.transfusion.nitroless.util.GridAutofitLayoutManager
import io.github.transfusion.nitroless.util.GridSpacingItemDecoration
import io.github.transfusion.nitroless.util.clearDecorations

class SingleSourceFragment : Fragment() {

    companion object {
        fun newInstance() = SingleSourceFragment()
    }

    /*private val viewModel: SingleSourceViewModel by viewModels {
        SingleSourceViewModelFactory((activity?.application as NitrolessApplication).repository)
    }*/

    private val viewModel: SingleSourceViewModel by viewModels {
        SingleSourceViewModelFactory(
            requireArguments().getInt("NitrolessRepoId"),
            (requireActivity().application as NitrolessApplication).repository
        )
    }

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


        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSingleSourceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val layoutManager = GridAutofitLayoutManager(requireContext(), 150)
        binding.emotesRecyclerView.layoutManager = layoutManager

        binding.emotesRecyclerView.clearDecorations()
        binding.emotesRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                layoutManager.spanCount,
                30,
                false,
                0,
                false
            )
        )

        viewModel.currentRepo.observe(viewLifecycleOwner) { repo ->
            Log.d(javaClass.name, "Observed!! $repo")
            binding.collapsingToolbarLayout.title = repo.name
            val emoteCellAdapter = EmoteCellAdapter(repo.url)
            subscribeEmoteCellAdapter(emoteCellAdapter)
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                LOADINGSTATUS.LOADING -> {
                    binding.progressBarVisible = true
                }
                else -> {
                    Log.d(javaClass.name, "Setting progress bar visible to FALSE")
                    binding.progressBarVisible = false
                }
            }
        }

//        return inflater.inflate(R.layout.fragment_single_source, container, false)
        return root
    }

    private fun subscribeEmoteCellAdapter(adapter: EmoteCellAdapter) {
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

}