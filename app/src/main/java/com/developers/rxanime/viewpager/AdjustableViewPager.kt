package com.developers.rxanime.viewpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

class AdjustableViewPager(context: Context, attributeSet: AttributeSet?) : ViewPager(context, attributeSet) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpecNew = heightMeasureSpec
        val mode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (mode == View.MeasureSpec.UNSPECIFIED || mode == View.MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            var height = 0
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                val h = child.measuredHeight
                if (h > height) height = h
            }
            heightMeasureSpecNew = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        } else {
            // do nothing
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpecNew)
        setMeasuredDimension(getWidthFromDisplayMetrics(), getHeightFromDisplayMetrics() - getDimensionInPixel(20))
    }

    private fun getHeightFromDisplayMetrics(): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    private fun getWidthFromDisplayMetrics(): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    private fun getDimensionInPixel(dp: Int): Int {
        val density = resources.displayMetrics.densityDpi

        var modifieddp = dp
        when (density) {
            DisplayMetrics.DENSITY_LOW -> modifieddp = dp - dp / 2
            DisplayMetrics.DENSITY_MEDIUM -> modifieddp = dp - dp / 3
            DisplayMetrics.DENSITY_HIGH -> modifieddp = dp - dp / 4
            DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XXXHIGH -> modifieddp = dp
            else -> {
            }
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, modifieddp.toFloat(), resources.displayMetrics).toInt()
    }

}