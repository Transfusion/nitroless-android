package io.github.transfusion.nitroless.ime

import android.content.Context
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
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    private var _binding: KeyboardMainBinding? = null
    private val binding get() = _binding!!

    init {
        val _ctx = ContextThemeWrapper(ctx, R.style.Theme_Nitroless)
        _binding =
            DataBindingUtil.inflate(LayoutInflater.from(_ctx), R.layout.keyboard_main, this, true)

//        val kb = Keyboard(ctx, R.xml.kbd_qwerty)
//        binding.keyboardView.keyboard = kb
    }

}