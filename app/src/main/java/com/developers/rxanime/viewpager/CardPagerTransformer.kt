package com.developers.rxanime.viewpager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import android.view.View


class CardsPagerTransformerBasic(private val baseElevation: Int, private val raisingElevation: Int, private val smallerScale: Float) : androidx.viewpager.widget.ViewPager.PageTransformer {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun transformPage(page: View, position: Float) {
        val absPosition = Math.abs(position)
        if (absPosition >= 1) {
            page.elevation = baseElevation.toFloat()
            page.scaleY = smallerScale
        } else {
            // This will be during transformation
            page.elevation = (1 - absPosition) * raisingElevation + baseElevation
            page.scaleY = (smallerScale - 1) * absPosition + 1
        }
    }
}