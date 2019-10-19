package com.developers.rxanime.viewpager

import androidx.viewpager.widget.PagerAdapter
import androidx.cardview.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.developers.rxanime.R
import com.developers.rxanime.StreamView
import com.developers.rxanime.model.CardItem

class CardPagerAdapter : androidx.viewpager.widget.PagerAdapter() {


    private var baseElevation = 4f
    private var MAX_ELEVATION_FACTOR = 8
    private var dataList = mutableListOf<CardItem>()
    private lateinit var cardView: androidx.cardview.widget.CardView
    private lateinit var operatorTitleTextView: TextView
    private lateinit var operatorDescriptionTextView: TextView
    private lateinit var operatorHtmlLinkTextView: TextView


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
        cardView = view.findViewById(R.id.cardView)
        operatorTitleTextView = view.findViewById(R.id.operatorName)
        operatorHtmlLinkTextView = view.findViewById(R.id.operatorHtmlLink)
        operatorDescriptionTextView = view.findViewById(R.id.operatorDescription)
        if (position > 0) {
            view.findViewById<StreamView>(R.id.streamView).setShouldReset(true)
        }
        if (position == 0) {
            view.findViewById<StreamView>(R.id.streamView).init()
        }
        cardView.maxCardElevation = baseElevation * MAX_ELEVATION_FACTOR
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
}