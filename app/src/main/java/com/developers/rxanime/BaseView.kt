package com.developers.rxanime

import android.animation.*
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
import kotlinx.coroutines.*
import kotlin.math.min

abstract class BaseView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    private var currentMarble: MarbleData = MarbleData()
    // Animate to move the marble
    private var circleY: Float = 30.toPx().toFloat()
    // Animate to scale the left marble radius
    private var leftCircleRadius = 5.toPx().toFloat()
    // Animate to scale the right marble radius
    var rightCircleRadius = 0.toPx().toFloat()

    private val lineStartY = 10.toPx()
    private val centreDistance = 100.toPx()
    var offset = 0f
    private var marbleStartY = 30.toPx().toFloat()

    // To be assigned once view is drawn
    var leftLineStart = 0f
    // To be assigned once view is drawn
    var rightLineStart = 0f

    val linePaint = Paint()
    val leftCirclePaint = Paint()
    private val marblePaint = Paint()
    private val textPaint = Paint()

    private val bounds = Rect()

    private var rxAnimeState = RxAnimeState()
    private val marbleList = mutableListOf<MarbleData>()

    protected val emissions = mutableListOf<MarbleData>()

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
        canvas?.drawCircle(leftLineStart, circleY, leftCircleRadius, leftCirclePaint)

        Log.d(TAG, "State: " + rxAnimeState.canvasAction)

        when (rxAnimeState.canvasAction) {
            CanvasAction.INITIAL_STATE -> {
                canvas?.drawColor(Color.WHITE)
            }
            CanvasAction.DRAW_OPERATOR_WITH_LINE -> {
                drawMovingNumericalMarbles(canvas)
                drawOperator(canvas, rxAnimeState.currentData)
                drawEmission(canvas, emissions)
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


    /**
     * Draws a translated line for emission of data in operator
     */
    abstract fun drawOperator(canvas: Canvas?, currentData: MarbleData)

    /**
     * Adds emitted [MarbleData] in a list
     */
    abstract fun addEmissions(currentData: MarbleData)

    /**
     * Initializes the animators and start the animation for canvas.
     */
    private suspend fun animateMarbles() {
        emissions.clear()
        withContext(Dispatchers.Main) {
            // Initialize the animator Set
            val (propertyHolderY, animatorSet) = initializeAnimator()
            // Repeat the animation 5 times
            repeat(5) { currentMarbleData ->
                animatorSet.start()
                // Wait for end
                animatorSet.awaitEnd()

                addEmissions(currentMarble)

                marbleStartY += Y_OFFSET
                currentMarble = MarbleData(leftLineStart, marbleStartY, currentMarbleData)
                // Dispatch Action and data
                rxAnimeState = rxAnimeState.copy(canvasAction = CanvasAction.DRAW_OPERATOR_WITH_LINE,
                        currentData = currentMarble)
                marbleList.add(currentMarble)

                propertyHolderY.setFloatValues(marbleStartY, marbleStartY + Y_OFFSET)
            }
        }
    }

    /**
     * Draws a marble with numerical text in centre
     */
    private fun drawNumericMarbles(cx: Float, cy: Float, number: Int, canvas: Canvas?) {
        val text = number.toString()
        canvas?.drawCircle(cx, cy, 15.toPx().toFloat(), marblePaint)
        val yOffset = bounds.height() / 2
        canvas?.drawText(text, cx, cy + yOffset, textPaint)
        invalidate()
    }

    /**
     * Initializes the following animators and sets up with a [AnimatorSet]:
     * 1. CircleAnimator: Translates the Y coordinate of marble i.e property [circleY].
     * 2. ScaleAnimator: Scales radius of a marble i.e property [leftCircleRadius].
     * 3. LineTranslateAnimator: Line translation of the emissions with a [offset].
     */
    private fun initializeAnimator(): Pair<PropertyValuesHolder, AnimatorSet> {
        marbleStartY = 30.toPx().toFloat()
        leftCircleRadius = 5.toPx().toFloat()
        rightCircleRadius = 0.toPx().toFloat()
        offset = 0f
        marbleList.clear()
        val propertyHolderY = PropertyValuesHolder.ofFloat(MARBLE_TRANSLATION_Y, marbleStartY, marbleStartY + Y_OFFSET)
        val propertyLeftCircleScale = PropertyValuesHolder.ofFloat(MARBLE_SCALE_PROPERTY, leftCircleRadius, 10.toPx().toFloat())
        val propertyValueTranslateX = PropertyValuesHolder.ofFloat(EMISSION_OFFSET_X, leftLineStart, centreDistance * 2f)
        val propertyRightCircleScale = PropertyValuesHolder.ofFloat(MARBLE_SCALE_PROPERTY, rightCircleRadius, 10.toPx().toFloat())

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
        val leftMarbleScaleAnimation = ValueAnimator().apply {
            duration = 300
            setValues(propertyLeftCircleScale)
            interpolator = LinearInterpolator()
            addUpdateListener {
                leftCircleRadius = it.animatedValue as Float
                invalidate()
            }
        }

        val rightMarbleScaleAnimator = ValueAnimator().apply {
            duration = 400
            setValues(propertyRightCircleScale)
            interpolator = LinearInterpolator()
            addUpdateListener {
                rightCircleRadius = it.animatedValue as Float
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

        val lineMarbleAnimatorSet = AnimatorSet().apply {
            playSequentially(lineTranslateAnimator, rightMarbleScaleAnimator)
            interpolator = LinearInterpolator()
        }

        val marbleAnimatorSet = AnimatorSet().apply {
            playSequentially(circleYAnimator, leftMarbleScaleAnimation)
            playTogether(lineMarbleAnimatorSet)
            interpolator = LinearInterpolator()
        }

        return Pair(propertyHolderY, marbleAnimatorSet)
    }

    fun attachScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    /**
     *  Restarts the animation on canvas
     */
    suspend fun restart() {
        rxAnimeState = rxAnimeState.copy(canvasAction = CanvasAction.INITIAL_STATE)
        animateMarbles()
    }

    /**
     * Draws a static line and emitted marble on canvas after a data emission.
     */
    private fun drawEmission(canvas: Canvas?, emissions: MutableList<MarbleData>) {
        if (emissions.isNotEmpty()) {
            emissions.forEach {
                canvas?.drawLine(it.cx + 14.toPx(), it.cy, rightLineStart, it.cy, linePaint)
                drawNumericMarbles(cx = rightLineStart, cy = it.cy, number = it.data, canvas = canvas)
            }
        }
    }

    companion object {
        private const val MARBLE_SCALE_PROPERTY = "MARBLE_SCALE"
        private const val MARBLE_TRANSLATION_Y = "MARBLE_TRANSLATION"
        private const val EMISSION_OFFSET_X = "EMISSION_X"
        private const val Y_OFFSET = 200.toFloat()
        private const val TAG = "BaseView"
    }
}

