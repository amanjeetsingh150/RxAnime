package com.developers.rxanime

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.util.TypedValue
import android.util.DisplayMetrics
import android.graphics.*
import android.util.Log
import com.developers.rxanime.model.EmissionCircleData


class StreamView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet), Animator.AnimatorListener {

    private var initialCircleY = 0f
    private var endVal = 0f
    private var circleRadius = 0
    private var emissionCircleList = mutableListOf<EmissionCircleData?>()
    private var takeOperatorEmissionList = mutableListOf<EmissionCircleData>()
    private var filterOperatorEmissionList = mutableListOf<EmissionCircleData>()
    private var skipOperatorEmissionList = mutableListOf<EmissionCircleData>()
    private var mapOperatorEmissionList = mutableListOf<EmissionCircleData>()
    private var startPoint = 0f
    private var count = 0
    private var isAnimated = false
    private var shouldDrawMarbleEmission = false

    private var paintLeftLine = Paint()
    private var leftCirclePaint = Paint()
    private var circlePaint = Paint()
    private var textPaint = Paint()
    private val bounds = Rect()
    private var canShowTakeOperator = false
    private var canShowFilterOperator = false
    private var canShowSkipOperator = false
    private var canShowMapOperator = false
    private var isResetNeeded = false
    private var isAnimating = false

    private val differenceOfFirstLineFromCenter = getDimensionInPixel(100)
    private val differenceOfSecondLineFromCenter = getDimensionInPixel(100)

    companion object {
        private val MAX_EMISSION_COUNT = 5
    }

    init {
        paintLeftLine.color = Color.BLACK
        paintLeftLine.isAntiAlias = true
        paintLeftLine.style = Paint.Style.STROKE
        paintLeftLine.strokeWidth = 5f

        leftCirclePaint.color = Color.BLACK
        leftCirclePaint.style = Paint.Style.FILL_AND_STROKE
        leftCirclePaint.strokeWidth = 30f

        circlePaint.color = Color.RED
        circlePaint.isAntiAlias = true

        textPaint.color = Color.WHITE
        textPaint.textSize = getDimensionInPixelFromSP(15).toFloat()
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER

        textPaint.getTextBounds("0", 0, 1, bounds)
        canShowTakeOperator = true
    }

    constructor(context: Context) : this(context, attributeSet = null)

    fun init() {
        initialCircleY = 10f
        startPoint = 10f
        endVal = 0f
        circleRadius = getDimensionInPixel(10)
        shouldDrawMarbleEmission = false
        count = 0

        emissionCircleList.clear()
        takeOperatorEmissionList.clear()
        skipOperatorEmissionList.clear()
        filterOperatorEmissionList.clear()

        canShowSkipOperator = getCanShowSkipOperator()
        canShowTakeOperator = getCanShowTakeOperator()
        canShowFilterOperator = getCanShowFilterOperator()
        canShowMapOperator = getCanShowMapOperator()

        if (!isAnimated && !isAnimating) {
            postDelayed({ animateStreamMarbles() }, 300)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = getDimensionInPixel(400)
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var height = 0
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize)
        } else {
            height = desiredHeight
        }
        setMeasuredDimension(desiredWidth, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        shouldReset(canvas)
        Log.d("StreamView", canShowMapOperator.toString())
        if (!isResetNeeded) {
            canvas?.drawLine(width.div(2).toFloat() - differenceOfFirstLineFromCenter, getDimensionInPixel(10).toFloat(), width.div(2).toFloat() - differenceOfFirstLineFromCenter, height.toFloat(), paintLeftLine)
            canvas?.drawLine(width.div(2).toFloat() + differenceOfSecondLineFromCenter, getDimensionInPixel(10).toFloat(), width.div(2).toFloat() + differenceOfSecondLineFromCenter, height.toFloat(), paintLeftLine)
            canvas?.drawCircle(width.div(2).toFloat() - differenceOfFirstLineFromCenter, initialCircleY, circleRadius.toFloat(), leftCirclePaint)
        }
        when {
            canShowTakeOperator -> drawTakeOperator(canvas)
            canShowFilterOperator -> drawFilterOperator(canvas)
            canShowSkipOperator -> drawSkipOperator(canvas)
            canShowMapOperator -> drawMapOperator(canvas)
            else -> {
                //Do nothing
            }
        }
        if (shouldDrawMarbleEmission && count < 5 && !isResetNeeded) {
            drawMovingMarbleEmission(canvas)
        }
        if (count <= MAX_EMISSION_COUNT && !emissionCircleList.isEmpty() && !isResetNeeded) {
            drawEmissionMarbles(canvas)
        }
    }

