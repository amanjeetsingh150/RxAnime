package com.developers.rxanime

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.developers.rxanime.model.OperatorCategory
import com.developers.rxanime.util.toPx
import com.developers.rxanime.viewpager.CardPagerAdapter
import com.developers.rxanime.viewpager.CardsPagerTransformer
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch


class RxAnimeActivity : AppCompatActivity() {

    private lateinit var cardPagerAdapter: CardPagerAdapter
    private lateinit var mainViewModel: RxAnimeViewModel

    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private val rxSharedPreference by lazy { RxSharedPreferences.create(sharedPreferences) }
    private val selectedCategory by lazy { rxSharedPreference.getString(RX_ANIME_PREFERENCES, OperatorCategory.FILTER.toString()) }

    private val disposable = CompositeDisposable()

    companion object {
        private const val CURRENT_SELECTION = "CURRENT_OPERATOR_CATEGORY"
        private const val RX_ANIME_PREFERENCES = "RX_ANIME_PREFS"
    }

    private val cardsPagerTransformer = CardsPagerTransformer(5, 10, 0.6f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this).get(RxAnimeViewModel::class.java)
        disposable += selectedCategory.asObservable()
                .map { mainViewModel.getOperators(OperatorCategory.valueOf(it)) }
                .subscribe({ operators ->
                    cardPagerAdapter = CardPagerAdapter()
                    cardPagerAdapter.addOperators(operators)
                    viewPager.adapter = cardPagerAdapter
                }, { showError(it) })

        disposable += RxViewPager.pageSelections(viewPager)
                .subscribe({ position ->
                    val currentSelection = sharedPreferences.getString(CURRENT_SELECTION, OperatorCategory.FILTER.toString())

                    when (OperatorCategory.valueOf(currentSelection!!)) {

                        OperatorCategory.FILTER -> {
                            val operatorList = mainViewModel.getOperators(OperatorCategory.FILTER)
                            val currentOperator = viewPager.findViewWithTag<BaseView>(operatorList[position].getOperatorName())
                            currentOperator?.let {
                                lifecycleScope.launch {
                                    currentOperator.restart()
                                }
                            }

                        }
                        OperatorCategory.TRANSFORM -> {

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
        Toast.makeText(this, "Error: " + it.message, Toast.LENGTH_SHORT).show()
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
                    putString(CURRENT_SELECTION, OperatorCategory.TRANSFORM.toString())
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
