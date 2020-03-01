package com.developers.rxanime.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.developers.rxanime.BaseView
import com.developers.rxanime.R
import com.developers.rxanime.model.Operators

class CardPagerAdapter : PagerAdapter() {

    private lateinit var operatorList: List<Operators>

    override fun isViewFromObject(view: View, anyObject: Any): Boolean {
        return view == anyObject
    }

    override fun getCount(): Int {
        return operatorList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.emmision_card_layout, container, false)
        container.addView(view)

        val cardView = view.findViewById<CardView>(R.id.cardView)
        val operatorTitleTextView = view.findViewById<TextView>(R.id.operatorName)
        val operatorHtmlLinkTextView = view.findViewById<TextView>(R.id.operatorHtmlLink)
        val operatorDescriptionTextView = view.findViewById<TextView>(R.id.operatorDescription)
        val streamContainerView = view.findViewById<LinearLayout>(R.id.streamViewContainer)

        val operator = operatorList[position]
        val operatorClass = operator.getView()
        val operatorView = operatorClass
                .getDeclaredConstructor(Context::class.java, AttributeSet::class.java)
                .newInstance(container.context, null) as BaseView
        operatorView.tag = operator.getOperatorName()
        operatorView.parent?.let { (it as ViewGroup).removeView(operatorView) }
        streamContainerView.addView(operatorView)
        cardView.maxCardElevation = MAX_ELEVATION
        operatorTitleTextView.text = operator.getOperatorName()
        operatorHtmlLinkTextView.text = container.context.getString(operator.getOperatorLink())
        operatorDescriptionTextView.text = container.context.getString(operator.getOperatorDescription())
        return view
    }

    override fun getItemPosition(anyObject: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun addOperators(operators: List<Operators>) {
        this.operatorList = operators
    }

    companion object {
        private const val MAX_ELEVATION_CONST = 8
        private const val BASE_ELEVATION = 4f
        private const val MAX_ELEVATION = BASE_ELEVATION * MAX_ELEVATION_CONST
    }

}