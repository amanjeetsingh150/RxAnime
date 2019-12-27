package com.developers.rxanime

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.developers.rxanime.model.CardItem
import com.developers.rxanime.model.FilterOperator
import com.developers.rxanime.model.OperatorCategory
import com.developers.rxanime.model.Transforming
import com.developers.rxanime.util.getOperator
import com.developers.rxanime.util.toPx
import com.developers.rxanime.viewpager.CardPagerAdapter
import com.developers.rxanime.viewpager.CardsPagerTransformer
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.nio.charset.Charset


class RxAnimeActivity : AppCompatActivity() {

    private lateinit var cardPagerAdapter: CardPagerAdapter

    private val sharedPreferences by lazy {
         PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val rxSharedPreference by lazy { RxSharedPreferences.create(sharedPreferences) }
    private val selectedCategory by lazy {
        rxSharedPreference.getString(RX_ANIME_PREFERENCES, OperatorCategory.FILTER.toString())
    }
    private lateinit var operatorViewInitializer: OperatorViewInitializer
    private lateinit var mainViewModel: RxAnimeViewModel

    private val disposable = CompositeDisposable()

    companion object {
        const val RX_PREFERENCE_NAME = "RX_PREFERENCES"
        const val FILTER_OPERATOR_SHOW = "FILTER_OPERATORS"
        const val TRANSFORMING_OPERATOR_SHOW = "TRANSFORM_OPERATORS"
        private const val CURRENT_SELECTION = "CURRENT_OPERATOR_CATEGORY"
        private const val RX_ANIME_PREFERENCES = "RX_ANIME_PREFS"
    }

    private val cardsPagerTransformer = CardsPagerTransformer(5, 10, 0.6f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this).get(RxAnimeViewModel::class.java)
        val displayData = mainViewModel.fetchCategories(loadJSONFromAsset())
        val categoryList = displayData?.displayData?.apply {
            operatorViewInitializer = OperatorViewInitializer(this, this@RxAnimeActivity)
        }
        disposable += selectedCategory.asObservable()
                .map {
                    val initialCardItems = mainViewModel.fetchCurrentOperators(categoryList = categoryList)
                    when (OperatorCategory.valueOf(it)) {
                        OperatorCategory.FILTER -> {
                            val filterViews = operatorViewInitializer.fetchFilterViews()
                            return@map initialCardItems.asSequence()
                                    .filter { operator -> operator.operatorCategory == OperatorCategory.FILTER }
                                    .mapIndexed { position, operator ->
                                        CardItem(name = operator.name,
                                                description = operator.description,
                                                htmlLink = operator.htmlLink,
                                                operatorVisualizer = filterViews[position],
                                                operatorCategory = OperatorCategory.FILTER)
                                    }.toList()
                        }
                        OperatorCategory.TRANSFORMING -> {
                            val transformingViews = operatorViewInitializer.fetchTransformingViews()
                            return@map initialCardItems.asSequence()
                                    .filter { operator -> operator.operatorCategory == OperatorCategory.FILTER }
                                    .mapIndexed { position, operator ->
                                        CardItem(name = operator.name,
                                                description = operator.description,
                                                htmlLink = operator.htmlLink,
                                                operatorVisualizer = transformingViews[position],
                                                operatorCategory = OperatorCategory.TRANSFORMING)
                                    }.toList()
                        }
                    }
                }
                .subscribe({ operators ->
                    Log.d("RxAnime", "onSuccess ")
                    cardPagerAdapter = CardPagerAdapter()
                    cardPagerAdapter.addOperators(operators)
                    viewPager.adapter = cardPagerAdapter
                }, {
                    Log.d("RxAnime", "onError ${it.printStackTrace()}")
                    showError(it)
                })

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
                            operators?.let {
                                val currentOperatorName = it[position].name.getOperator<FilterOperator>()
                            }

                        }
                        OperatorCategory.TRANSFORMING -> {
                            operators?.let {
                                val currentOperatorName = it[position].name.getOperator<Transforming>()
                            }
                        }
                    }
                }, { showError(it) })

        with(viewPager) {
            pageMargin = 5.toPx()
            setPageTransformer(false, cardsPagerTransformer)
            offscreenPageLimit = 1
        }
    }

    /**
     * Shows error in form of a toast.
     *
     * @param it Throwable to get the error message.
     */
    private fun showError(it: Throwable) {
        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.transforming_operators -> {
                sharedPreferences.edit().apply {
                    putString(CURRENT_SELECTION, OperatorCategory.TRANSFORMING.toString())
                    apply()
                }
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
}
