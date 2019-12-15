package com.developers.rxanime

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.developers.rxanime.model.CardItem
import com.developers.rxanime.model.OperatorCategory
import com.developers.rxanime.viewpager.CardPagerAdapter
import com.developers.rxanime.viewpager.CardsPagerTransformerBasic
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.emmision_card_layout.*
import java.io.IOException
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    private lateinit var cardPagerAdapter: CardPagerAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainViewModel: MainViewModel

    private val disposable = CompositeDisposable()

    companion object {
        const val RX_PREFERENCE_NAME = "RX_PREFERENCES"
        const val FILTER_OPERATOR_SHOW = "FILTER_OPERATORS"
        const val TRANSFORMING_OPERATOR_SHOW = "TRANSFORM_OPERATORS"
        private const val CURRENT_SELECTION = "CURRENT_OPERATOR_CATEGORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val displayData = mainViewModel.fetchCategories(loadJSONFromAsset())
        val categoryList = displayData?.displayData

        disposable += RxViewPager.pageSelections(viewPager)
                .subscribe({ position ->
                    // get current selection
                }, {})


        setupPreferences()
        cardPagerAdapter = CardPagerAdapter()
        setupInitialOperators(cardPagerAdapter)
        with(viewPager) {
            pageMargin = dpToPixels(5, this@MainActivity).toInt()
            setPageTransformer(false, CardsPagerTransformerBasic(5, 10, 0.6f))
            adapter = cardPagerAdapter
            offscreenPageLimit = 1
        }
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
                        toggleOperatorsOnView(streamView, false, true, false,
                                false, false, false)
                    }
                    operatorTitle.text == getString(R.string.take_last_operator) -> {
                        toggleOperatorsOnView(streamView, false, false, true,
                                false, false, false)
                    }
                    operatorTitle.text == getString(R.string.skip_operator) -> {
                        toggleOperatorsOnView(streamView, false, false, false,
                                true, false, false)
                    }
                    operatorTitle.text == getString(R.string.filter_operator) -> {
                        toggleOperatorsOnView(streamView, true, false, false,
                                false, false, false)
                    }
                    operatorTitle.text == getString(R.string.map_operator) -> {
                        toggleOperatorsOnView(streamView, false, false, false,
                                false, true, false)
                    }
                    operatorTitle.text == getString(R.string.buffer_operator) -> {
                        toggleOperatorsOnView(streamView, false, false, false,
                                false, false, true)
                    }
                }

            }
        })
    }

    private fun setupInitialOperators(cardPagerAdapter: CardPagerAdapter) {
        cardPagerAdapter.addItem(CardItem(getString(R.string.take_operator), getString(R.string.take_operator_desc),
                getString(R.string.take_operator_link), StreamView(this)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.take_last_operator), getString(R.string.take_last_operator_desc),
                getString(R.string.take_last_operator_link), StreamView(this)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.filter_operator), getString(R.string.filter_operator_desc),
                getString(R.string.filter_operator_link), StreamView(this)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.skip_operator), getString(R.string.skip_operator_desc),
                getString(R.string.skip_operator_link), StreamView(this)))
    }

    private fun toggleOperatorsOnView(streamView: StreamView, shouldShowFilter: Boolean, shouldShowTake: Boolean, shouldShowTakeLast: Boolean,
                                      shouldShowSkip: Boolean, shouldShowMap: Boolean, shouldShowBuffer: Boolean) {
        with(streamView) {
            setCanShowTakeOperatorAnimation(shouldShowTake)
            setCanShowTakeLastOperatorAnimation(shouldShowTakeLast)
            setCanShowFilterOperatorAnimation(shouldShowFilter)
            setCanShowSkipOperatorAnimation(shouldShowSkip)
            setCanShowMapOperatorAnimation(shouldShowMap)
            setCanShowBufferOperatorAnimation(shouldShowBuffer)
            init()
        }
    }

    private fun loadJSONFromAsset(): String {
        val json: String?
        try {
            val displayJsonStream = assets.open("display_operators.json")
            val size = displayJsonStream.available()
            val buffer = ByteArray(size)
            displayJsonStream.read(buffer)
            displayJsonStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }

        return json
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
                val editor = sharedPreferences.edit()
                editor.putString(CURRENT_SELECTION, OperatorCategory.TRANSFORMING.toString())
                changePreferences(false, true)
                cardPagerAdapter.cleatItems()
                toggleOperatorsOnView(streamView, false, false, false,
                        false, true, false)
                addTransFormingOperatorsToModel()
                true
            }
            R.id.filtering_operators -> {
                changePreferences(true, false)
                cardPagerAdapter.cleatItems()
                toggleOperatorsOnView(streamView, false, true, false,
                        false, false, false)
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
