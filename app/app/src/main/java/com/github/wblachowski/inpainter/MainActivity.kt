package com.github.wblachowski.inpainter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.Images.Media.getBitmap
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.drawable.BitmapDrawable


class MainActivity : AppCompatActivity() {

    private var imageView: ImageView? = null
    private var bitmapChanged: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgSelectButton.setOnClickListener { openImageSelectionIntent() }
        processButton.setOnClickListener { process() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_IMG_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                imageView = ImageView(this, getBitmap(contentResolver, it), imageLayout.width)
                imageLayout.addView(imageView)
            }
            processButton.visibility = View.VISIBLE
        }
    }

    private fun openImageSelectionIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select image"), SELECT_IMG_REQUEST)
    }

    private fun process() {
        imageView?.let {
            val bitmap =
                Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.draw(canvas)
            val view = android.widget.ImageView(this)
            view.setImageBitmap(bitmap)
            view.invalidate()
            val drawable = view.drawable as BitmapDrawable
            bitmapChanged = drawable.bitmap
        }

        processButton.isEnabled = false
        processButton.text = getString(R.string.processing)
        progressBar.bringToFront()
        progressBar.show()
        Handler().postDelayed({
            processButton.isEnabled = true
            processButton.text = getString(R.string.process)
            progressBar.hide()
            showResult()
        }, 2000)
    }

    private fun showResult() {
        MemoryCacher.addBitmapToMemoryCache("bitmap", bitmapChanged!!)
        val intent = Intent(this, DisplayResultActivity::class.java).apply {
            putExtra("layoutWidth", imageLayout.width)
        }
        startActivity(intent)
    }

    companion object {
        const val SELECT_IMG_REQUEST = 1
    }
}
