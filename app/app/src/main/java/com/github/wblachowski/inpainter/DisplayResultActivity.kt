package com.github.wblachowski.inpainter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_display_result.*
import kotlinx.android.synthetic.main.activity_display_result.resultLayout


class DisplayResultActivity : AppCompatActivity() {

    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_result)
        imgSaveButton.setOnClickListener { save() }
        val bitmap = MemoryCacher.getBitmapFromMemCache("bitmap")
        val layoutWidth = intent.getIntExtra("layoutWidth", 1080)
        start(bitmap, layoutWidth)
    }

    private fun start(bitmap: Bitmap?, layoutWidth: Int) {
        imageView = ImageView(this, bitmap!!, layoutWidth)

        val canvas = Canvas(bitmap)
        imageView!!.draw(canvas)
        val view = android.widget.ImageView(this)
        view.setImageBitmap(bitmap)
        resultLayout.addView(view)
    }


    private fun save() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
