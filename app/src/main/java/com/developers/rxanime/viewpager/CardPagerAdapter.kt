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
import com.developers.rxanime.model.FilterOperator
import com.developers.rxanime.operators.TakeOperatorView

class CardPagerAdapter : PagerAdapter() {


    private lateinit var filterOperatorList: List<FilterOperator>

    private var baseElevation = 4f
    private var dataList = mutableListOf<CardItem>()


    fun addItem(cardItem: CardItem) {
        dataList.add(cardItem)
    }

    fun cleatItems() {
        this.dataList.clear()
    }

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

        streamContainerView.addView(dataList[position].operatorVisualizer)
        cardView.maxCardElevation = baseElevation * MAX_ELEVATION
        operatorTitleTextView.text = dataList[position].operatorName
        operatorHtmlLinkTextView.text = dataList[position].operatorHtmlLink
        operatorDescriptionTextView.text = dataList[position].operatorDescription
        return view
    }

    override fun getItemPosition(anyObject: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    companion object {
        private const val MAX_ELEVATION = 8
    }

}