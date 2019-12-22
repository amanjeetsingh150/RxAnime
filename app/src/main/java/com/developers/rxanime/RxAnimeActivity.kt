package com.developers.rxanime

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.developers.rxanime.model.CardItem
import com.developers.rxanime.model.FilterOperator
import com.developers.rxanime.model.OperatorCategory
import com.developers.rxanime.model.Transforming
import com.developers.rxanime.operators.TakeOperatorView
import com.developers.rxanime.util.getOperator
import com.developers.rxanime.util.toPx
import com.developers.rxanime.viewpager.CardPagerAdapter
import com.developers.rxanime.viewpager.CardsPagerTransformerBasic
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.nio.charset.Charset


class RxAnimeActivity : AppCompatActivity() {

    private lateinit var cardPagerAdapter: CardPagerAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainViewModel: RxAnimeViewModel

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
        mainViewModel = ViewModelProviders.of(this).get(RxAnimeViewModel::class.java)
        val displayData = mainViewModel.fetchCategories(loadJSONFromAsset())
        val categoryList = displayData?.displayData

        disposable += RxViewPager.pageSelections(viewPager)
                .subscribe({ position ->
                    // get current selection and index
                    val currentSelection = sharedPreferences.getString(CURRENT_SELECTION, OperatorCategory.FILTER.toString())
                    val currentCategoryIndex = OperatorCategory.valueOf(currentSelection!!).ordinal

                    // get current category and its operators
                    val currentCategory = categoryList?.get(currentCategoryIndex)
                    val operators = currentCategory?.operators

                    when (OperatorCategory.valueOf(currentSelection)) {

                        OperatorCategory.FILTER -> {
                            val selectedOperatorList = operators?.let {
                                val currentOperatorName = it[position].name.getOperator<FilterOperator>()

                            }

                        }
                        OperatorCategory.TRANSFORMING -> {
                            val selectedOperatorList = operators?.let {
                                val currentOperatorName = it[position].name.getOperator<Transforming>()
                            }
                        }
                    }
                }, {
                    // Some error occurred
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })

        cardPagerAdapter = CardPagerAdapter()
        setupInitialOperators(cardPagerAdapter)
        with(viewPager) {
            pageMargin = 5.toPx()
            setPageTransformer(false, CardsPagerTransformerBasic(5, 10, 0.6f))
            adapter = cardPagerAdapter
            offscreenPageLimit = 1
        }
    }

    private fun setupInitialOperators(cardPagerAdapter: CardPagerAdapter) {
        cardPagerAdapter.addItem(CardItem(getString(R.string.take_operator), getString(R.string.take_operator_desc),
                getString(R.string.take_operator_link), TakeOperatorView(this, null)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.take_last_operator), getString(R.string.take_last_operator_desc),
                getString(R.string.take_last_operator_link), TakeOperatorView(this, null)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.filter_operator), getString(R.string.filter_operator_desc),
                getString(R.string.filter_operator_link), TakeOperatorView(this, null)))
        cardPagerAdapter.addItem(CardItem(getString(R.string.skip_operator), getString(R.string.skip_operator_desc),
                getString(R.string.skip_operator_link), TakeOperatorView(this, null)))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.transforming_operators -> {
                sharedPreferences.edit().apply {
                    putString(CURRENT_SELECTION, OperatorCategory.TRANSFORMING.toString())
                    apply()
                }
                cardPagerAdapter.cleatItems()
                true
            }
            R.id.filtering_operators -> {
                sharedPreferences.edit().apply {
                    putString(CURRENT_SELECTION, OperatorCategory.FILTER.toString())
                    apply()
                }
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
}
