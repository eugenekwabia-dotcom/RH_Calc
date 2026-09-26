package com.ekwabia.rhcalc.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class HumidityGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var animatedHumidity = 0f
    private var animator: ValueAnimator? = null

    private val arcRect = RectF()

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.DKGRAY
    }
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
        color = Color.RED
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 48f
    }

    private val dryColor = Color.parseColor("#E0A458")
    private val normalColor = Color.parseColor("#4CAF50")
    private val humidColor = Color.parseColor("#2196F3")

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = w * 0.08f
        arcRect.set(padding, padding, w - padding, w - padding)
        arcPaint.strokeWidth = w * 0.08f
    }

    fun setHumidity(value: Float, animate: Boolean = true) {
        val clamped = value.coerceIn(0f, 100f)
        animator?.cancel()
        if (animate) {
            animator = ValueAnimator.ofFloat(animatedHumidity, clamped).apply {
                duration = 800
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    animatedHumidity = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            animatedHumidity = clamped
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (arcRect.isEmpty) return

        arcPaint.color = dryColor
        canvas.drawArc(arcRect, 180f, 54f, false, arcPaint)
        arcPaint.color = normalColor
        canvas.drawArc(arcRect, 234f, 54f, false, arcPaint)
        arcPaint.color = humidColor
        canvas.drawArc(arcRect, 288f, 72f, false, arcPaint)

        val cx = arcRect.centerX()
        val cy = arcRect.centerY()
        val radius = arcRect.width() / 2f

        for (i in 0..10) {
            val angle = Math.toRadians((180 + i * 18).toDouble())
            val startX = cx + (radius - 30) * cos(angle).toFloat()
            val startY = cy + (radius - 30) * sin(angle).toFloat()
            val endX = cx + radius * cos(angle).toFloat()
            val endY = cy + radius * sin(angle).toFloat()
            canvas.drawLine(startX, startY, endX, endY, tickPaint)
        }

        val needleAngle = Math.toRadians((180 + animatedHumidity * 1.8).toDouble())
        val needleLength = radius - 50
        val needleX = cx + needleLength * cos(needleAngle).toFloat()
        val needleY = cy + needleLength * sin(needleAngle).toFloat()
        canvas.drawLine(cx, cy, needleX, needleY, needlePaint)
        canvas.drawCircle(cx, cy, 12f, needlePaint)

        canvas.drawText("${animatedHumidity.roundToInt()}%", cx, cy + radius / 2, textPaint)
    }
}
