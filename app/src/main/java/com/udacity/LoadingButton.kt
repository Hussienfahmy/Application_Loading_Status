package com.udacity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var currentBackgroundWidth = 0f
    private lateinit var loadingBackgroundAnimator: ValueAnimator

    private var currentProgressCircleValue = 0f
    private val circleProgressAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        addUpdateListener {
            currentProgressCircleValue = it.animatedValue as Float
            invalidate()
        }
    }

    private val animatorSet = AnimatorSet().apply {
        duration = 3000
        doOnEnd {
            setState(ButtonState.Completed)
        }
    }

    private var buttonState: ButtonState by observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when(new) {
            ButtonState.Loading -> {
                animatorSet.start()
            }
            else -> animatorSet.cancel()
        }
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    private var loadingText: String = ""
    private var loadingBackgroundColor = 0
    private var circleColor = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.default_text_size)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingText = this.getString(R.styleable.LoadingButton_loadingText)?: ""
            loadingBackgroundColor =
                this.getColor(R.styleable.LoadingButton_loadingBackgroundColor, 0)
            circleColor = this.getColor(R.styleable.LoadingButton_circleProgressColor, 0)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(resources.getColor(R.color.colorPrimary, context.theme))

        when (buttonState) {
            ButtonState.Loading -> canvas.drawLoadingState()
            else -> canvas.drawDefaultState()
        }
    }

    private fun Canvas.drawLoadingState() {
        drawRect(RectF(
            0f,
            0f,
            currentBackgroundWidth,
            heightSize.toFloat()
        ), paint.apply { color = loadingBackgroundColor })

        drawText(
            loadingText, width / 2f, height / 2f, paint.apply { color = Color.BLACK }
        )

        val circleSize = (width / 2f) * 0.1f
        val textBounds = Rect()
        paint.getTextBounds(loadingText, 0, loadingText.length ?: 0, textBounds)
        val circleMargin = 14f
        val horizontal = textBounds.right + textBounds.width() + circleMargin
        val vertical = height / 2f

        drawArc(
            RectF(
                horizontal - circleSize,
                vertical - circleSize,
                horizontal + circleSize,
                vertical + circleSize
            ),
            0f,
            currentBackgroundWidth,
            true,
            paint.apply { color = circleColor }
        )
    }

    private fun Canvas.drawDefaultState() {
        drawText(
            resources.getText(R.string.button_name).toString(),
            width / 2f,
            height / 2f,
            paint.apply { color = Color.BLACK }
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initializeBackgroundColorAnimator()
    }

    private fun initializeBackgroundColorAnimator() {
        loadingBackgroundAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            addUpdateListener {
                currentBackgroundWidth = it.animatedValue as Float
                invalidate()
            }
        }
        animatorSet.playTogether(loadingBackgroundAnimator, circleProgressAnimator)
    }

    override fun performClick(): Boolean {
        setState(ButtonState.Clicked)
        return super.performClick()
    }
}