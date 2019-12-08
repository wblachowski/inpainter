package com.github.wblachowski.inpainter

import android.graphics.Bitmap
import android.util.LruCache


object MemoryCacher {

    private var memoryCache: LruCache<String, Bitmap>? = null

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    fun addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
        memoryCache!!.put(key, bitmap)
    }

    fun getBitmapFromMemCache(key: String): Bitmap? {
        return memoryCache!!.get(key)
    }
}