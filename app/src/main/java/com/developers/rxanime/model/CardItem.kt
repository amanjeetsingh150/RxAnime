package com.developers.rxanime.model

import android.view.View

data class CardItem(val name: String, val description: String,
                    val htmlLink: String, val operatorVisualizer: View? = null,
                    val operatorCategory: OperatorCategory)