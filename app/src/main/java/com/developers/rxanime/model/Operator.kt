package com.developers.rxanime.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


enum class OperatorCategory {
    @Json(name = "Filtering")
    FILTER,
    @Json(name = "Transforming")
    TRANSFORMING
}

interface BaseOperator {
    fun getOperatorName(): String
}

enum class FilterOperator : BaseOperator {
    TAKE {
        override fun getOperatorName() = "Take(3)"
    },
    FILTER {
        override fun getOperatorName() = "Filter(even emissions)"
    },
    SKIP {
        override fun getOperatorName() = "Skip(3)"
    },
    TAKE_LAST {
        override fun getOperatorName() = "Take Last(2)"
    }
}

enum class Transforming : BaseOperator {
    MAP {
        override fun getOperatorName() = "Map(it->it*2)"
    },
    BUFFER {
        override fun getOperatorName() = "Buffer(2)"
    };
}

@JsonClass(generateAdapter = true)
data class Operator(@Json(name = "name") val name: String,
                    @Json(name = "description") val description: String = "",
                    @Json(name = "link") val operatorLink: String = "")

@JsonClass(generateAdapter = true)
data class Category(@Json(name = "name") val name: OperatorCategory,
                    @Json(name = "operators") val operators: List<Operator>)

@JsonClass(generateAdapter = true)
data class DisplayData(@Json(name = "data") val displayData: List<Category>)