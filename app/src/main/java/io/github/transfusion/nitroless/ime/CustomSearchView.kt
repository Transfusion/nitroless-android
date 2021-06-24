package io.github.transfusion.nitroless.ime

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.SearchView

class CustomSearchView : SearchView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) :
            super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    fun getSearchAutoComplete(): SearchAutoComplete {
        return findViewById(R.id.search_src_text)
    }
}