package com.developers.rxanime

import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.developers.rxanime.model.CanvasAction
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.model.RxAnimeState
import com.developers.rxanime.model.RxFrame
import com.developers.rxanime.util.awaitEnd
import com.developers.rxanime.util.awaitViewDrawn
import com.developers.rxanime.util.spToPx
import com.developers.rxanime.util.toPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

abstract class BaseView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet), RxAnimationCallbacks {

    private var currentMarble: MarbleData = MarbleData()
    private var leftMarble = MarbleData()
    lateinit var rxFrame: RxFrame

    // Animate to scale the right marble radius
    var rightCircleRadius = 0.toPx().toFloat()

    private val lineStartY = 10.toPx()
    private val centreDistance = 100.toPx()

    // To be assigned once view is drawn
    private var leftLineStart = 0f
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
    private var coroutineScope = CoroutineScope(Dispatchers.Default)

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
        canvas?.drawCircle(leftLineStart, leftMarble.cy, leftMarble.radius, leftCirclePaint)

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

            val (propertyHolderY, animatorSet) = initializeAnimator()
            // Repeat the animation 5 times
            repeat(5) { currentMarbleData ->
                animatorSet.start()
                // Wait for end
                animatorSet.awaitEnd()

                addEmissions(currentMarble)

                currentMarble = MarbleData(leftLineStart, leftMarble.cy, currentMarbleData)
                // Dispatch Action and data
                rxAnimeState = rxAnimeState.copy(canvasAction = CanvasAction.DRAW_OPERATOR_WITH_LINE,
                        currentData = currentMarble)
                marbleList.add(currentMarble)

                propertyHolderY.setFloatValues(leftMarble.cy, leftMarble.cy + Y_OFFSET)
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

    private fun initializeAnimator(): Pair<PropertyValuesHolder, AnimatorSet> {
        rightCircleRadius = 0.toPx().toFloat()
        leftMarble = MarbleData(cx = leftLineStart, cy = 30.toPx().toFloat(),
                data = 0, radius = 5.toPx().toFloat())
        rxFrame = RxFrame(leftMarble = leftMarble, emissionLineX = 0f)
        marbleList.clear()

        val rxAnimation = RxAnimation.Builder()
                .leftMarble(leftMarble)
                .emissionLineOffset(rxFrame.emissionLineX)
                .leftLineStart(leftLineStart)
                .rightMarbleRadius(rightCircleRadius)
                .rxAnimationCallback(this)
                .build()

        return rxAnimation.createAnimator()
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

    override fun updateLeftMarbleY(leftMarbleY: Float) {
        leftMarble.cy = leftMarbleY
        invalidate()
    }

    override fun updateLeftMarbleRadius(leftMarbleRadius: Float) {
        leftMarble.radius = leftMarbleRadius
        invalidate()
    }

    override fun updateEmissionLineOffset(emissionLineOffset: Float) {
        rxFrame.emissionLineX = emissionLineOffset
        invalidate()
    }

    override fun updateRightMarbleRadius(rightMarbleRadius: Float) {
        rightCircleRadius = rightMarbleRadius
        invalidate()
    }


    companion object {
        private const val Y_OFFSET = 200.toFloat()
        private const val TAG = "BaseView"
    }
}

