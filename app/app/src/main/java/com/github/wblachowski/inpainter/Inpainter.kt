package com.github.wblachowski.inpainter

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.nio.MappedByteBuffer

class Inpainter(modelFile: MappedByteBuffer) {

    companion object : SingletonHolder<Inpainter, MappedByteBuffer>(::Inpainter)

    val interpreter: Interpreter

    init {
        val delegate = GpuDelegate()
        val options = Interpreter.Options().addDelegate(delegate)

        interpreter = Interpreter(modelFile, options)
    }

    private fun getResult(image: Bitmap): Bitmap {
        return image
    }


}