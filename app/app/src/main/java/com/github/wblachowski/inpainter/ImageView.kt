package com.github.wblachowski.inpainter

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class ImageView(context: Context, private var bitmap: Bitmap) : View(context) {

    private var scaledBitmap: Bitmap = bitmap.copy(bitmap.config, true)
    private var paint = Paint()
    private var path = Path()

    init {
        paint.isAntiAlias = true
        paint.color = Color.RED
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 15f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(scaledBitmap, 0f, 0f, null)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(event.x, event.y)
            MotionEvent.ACTION_MOVE -> path.lineTo(event.x, event.y)
            MotionEvent.ACTION_UP -> {
            }
            else -> return false
        }
        invalidate()
        return true
    }
}