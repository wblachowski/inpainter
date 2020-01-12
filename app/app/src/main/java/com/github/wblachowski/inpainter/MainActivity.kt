package com.github.wblachowski.inpainter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileInputStream
import java.nio.channels.FileChannel


class MainActivity : AppCompatActivity() {

    private var imageView: ImageView? = null
    private var bitmapChanged: Bitmap? = null
    private var inpainter: Inpainter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgSelectButton.setOnClickListener { openImageSelectionIntent() }
        processButton.setOnClickListener { process() }
        loadModel()
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

    private fun loadModel() {
        val fileDescriptor = resources.openRawResourceFd(R.raw.model)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val model = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        Inpainter.init(model)
        inpainter = Inpainter.getInstance()
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
        AsyncTask.execute { inpaint() }
    }

    private fun showResult(bitmap: Bitmap) {
        MemoryCacher.addBitmapToMemoryCache("bitmap", bitmap)
        val intent = Intent(this, DisplayResultActivity::class.java).apply {
            putExtra("layoutWidth", imageLayout.width)
        }
        startActivity(intent)
    }

    private fun inpaint() {
        val matrix = Matrix().apply { postRotate(-90F) }
        val matrix2 = Matrix().apply { postRotate(90F) }
        var scaledBitmap = Bitmap.createScaledBitmap(bitmapChanged!!, 256, 256, false)
        scaledBitmap = Bitmap.createBitmap(scaledBitmap!!, 0, 0, 256, 256, matrix, false)
        val inputImg = Array(1) { Array(256) { Array(256) { FloatArray(3) { 0.0f } } } }
        val inputMask = Array(1) { Array(256) { Array(256) { FloatArray(1) { 0.0f } } } }
        val output = Array(1) { Array(256) { Array(256) { ByteArray(3) { 0 } } } }
        for (x in 0 until scaledBitmap.width) {
            for (y in 0 until scaledBitmap.height) {
                val pixel = scaledBitmap.getPixel(x, y)
                inputImg[0][x][y][0] = Color.blue(pixel).toFloat()
                inputImg[0][x][y][1] = Color.green(pixel).toFloat()
                inputImg[0][x][y][2] = Color.red(pixel).toFloat()
                if (pixel == Color.WHITE) {
                    inputMask[0][x][y][0] = 1.0f
                }
            }
        }
        val outputs: HashMap<Int, Any> = HashMap()
        outputs[0] = output
        inpainter!!.interpreter.runForMultipleInputsOutputs(arrayOf(inputImg, inputMask), outputs)
        var result = Bitmap.createBitmap(scaledBitmap!!)
        for (x in 0 until result.width) {
            for (y in 0 until result.height) {
                val red = output[0][x][y][0].toInt()
                val green = output[0][x][y][1].toInt()
                val blue = output[0][x][y][2].toInt()
                val color = Color.rgb(
                    if (red >= 0) red else 255 + red,
                    if (green >= 0) green else 255 + green,
                    if (blue >= 0) blue else 255 + blue
                )
                result.setPixel(x, y, color)
            }
        }
        result = Bitmap.createBitmap(result, 0, 0, 256, 256, matrix2, false)
        showResult(result)
    }

    companion object {
        const val SELECT_IMG_REQUEST = 1
    }
}
