package com.developers.rxanime.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.developers.rxanime.R
import com.developers.rxanime.model.CardItem

class CardPagerAdapter : PagerAdapter() {

    private lateinit var dataList: List<CardItem>

    override fun isViewFromObject(view: View, anyObject: Any): Boolean {
        return view == anyObject
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.emmision_card_layout, container, false)
        container.addView(view)
        val cardView = view.findViewById<CardView>(R.id.cardView)

        val operatorTitleTextView = view.findViewById<TextView>(R.id.operatorName)
        val operatorHtmlLinkTextView = view.findViewById<TextView>(R.id.operatorHtmlLink)
        val operatorDescriptionTextView = view.findViewById<TextView>(R.id.operatorDescription)
        val streamContainerView = view.findViewById<LinearLayout>(R.id.streamViewContainer)

        val operatorView = dataList[position].operatorVisualizer
        operatorView?.parent?.let { (it as ViewGroup).removeView(operatorView) }
        streamContainerView.addView(operatorView)
        cardView.maxCardElevation = MAX_ELEVATION
        operatorTitleTextView.text = dataList[position].name
        operatorHtmlLinkTextView.text = dataList[position].htmlLink
        operatorDescriptionTextView.text = dataList[position].description
        return view
    }

    override fun getItemPosition(anyObject: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun addOperators(operators: List<CardItem>) {
        this.dataList = operators
    }

    companion object {
        private const val MAX_ELEVATION_CONST = 8
        private const val BASE_ELEVATION = 4f
        private const val MAX_ELEVATION = BASE_ELEVATION * MAX_ELEVATION_CONST
    }

}