package io.github.transfusion.nitroless.ui.sources

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import android.app.Activity
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.databinding.FragmentSourcesBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.AlertDialogFragment
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.adapters.NitrolessRepoAdapter
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.network.NitrolessRepoEndpoints
import io.github.transfusion.nitroless.network.ServiceBuilder
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.ui.AddNitrolessUrlFragment

import io.github.transfusion.nitroless.ui.YesNoDialogFragment
import io.github.transfusion.nitroless.viewholders.NitrolessRepoViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception


class SourcesFragment : Fragment() {

    //    private lateinit var sourcesViewModel: SourcesViewModel
    private val sourcesViewModel: SourcesViewModel by viewModels {
        SourcesViewModelFactory((activity?.application as NitrolessApplication).repository)
    }

    private var _binding: FragmentSourcesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var itemTouchHelper: CustomItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        sourcesViewModel = ViewModelProvider(this).get(SourcesViewModel::class.java)

        _binding = FragmentSourcesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textDashboard
        sourcesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        val nitrolessRepoAdapter = NitrolessRepoAdapter()
//        binding.sourcesRecyclerView.adapter = nitrolessRepoAdapter
        subscribeNitrolessRepoAdapter(nitrolessRepoAdapter)


        // todo: add swipe listeners here
        val itemTouchHelperCallback = ItemTouchHelperCallback()
//        itemTouchHelperCallback.setiMoveAndSwipeCallback(this)
        itemTouchHelper = CustomItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.sourcesRecyclerView)

        itemTouchHelperCallback.itemTouchHelper = itemTouchHelper

        return root
    }

    private fun subscribeNitrolessRepoAdapter(adapter: NitrolessRepoAdapter) {
        sourcesViewModel.repos.observe(viewLifecycleOwner) { repos ->
            Log.d("repos changed", repos.toString())
            adapter.submitList(repos)
        }
        binding.sourcesRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val toolbar = binding.toolbar
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.inflateMenu(R.menu.sources_toolbar_menu)
        toolbar.setOnMenuItemClickListener { it ->
            when (it.itemId) {
                R.id.action_add_repo -> {
                    addRepo();
                }
            }
            Log.d(javaClass.name, it.toString());
            false
        }
    }

    private fun addRepo() {
        val clipboard =
            context?.let { getSystemService(it, ClipboardManager::class.java) } as ClipboardManager

        // check if the clipboard contains a url
        var clipboardUrl: String? = null;
        if (clipboard.hasPrimaryClip()) {
            val primaryClip = clipboard.primaryClip
            if (primaryClip != null) {
                for (i in 0 until primaryClip.itemCount) {
                    val text = primaryClip.getItemAt(i).text.toString();
                    Log.d(javaClass.name, text);
                    if (URLUtil.isHttpsUrl(text))
                        clipboardUrl = text;
                    if (clipboardUrl != null) break
                }
            }
        } else {
            Log.d(javaClass.name, "no primary clip");
        }

        if (clipboardUrl != null) {
            val dialog: DialogFragment = YesNoDialogFragment()
            val args = Bundle()
            args.putString(YesNoDialogFragment.ARG_TITLE, getString(R.string.add_repo_dialog_title))
            args.putString(
                YesNoDialogFragment.ARG_MESSAGE,
                "Do you want to add: \n\n $clipboardUrl"
            )
            args.putString(YesNoDialogFragment.ARG_EXTRA_STRING, clipboardUrl)

            dialog.arguments = args
            dialog.setTargetFragment(this, YES_NO_CALL)
            fragmentManager?.let { dialog.show(it, "tag") }
        } else {
            showEnterRepoDialog();
        }
        Log.d(javaClass.name, clipboard.toString());
    }

    companion object {
        const val YES_NO_CALL = 1;
        const val ADD_NITROLESS_URL_CALL = 2;
    }

    private fun showEnterRepoDialog() {
        Log.d(javaClass.name, "showing enter repo dialog!");
        val dialog: DialogFragment = AddNitrolessUrlFragment()
        val args = Bundle()
        args.putString(AddNitrolessUrlFragment.ARG_TITLE, getString(R.string.add_repo_dialog_title))
        args.putString(AddNitrolessUrlFragment.ARG_MESSAGE, "Add Nitroless URL")
        dialog.arguments = args
        dialog.setTargetFragment(this, ADD_NITROLESS_URL_CALL)
        fragmentManager?.let { dialog.show(it, "tag") }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YES_NO_CALL)
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val url = data?.getStringExtra(YesNoDialogFragment.RESULT_EXTRA_STRING)
                    url?.let {
                        lifecycleScope.launch(Dispatchers.Main) {
                            addURL(it)
                        }
                    }
                }
                Activity.RESULT_CANCELED -> {
                    showEnterRepoDialog()
                }
            }
        else if (requestCode == ADD_NITROLESS_URL_CALL)
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val url = data?.getStringExtra(AddNitrolessUrlFragment.RESULT_URL)
                    url?.let {
                        lifecycleScope.launch(Dispatchers.Main) {
                            addURL(it)
                        }
                    }
                }
            }
    }

    private suspend fun addURL(url: String) {
        var indexResponse: NitrolessRepoModel? = null

        try {
            // validate repo url against json schema
            withContext(Dispatchers.IO) {
                val serviceBuilder = ServiceBuilder(url);
                val request = serviceBuilder.buildService(NitrolessRepoEndpoints::class.java)
                indexResponse = request.getIndex()
                Log.d(javaClass.name, indexResponse.toString())
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "invalid url", e);
        }

        if (indexResponse != null) {
            val name = indexResponse!!.name
            val newRepo = NitrolessRepo(name = name, url = url)
            Log.d(javaClass.name, "inserting $newRepo")
            withContext(Dispatchers.IO) {
                sourcesViewModel.insert(newRepo)
            }
            return
        }

        val dialog: DialogFragment = AlertDialogFragment()
        val args = Bundle()
        args.putString(AlertDialogFragment.ARG_TITLE, "Invalid URL")
        args.putString(
            AlertDialogFragment.ARG_MESSAGE,
            "URL must start with https:// and be a Nitroless repo."
        )
        dialog.arguments = args
        fragmentManager?.let { dialog.show(it, "tag") }
    }

}