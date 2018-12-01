package com.developers.rxanime

import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.developers.rxanime.model.CardItem
import com.developers.rxanime.viewpager.CardPagerAdapter
import com.developers.rxanime.viewpager.CardsPagerTransformerBasic
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.emmision_card_layout.*


class MainActivity : AppCompatActivity() {

    private lateinit var cardPagerAdapter: CardPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cardPagerAdapter = CardPagerAdapter()

        cardPagerAdapter.addItem(CardItem(getString(R.string.take_operator), getString(R.string.take_operator_desc),
                getString(R.string.take_operator_link), StreamView(this)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.filter_operator), getString(R.string.filter_operator_desc),
                getString(R.string.filter_operator_link), StreamView(this)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.skip_operator), getString(R.string.skip_operator_desc),
                getString(R.string.skip_operator_link), StreamView(this)))

        viewPager.pageMargin = dpToPixels(5, this).toInt()
        viewPager.setPageTransformer(false, CardsPagerTransformerBasic(5, 10, 0.6f))
        viewPager.adapter = cardPagerAdapter
        viewPager.offscreenPageLimit = 1
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(position: Int) {
                //Do nothing
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //Do nothing
            }

            override fun onPageSelected(position: Int) {
                val operatorTitle = viewPager.getChildAt(position).findViewById<TextView>(R.id.operatorName)
                val streamView = viewPager.getChildAt(position).findViewById(R.id.streamView) as StreamView
                streamView.setShouldReset(false)
                when {
                    operatorTitle.text == getString(R.string.take_operator) -> {
                        streamView.setCanShowTakeOperatorAnimation(true)
                        streamView.setCanShowSkipOperatorAnimation(false)
                        streamView.setCanShowFilterOperatorAnimation(false)
                        streamView.init()
                    }
                    operatorTitle.text == getString(R.string.skip_operator) -> {
                        streamView.setCanShowSkipOperatorAnimation(true)
                        streamView.setCanShowFilterOperatorAnimation(false)
                        streamView.setCanShowTakeOperatorAnimation(false)
                        streamView.init()
                    }
                    operatorTitle.text == getString(R.string.filter_operator) -> {
                        streamView.setCanShowFilterOperatorAnimation(true)
                        streamView.setCanShowTakeOperatorAnimation(false)
                        streamView.setCanShowSkipOperatorAnimation(false)
                        streamView.init()
                    }
                }

            }
        })
    }

    private fun dpToPixels(dp: Int, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.transforming_operators -> {
                addTransFormingOperatorsToModel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addTransFormingOperatorsToModel() {
        cardPagerAdapter.addItem(CardItem(getString(R.string.map_operator), getString(R.string.map_operator_desc),
                getString(R.string.map_operator_link), streamView))
        cardPagerAdapter.addItem(CardItem(getString(R.string.buffer_operator), getString(R.string.buffer_operator_desc),
                getString(R.string.buffer_operator_link), streamView))
    }
}
