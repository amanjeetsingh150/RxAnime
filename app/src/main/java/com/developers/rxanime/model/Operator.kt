package com.developers.rxanime.model

import com.developers.rxanime.R
import com.developers.rxanime.operators.filter.FilterOperatorView
import com.developers.rxanime.operators.filter.SkipOperatorView
import com.developers.rxanime.operators.filter.TakeLastOperator
import com.developers.rxanime.operators.filter.TakeOperatorView

enum class OperatorCategory {
    FILTER,
    TRANSFORM
}

interface Operators {
    fun getOperatorName(): String
    fun getOperatorDescription(): Int
    fun getOperatorLink(): Int
    fun getView(): Class<*>
}

enum class FilterOperator : Operators {
    TAKE {
        override fun getOperatorName() = "Take(3)"
        override fun getOperatorDescription() = R.string.take_operator_desc
        override fun getOperatorLink() = R.string.take_operator_link
        override fun getView() = TakeOperatorView::class.java
    },
    FILTER {
        override fun getOperatorName() = "Filter(even emissions)"
        override fun getOperatorDescription() = R.string.filter_operator_desc
        override fun getOperatorLink() = R.string.filter_operator_link
        override fun getView() = FilterOperatorView::class.java
    },
    SKIP {
        override fun getOperatorName() = "Skip(3)"
        override fun getOperatorDescription() = R.string.skip_operator_desc
        override fun getOperatorLink() = R.string.skip_operator_link
        override fun getView() = SkipOperatorView::class.java
    },
    TAKE_LAST {
        override fun getOperatorName() = "Take Last(2)"
        override fun getOperatorDescription() = R.string.take_last_operator_desc
        override fun getOperatorLink() = R.string.take_last_operator_link
        override fun getView() = TakeLastOperator::class.java
    }
}

enum class TransformingOperators : Operators {
    MAP {
        override fun getOperatorName() = "Map(it->it*2)"
        override fun getOperatorDescription() = R.string.map_operator_desc
        override fun getOperatorLink() = R.string.map_operator_link
        override fun getView(): Class<*> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    BUFFER {
        override fun getOperatorName() = "Buffer(2)"
        override fun getOperatorDescription() = R.string.buffer_operator_desc
        override fun getOperatorLink() = R.string.buffer_operator_link
        override fun getView(): Class<*> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    };
}