package com.developers.rxanime.model

import com.developers.rxanime.BaseView
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


enum class OperatorCategory(name: String) {
    @Json(name = "Filtering") FILTER("Filter"),
    @Json(name = "Transforming") TRANSFORMING("Transforming")
}

@JsonClass(generateAdapter = true)
data class Operator(@Json(name = "name") val name: String,
                    @Json(name = "description") val description: String,
                    @Json(name = "link") val operatorLink: String)

@JsonClass(generateAdapter = true)
data class Category(@Json(name = "name") val name: OperatorCategory,
                    @Json(name = "operators") val operators: List<Operator>)

@JsonClass(generateAdapter = true)
data class DisplayData(@Json(name = "data") val displayData: List<Category>)