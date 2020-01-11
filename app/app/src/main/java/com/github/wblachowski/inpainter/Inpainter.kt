package com.github.wblachowski.inpainter

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer

class Inpainter(modelFile: MappedByteBuffer) {

    companion object : SingletonHolder<Inpainter, MappedByteBuffer>(::Inpainter)

    val interpreter: Interpreter

    init {
        interpreter = Interpreter(modelFile)
    }

    private fun getResult(image: Bitmap): Bitmap {
        return image
    }


}