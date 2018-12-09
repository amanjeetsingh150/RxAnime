package com.developers.rxanime

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        val RX_PREFERENCE_NAME = "RX_PREFERENCES"
        val FILTER_OPERATOR_SHOW = "FILTER_OPERATORS"
        val TRANSFORMING_OPERATOR_SHOW = "TRANSFORM_OPERATORS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPreferences()
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
                        streamView.setShouldShowFilterOperators(true)
                        streamView.setCanShowTakeOperatorAnimation(true)
                        streamView.setCanShowSkipOperatorAnimation(false)
                        streamView.setCanShowFilterOperatorAnimation(false)
                        streamView.init()
                    }
                    operatorTitle.text == getString(R.string.skip_operator) -> {
                        streamView.setShouldShowFilterOperators(true)
                        streamView.setCanShowSkipOperatorAnimation(true)
                        streamView.setCanShowFilterOperatorAnimation(false)
                        streamView.setCanShowTakeOperatorAnimation(false)
                        streamView.init()
                    }
                    operatorTitle.text == getString(R.string.filter_operator) -> {
                        streamView.setShouldShowFilterOperators(true)
                        streamView.setCanShowFilterOperatorAnimation(true)
                        streamView.setCanShowTakeOperatorAnimation(false)
                        streamView.setCanShowSkipOperatorAnimation(false)
                        streamView.init()
                    }
                    operatorTitle.text == getString(R.string.map_operator) -> {
                        streamView.setShouldShowTransformingOperators(true)
                        streamView.setCanShowMapOperatorAnimation(true)
                        streamView.setCanShowFilterOperatorAnimation(false)
                        streamView.setCanShowTakeOperatorAnimation(false)
                        streamView.setCanShowSkipOperatorAnimation(false)
                        streamView.setCanShowBufferOperatorAnimation(false)
                        streamView.init()
                    }
                    operatorTitle.text == getString(R.string.buffer_operator) -> {
                        streamView.setShouldShowTransformingOperators(true)
                        streamView.setCanShowBufferOperatorAnimation(true)
                        streamView.setCanShowMapOperatorAnimation(false)
                        streamView.setCanShowFilterOperatorAnimation(false)
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

    private fun setupPreferences() {
        sharedPreferences = getSharedPreferences(RX_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(FILTER_OPERATOR_SHOW, true)
        editor.putBoolean(TRANSFORMING_OPERATOR_SHOW, false)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.transforming_operators -> {
                changePreferences(false, true)
                cardPagerAdapter.cleatItems()
                streamView.setCanShowMapOperatorAnimation(true)
                streamView.setCanShowFilterOperatorAnimation(false)
                streamView.setCanShowTakeOperatorAnimation(false)
                streamView.setCanShowSkipOperatorAnimation(false)
                addTransFormingOperatorsToModel()
                true
            }
            R.id.filtering_operators -> {
                changePreferences(true, false)
                cardPagerAdapter.cleatItems()
                streamView.setCanShowBufferOperatorAnimation(false)
                streamView.setCanShowMapOperatorAnimation(false)
                streamView.setCanShowFilterOperatorAnimation(false)
                streamView.setCanShowTakeOperatorAnimation(true)
                streamView.setCanShowSkipOperatorAnimation(false)
                addFilteringOperatorsToModel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addFilteringOperatorsToModel() {
        cardPagerAdapter.addItem(CardItem(getString(R.string.take_operator), getString(R.string.take_operator_desc),
                getString(R.string.take_operator_link), StreamView(this)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.filter_operator), getString(R.string.filter_operator_desc),
                getString(R.string.filter_operator_link), StreamView(this)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.skip_operator), getString(R.string.skip_operator_desc),
                getString(R.string.skip_operator_link), StreamView(this)))
        cardPagerAdapter.notifyDataSetChanged()
        viewPager.currentItem = 0
    }

    private fun changePreferences(showFilterOperators: Boolean, showTransformingOperators: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(FILTER_OPERATOR_SHOW, showFilterOperators)
        editor.putBoolean(TRANSFORMING_OPERATOR_SHOW, showTransformingOperators)
        editor.apply()
    }

    private fun addTransFormingOperatorsToModel() {
        cardPagerAdapter.addItem(CardItem(getString(R.string.map_operator), getString(R.string.map_operator_desc),
                getString(R.string.map_operator_link), streamView))
        cardPagerAdapter.addItem(CardItem(getString(R.string.buffer_operator), getString(R.string.buffer_operator_desc),
                getString(R.string.buffer_operator_link), streamView))
        cardPagerAdapter.notifyDataSetChanged()
        viewPager.currentItem = 0
    }
}
