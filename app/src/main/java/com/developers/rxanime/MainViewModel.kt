package com.developers.rxanime

import androidx.lifecycle.ViewModel
import com.developers.rxanime.model.DisplayData
import com.developers.rxanime.model.DisplayDataJsonAdapter
import com.developers.rxanime.model.OperatorCategory
import com.squareup.moshi.Moshi

class MainViewModel : ViewModel() {

    private val moshi = Moshi.Builder().build()
    private val adapter by lazy { moshi.adapter<DisplayData>(DisplayData::class.java) }

    fun fetchCategories(displayJson: String) {
        val displayData = adapter.fromJson(displayJson)
    }
}