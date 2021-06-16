package io.github.transfusion.nitroless.ime

import android.inputmethodservice.Keyboard
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.databinding.KeyboardMainBinding


class NitrolessMainKeyboardView @JvmOverloads
constructor(
    private val nitrolessInputMethodService: NitrolessInputMethodService,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(nitrolessInputMethodService, attributeSet, defStyleAttr) {

    private var _binding: KeyboardMainBinding? = null
    private val binding get() = _binding!!

    val inputView get() = binding.keyboardView

    init {
        val _ctx = ContextThemeWrapper(nitrolessInputMethodService, R.style.Theme_Nitroless)
        _binding =
            DataBindingUtil.inflate(LayoutInflater.from(_ctx), R.layout.keyboard_main, this, true)

        val kb = Keyboard(nitrolessInputMethodService, R.xml.qwerty)
        binding.keyboardView.keyboard = kb

//        binding.keyboardView.setOnKeyboardActionListener(nitrolessInputMethodService)
    }

}