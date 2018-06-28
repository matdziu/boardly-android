package com.boardly.customviews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.EditText
import com.boardly.R

class BoardlyEditText(context: Context, attrs: AttributeSet? = null) : EditText(context, attrs) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.round_empty_blue_background)
        setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText))
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        val paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
    }

    fun showError(show: Boolean) {
        background = if (show) {
            ContextCompat.getDrawable(context, R.drawable.round_empty_red_background)
        } else {
            ContextCompat.getDrawable(context, R.drawable.round_empty_blue_background)
        }
    }
}