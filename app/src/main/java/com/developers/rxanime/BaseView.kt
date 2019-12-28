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
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.developers.rxanime.model.CanvasAction
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.model.RxAnimeState
import com.developers.rxanime.util.awaitEnd
import com.developers.rxanime.util.awaitViewDrawn
import com.developers.rxanime.util.spToPx
import com.developers.rxanime.util.toPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

abstract class BaseView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    // Animate to move the marble
    private var circleY: Float = 30.toPx().toFloat()
    // Animate to scale the marble radius
    private var circleRadius = 5.toPx().toFloat()

    private val lineStartY = 10.toPx()
    private val centreDistance = 100.toPx()
    var offset = 0f
    private var marbleStartY = 30.toPx().toFloat()

    // To be assigned once view is drawn
    var leftLineStart = 0f
    // To be assigned once view is drawn
    var rightLineStart = 0f

    val linePaint = Paint()
    private val leftCirclePaint = Paint()
    private val marblePaint = Paint()
    private val textPaint = Paint()

    private val bounds = Rect()

    private var rxAnimeState = RxAnimeState()
    private val marbleList = mutableListOf<MarbleData>()

    // TODO: change this by attaching lifecycle from activity/fragment
    var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        linePaint.apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        leftCirclePaint.apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 30f
        }
        marblePaint.apply {
            color = Color.RED
            isAntiAlias = true
        }
        textPaint.apply {
            color = Color.WHITE
            textSize = 15.toPx().toFloat()
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = 16.spToPx().toFloat()
            getTextBounds("0", 0, 1, bounds)
        }

        coroutineScope.launch {
            awaitViewDrawn()
            leftLineStart = width / 2.toFloat() - centreDistance
            rightLineStart = width / 2.toFloat() + centreDistance
            animateMarbles()
        }
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
        // Draw Translating marble
        canvas?.drawCircle(leftLineStart, circleY, circleRadius, leftCirclePaint)

        Log.d(TAG, "State: " + rxAnimeState.canvasAction)

        when (rxAnimeState.canvasAction) {
            CanvasAction.DRAW_OPERATOR -> {
                drawMovingNumericalMarbles(canvas)
                drawOperator(canvas, rxAnimeState.currentData)
            }
            CanvasAction.INITIAL_STATE -> {
                canvas?.drawColor(Color.WHITE)
            }
        }

    }

    /**
     * Draws moving textual marbles on canvas.
     *
     * @canvas Canvas for the custom view
     */
    private fun drawMovingNumericalMarbles(canvas: Canvas?) {
        for (marbles in marbleList) {
            drawNumericMarbles(cx = marbles.cx, cy = marbles.cy, number = marbles.data, canvas = canvas)
        }
    }


    abstract fun drawOperator(canvas: Canvas?, currentData: MarbleData)

    private suspend fun animateMarbles() {
        withContext(Dispatchers.Main) {
            // Initialize the animator Set
            val (propertyHolderY, animatorSet) = initializeAnimator()
            // Repeat the animation 4 times
            repeat(5) { currentMarbleData ->
                animatorSet.start()
                // Wait for end
                animatorSet.awaitEnd()

                marbleStartY += Y_OFFSET
                val currentMarble = MarbleData(leftLineStart, marbleStartY, currentMarbleData)
                // Dispatch Action and data
                rxAnimeState = rxAnimeState.copy(canvasAction = CanvasAction.DRAW_OPERATOR,
                        currentData = currentMarble)
                marbleList.add(currentMarble)

                propertyHolderY.setFloatValues(marbleStartY, marbleStartY + Y_OFFSET)
            }
        }
    }

    private fun drawNumericMarbles(cx: Float, cy: Float, number: Int, canvas: Canvas?) {
        val text = number.toString()
        canvas?.drawCircle(cx, cy, 15.toPx().toFloat(), marblePaint)
        val yOffset = bounds.height() / 2
        canvas?.drawText(text, cx, cy + yOffset, textPaint)
        invalidate()
    }

    private fun initializeAnimator(): Pair<PropertyValuesHolder, AnimatorSet> {
        marbleStartY = 30.toPx().toFloat()
        circleRadius = 5.toPx().toFloat()
        offset = 0f
        marbleList.clear()
        val propertyHolderY = PropertyValuesHolder.ofFloat(MARBLE_TRANSLATION_Y, marbleStartY, marbleStartY + Y_OFFSET)
        val propertyValueScale = PropertyValuesHolder.ofFloat(MARBLE_SCALE_PROPERTY, circleRadius, 10.toPx().toFloat())
        val propertyValueTranslateX = PropertyValuesHolder.ofFloat("X", leftLineStart, centreDistance * 2f)

        // Animator for Y coordinate of marble
        val circleYAnimator = ValueAnimator().apply {
            duration = 900
            setValues(propertyHolderY)
            interpolator = LinearInterpolator()
            addUpdateListener {
                circleY = it.animatedValue as Float
                invalidate()
            }
        }

        // Animator for scaling marble
        val scaleAnimation = ValueAnimator().apply {
            duration = 300
            setValues(propertyValueScale)
            interpolator = LinearInterpolator()
            addUpdateListener {
                circleRadius = it.animatedValue as Float
                invalidate()
            }
        }

        // Animator for translating the line to show emission
        val lineTranslateAnimator = ValueAnimator().apply {
            duration = 900
            setValues(propertyValueTranslateX)
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                offset = it.animatedValue as Float
                invalidate()
            }
        }

        val animatorSet = AnimatorSet().apply {
            playSequentially(circleYAnimator, scaleAnimation)
            playTogether(lineTranslateAnimator)
            interpolator = LinearInterpolator()
        }

        return Pair(propertyHolderY, animatorSet)
    }

    fun attachScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    suspend fun restart() {
        rxAnimeState = rxAnimeState.copy(canvasAction = CanvasAction.INITIAL_STATE)
        animateMarbles()
    }


    companion object {
        private const val MARBLE_SCALE_PROPERTY = "MARBLE_SCALE"
        private const val MARBLE_TRANSLATION_Y = "MARBLE_TRANSLATION"
        private const val Y_OFFSET = 200.toFloat()
        private const val TAG = "BaseView"
    }
}

