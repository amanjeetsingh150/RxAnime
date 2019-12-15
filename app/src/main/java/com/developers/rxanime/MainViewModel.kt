package com.developers.rxanime

import androidx.lifecycle.ViewModel
import com.developers.rxanime.model.DisplayData
import com.squareup.moshi.Moshi

class MainViewModel : ViewModel() {

    private val moshi = Moshi.Builder().build()
    private val adapter by lazy { moshi.adapter<DisplayData>(DisplayData::class.java) }

    fun fetchCategories(displayJson: String): DisplayData? {
        return adapter.fromJson(displayJson)
    }
}