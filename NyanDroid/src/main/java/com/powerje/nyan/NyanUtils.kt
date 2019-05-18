package com.powerje.nyan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix

object NyanUtils {

    fun maxHeightResourceToBitmap(c: Context, res: Int,
                                  maxHeight: Int): Bitmap {
        val bmp = imageResourceToBitmap(c, res, maxHeight)

        val width = bmp!!.width
        val height = bmp.height

        val newWidth = maxHeight / 2

        // calculate the scale
        val scaleHeight = maxHeight.toFloat() / height
        val scaleWidth = newWidth.toFloat() / width

        // create a matrix for the manipulation
        val matrix = Matrix()
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)

        // recreate the new Bitmap and return it
        return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true)
    }

    fun scaleWithRatio(c: Context, res: Int, max: Int): Bitmap {
        val opts = BitmapFactory.Options()
        opts.inPurgeable = true
        opts.inSampleSize = 2
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.resources, res, opts), max, max, false)
    }

    private fun imageResourceToBitmap(c: Context, res: Int, maxDim: Int): Bitmap? {
        var bmp: Bitmap? = null
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        // compute the smallest size bitmap we need to read
        BitmapFactory.decodeResource(c.resources, res, opts)
        var w = opts.outWidth
        var h = opts.outHeight
        var s = 1
        while (true) {
            if (w / 2 < maxDim || h / 2 < maxDim) {
                break
            }
            w /= 2
            h /= 2
            s++
        }
        // scale and read the data
        opts.inJustDecodeBounds = false
        opts.inPurgeable = true
        opts.inSampleSize = s
        bmp = BitmapFactory.decodeResource(c.resources, res, opts)
        return bmp
    }

}
