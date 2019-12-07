package com.github.wblachowski.inpainter

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class ImageView(context: Context, private var bitmap: Bitmap, private var layoutSize: Int) :
    View(context) {

    private var scaledBitmap: Bitmap = getCroppedAndScaledBitmap()
    private var paint = Paint()
    private var path = Path()

    init {
        paint.isAntiAlias = true
        paint.color = Color.RED
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 15f
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

    private fun getCroppedAndScaledBitmap(): Bitmap {
        var newH = layoutSize
        var newW = layoutSize
        var cropX = 0
        var cropY = 0
        if (bitmap.width < bitmap.height) {
            newH = (bitmap.height * layoutSize) / bitmap.width
            cropY = (newH - layoutSize) / 2
        } else {
            newW = (bitmap.width * layoutSize) / bitmap.height
            cropX = (newW - layoutSize) / 2
        }
        var result = Bitmap.createScaledBitmap(bitmap, newW, newH, true)
        result = Bitmap.createBitmap(result, cropX, cropY, layoutSize, layoutSize)
        return result
    }
}