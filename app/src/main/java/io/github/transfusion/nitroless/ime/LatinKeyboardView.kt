/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.transfusion.nitroless.ime

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.inputmethodservice.Keyboard
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper
import io.github.transfusion.nitroless.ime.LatinKeyboardView

class LatinKeyboardView : KeyboardView {
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        Log.d(javaClass.name, "LatinKeyboardView constructor")
        Log.d(javaClass.name, "${(context as ContextThemeWrapper).baseContext.javaClass}")
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        Log.d(javaClass.name, "LatinKeyboardView constructor")
        Log.d(javaClass.name, "${(context as ContextThemeWrapper).baseContext.javaClass}")
        // class io.github.transfusion.nitroless.ime.NitrolessInputMethodService
    }

    override fun onLongPress(key: Keyboard.Key): Boolean {
        return if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            onKeyboardActionListener.onKey(KEYCODE_OPTIONS, intArrayOf())
            true
        } else {
            super.onLongPress(key)
        }
    }

    fun setSubtypeOnSpaceKey() {
        invalidateAllKeys()
    } /*@Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);

        List<Key> keys = getKeyboard().getKeys();
        for (Key key : keys) {
            if (key.label != null) {
                if (key.label.equals("q")) {
                    canvas.drawText("1", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("w")) {
                    canvas.drawText("2", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("e")) {
                    canvas.drawText("3", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("r")) {
                    canvas.drawText("4", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("t")) {
                    canvas.drawText("5", key.x + (key.width - 25), key.y + 40, paint);
                }
            }
        }
    }*/

    companion object {
        const val KEYCODE_OPTIONS = -100
        const val KEYCODE_LANGUAGE_SWITCH = -101
    }
}