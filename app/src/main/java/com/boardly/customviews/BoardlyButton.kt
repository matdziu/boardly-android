package com.boardly.customviews

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button
import com.boardly.R

class BoardlyButton(context: Context, attrs: AttributeSet? = null) : Button(context, attrs) {

    init {
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        background = ContextCompat.getDrawable(context, R.drawable.blue_background_selector)
    }
}