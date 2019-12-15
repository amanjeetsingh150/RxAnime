package com.developers.rxanime

import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.developers.rxanime.util.toPx
import kotlin.math.min

abstract class BaseView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    private var circleY: Float = 0f
    private var circleRadius = 0f
    private val lineStartY = 10.toPx()
    private val centreDistance = 100.toPx()
    private val leftLineStart = width / 2.toFloat() - centreDistance
    private val rightLineStart = width / 2.toFloat() + centreDistance

    private val linePaint = Paint()
    private val leftCirclePaint = Paint()
    private val marblePaint = Paint()
    private val textPaint = Paint()
    private val bounds = Rect()

    init {
        linePaint.apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        leftCirclePaint.apply {
            color = Color.BLACK
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 30f
        }
        marblePaint.apply {
            color = Color.RED
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        textPaint.apply {
            color = Color.WHITE
            textSize = 15.toPx().toFloat()
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            getTextBounds("0", 0, 1, bounds)
        }
        circleY = 10.toPx().toFloat()
        circleRadius = 10.toPx().toFloat()

        animateMarbles()
    }


    constructor(context: Context) : this(context, attributeSet = null)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 400.toPx()
        val height: Int

        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Measure Height
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                min(desiredHeight, heightSize)
            }
            else -> {
                desiredHeight
            }
        }
        setMeasuredDimension(desiredWidth, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Lines
        canvas?.drawLine(leftLineStart, lineStartY.toFloat(), leftLineStart, height.toFloat(), linePaint)
        canvas?.drawLine(rightLineStart, lineStartY.toFloat(), rightLineStart, height.toFloat(), linePaint)

        // Circle marble
        canvas?.drawCircle(leftLineStart, circleY, circleRadius, leftCirclePaint)

        drawOperator(canvas)
    }

    abstract fun drawOperator(canvas: Canvas?)

    private fun animateMarbles() {
        val propertyHolderY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 10.toPx().toFloat(), 60.toPx().toFloat())
        val propertyValueScale = PropertyValuesHolder.ofFloat("CIRCLE_SCALE", circleRadius, 15.toPx().toFloat())

        val circleYAnimator = ValueAnimator().apply {
            duration = 600
            setValues(propertyHolderY)
            interpolator = LinearInterpolator()
            addUpdateListener {
                circleY = it.animatedValue as Float
                invalidate()
            }
        }

        val scaleAnimation = ValueAnimator().apply {
            duration = 300
            setValues(propertyValueScale)
            interpolator = LinearInterpolator()
            addUpdateListener {
                circleRadius = it.animatedValue as Float
                invalidate()
            }
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(circleYAnimator, scaleAnimation)
        animatorSet.start()
    }

    open fun drawNumericMarbles(cx: Float, cy: Float, number: Int, canvas: Canvas?) {
        val text = number.toString()
        canvas?.drawCircle(cx, cy, 15.toPx().toFloat(), marblePaint)
        val yOffset = bounds.height() / 2
        canvas?.drawText(text, cx, cy + yOffset, textPaint)
    }
}