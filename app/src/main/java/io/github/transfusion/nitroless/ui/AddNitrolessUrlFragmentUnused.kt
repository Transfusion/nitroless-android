package io.github.transfusion.nitroless.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import io.github.transfusion.nitroless.databinding.FragmentAddNitrolessUrlDialogBinding

class AddNitrolessUrlFragmentUnused : DialogFragment() {

    //    private lateinit var sourcesViewModel: SourcesViewModel
//    private var _binding: FragmentAddNitrolessUrlDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_nitroless_url_dialog, container)
    }*/

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
//        sourcesViewModel =
//            ViewModelProvider(this).get(SourcesViewModel::class.java)
//        _binding = FragmentAddNitrolessUrlDialogBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//        return root
//    }


    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: Bundle? = arguments
//        val mEditText = view.findViewById<EditText>(R.id.nitroless_url)
        val title = args?.getString(ARG_TITLE)
        dialog?.setTitle(title)
//        val mEditText = binding.nitrolessUrl
//        mEditText.requestFocus()
//        dialog?.window?.setSoftInputMode(
//            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
//        );
    }

    companion object {
        const val ARG_TITLE = "Title"
    }
}