    private fun drawMovingMarbleEmission(canvas: Canvas?) {
        drawEmissionCircle(canvas, count, width.div(2).toFloat() - differenceOfFirstLineFromCenter, endVal)
        shouldDrawMarbleEmission = false
    }

    private fun drawEmissionMarbles(canvas: Canvas?) {
        for (i in 0..emissionCircleList.size) {
            if (emissionCircleList.getOrNull(index = i) != null) {
                drawEmissionCircle(canvas, emissionCircleList[i]?.data!!, width.div(2).toFloat() - differenceOfFirstLineFromCenter, emissionCircleList[i]?.cy!!)
            }
        }
    }

    private fun drawTakeOperator(canvas: Canvas?) {
        if (!takeOperatorEmissionList.isEmpty()) {
            for (i in 0..takeOperatorEmissionList.size) {
                if (takeOperatorEmissionList.getOrNull(index = i) != null && !isResetNeeded) {
                    canvas?.drawLine(width.div(2).toFloat() - differenceOfFirstLineFromCenter, takeOperatorEmissionList[i].cy, width.div(2).toFloat() + differenceOfSecondLineFromCenter, takeOperatorEmissionList[i].cy, paintLeftLine)
                }
            }
        }
    }

    private fun drawSkipOperator(canvas: Canvas?) {
        if (!skipOperatorEmissionList.isEmpty()) {
            for (i in 0..skipOperatorEmissionList.size) {
                if (skipOperatorEmissionList.getOrNull(index = i) != null && !isResetNeeded) {
                    canvas?.drawLine(width.div(2).toFloat() - differenceOfFirstLineFromCenter, skipOperatorEmissionList[i].cy, width.div(2).toFloat() + differenceOfSecondLineFromCenter, skipOperatorEmissionList[i].cy, paintLeftLine)
                }
            }
        }
    }

    private fun drawFilterOperator(canvas: Canvas?) {
        if (!filterOperatorEmissionList.isEmpty()) {
            for (i in 0..filterOperatorEmissionList.size) {
                if (filterOperatorEmissionList.getOrNull(index = i) != null && !isResetNeeded) {
                    canvas?.drawLine(width.div(2).toFloat() - differenceOfFirstLineFromCenter, filterOperatorEmissionList[i].cy, width.div(2).toFloat() + differenceOfSecondLineFromCenter, filterOperatorEmissionList[i].cy, paintLeftLine)
                }
            }
        }
    }

    private fun drawMapOperator(canvas: Canvas?) {
        if (!mapOperatorEmissionList.isEmpty()) {
            for (i in 0..mapOperatorEmissionList.size) {
                if (mapOperatorEmissionList.getOrNull(i) != null && !isResetNeeded) {
                    canvas?.drawLine(width.div(2).toFloat() - differenceOfFirstLineFromCenter, mapOperatorEmissionList[i].cy, width.div(2).toFloat() + differenceOfSecondLineFromCenter, mapOperatorEmissionList[i].cy, paintLeftLine)
                    drawEmissionCircle(canvas, mapOperatorEmissionList[i].data, width.div(2).toFloat() + differenceOfSecondLineFromCenter, mapOperatorEmissionList[i].cy)
                }
            }
        }
    }

