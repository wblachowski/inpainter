package com.github.wblachowski.inpainter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_display_result.*
import kotlinx.android.synthetic.main.activity_display_result.resultLayout
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import android.os.Environment
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.widget.Toast


class DisplayResultActivity : AppCompatActivity() {

    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_result)
        val bitmap = MemoryCacher.getBitmapFromMemCache("bitmap")
        imgSaveButton.setOnClickListener { saveToExternalStorage(bitmap!!) }
        val layoutWidth = intent.getIntExtra("layoutWidth", 1080)
        val permissions = arrayOf(WRITE_EXTERNAL_STORAGE)
        requestPermissions(permissions, 1)
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

    private fun saveToExternalStorage(bitmapImage: Bitmap) {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/inpainter")
        myDir.mkdirs()
        val tsLong = System.currentTimeMillis() / 1000
        val timeStamp = tsLong.toString()
        val fname = "Inpainting_" + timeStamp + ".jpg"
        val file = File(myDir, fname)
        Log.d("file", "" + file)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(applicationContext, "Image saved", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Image could not be saved!", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Without permissions we won't save your image!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {

            }
        }
    }
}
