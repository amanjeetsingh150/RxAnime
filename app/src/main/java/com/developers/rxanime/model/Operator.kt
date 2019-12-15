package com.developers.rxanime.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


enum class OperatorCategory {
    @Json(name = "Filtering")
    FILTER{
        override fun getOperators(): Operator {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    @Json(name = "Transforming")
    TRANSFORMING{
        override fun getOperators(): Operator {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    };

    abstract fun getOperators(): Operator
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