    private fun animateStreamMarbles() {
        isAnimated = false
        isAnimating = true
        val propertyValueHolderYAnimation = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, startPoint, startPoint + getDimensionInPixel(50).toFloat())
        val propertyValueHolderScale = PropertyValuesHolder.ofFloat("CIRCLE_SCALE", circleRadius.toFloat(), getDimensionInPixel(15).toFloat())
        val animator = ValueAnimator()
        animator.duration = 700
        animator.setValues(propertyValueHolderYAnimation)
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            initialCircleY = it.animatedValue as Float
            invalidate()
        }
        val scaleAnimation = ValueAnimator()
        scaleAnimation.duration = 300
        scaleAnimation.setValues(propertyValueHolderScale)
        scaleAnimation.interpolator = LinearInterpolator()
        scaleAnimation.addUpdateListener {
            circleRadius = (it.animatedValue as Float).toInt()
            invalidate()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(animator, scaleAnimation)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if (count < MAX_EMISSION_COUNT) {
                    shouldDrawMarbleEmission = true
                    startPoint += getDimensionInPixel(50)
                    endVal = startPoint + getDimensionInPixel(50)
                    emissionCircleList.add(EmissionCircleData(width.div(2).toFloat() - differenceOfFirstLineFromCenter, startPoint, count))
                    propertyValueHolderYAnimation.setFloatValues(startPoint, endVal)
                    if (count < 3 && canShowTakeOperator) {
                        takeOperatorEmissionList.add(emissionCircleList[count]!!)
                    } else if (count % 2 == 0 && canShowFilterOperator) {
                        filterOperatorEmissionList.add(emissionCircleList[count]!!)
                    } else if (count > 2 && canShowSkipOperator) {
                        skipOperatorEmissionList.add(emissionCircleList[count]!!)
                    } else if (canShowMapOperator) {
                        val mappedEmissionData = emissionCircleList[count]!!.data * 2
                        val mappedEmission = EmissionCircleData(width.div(2).toFloat() - differenceOfFirstLineFromCenter, startPoint, mappedEmissionData)
                        mapOperatorEmissionList.add(mappedEmission)
                    }
                    animatorSet.start()
                    animatorSet.addListener(this@StreamView)
                    count++
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
    }

    private fun drawEmissionCircle(canvas: Canvas?, number: Int, cx: Float, cy: Float) {
        val text = number.toString()
        canvas?.drawCircle(cx, cy, getDimensionInPixel(15).toFloat(), circlePaint)
        val yOffset = bounds.height() / 2
        canvas?.drawText(text, cx, cy + yOffset, textPaint)
    }

    private fun getDimensionInPixel(dp: Int): Int {
        val density = resources.displayMetrics.densityDpi

        var modifieddp = dp
        when (density) {
            DisplayMetrics.DENSITY_LOW -> modifieddp = dp - dp / 2
            DisplayMetrics.DENSITY_MEDIUM -> modifieddp = dp - dp / 3
            DisplayMetrics.DENSITY_HIGH -> modifieddp = dp - dp / 4
            DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XXXHIGH -> modifieddp = dp
            else -> {
            }
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, modifieddp.toFloat(), resources.displayMetrics).toInt()
    }

    private fun getDimensionInPixelFromSP(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), resources.displayMetrics).toInt()
    }

    fun setCanShowTakeOperatorAnimation(canShowTakeOperator: Boolean) {
        this.canShowTakeOperator = canShowTakeOperator
    }

    private fun getCanShowTakeOperator(): Boolean {
        return canShowTakeOperator
    }

    fun setCanShowSkipOperatorAnimation(canShowSkipOperator: Boolean) {
        this.canShowSkipOperator = canShowSkipOperator
    }

    private fun getCanShowSkipOperator(): Boolean {
        return canShowSkipOperator
    }

    fun setCanShowFilterOperatorAnimation(canShowFilterOperator: Boolean) {
        this.canShowFilterOperator = canShowFilterOperator
    }

    private fun getCanShowFilterOperator(): Boolean {
        return canShowFilterOperator
    }

    fun setCanShowMapOperatorAnimation(canShowMapOperator: Boolean) {
        this.canShowMapOperator = canShowMapOperator
    }

    private fun getCanShowMapOperator(): Boolean {
        Log.d("StraeamView","Show "+canShowMapOperator)
        return canShowMapOperator
    }

    fun setShouldReset(isResetNeeded: Boolean) {
        this.isResetNeeded = isResetNeeded
    }

    private fun shouldReset(canvas: Canvas?) {
        if (isResetNeeded) {
            canvas?.drawColor(Color.WHITE)
        }
    }

    override fun onAnimationEnd(animation: Animator?) {
        if (count >= MAX_EMISSION_COUNT) {
            isAnimating = false
        }
    }

    override fun onAnimationStart(animation: Animator?) {

    }

    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onAnimationCancel(animation: Animator?) {

    }
}