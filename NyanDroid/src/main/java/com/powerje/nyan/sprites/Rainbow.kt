package com.powerje.nyan.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.powerje.nyan.NyanUtils
import com.powerje.nyan.R
import java.util.*

class Rainbow(mContext: Context, maxDim: Int, private val paint: Paint, image: String) {
    private val frames = ArrayList<Bitmap>()
    private var rainbowWidth: Int = 0

    private var centerX: Int = 0
    private var centerY: Int = 0
    private var offset: Int = 0
    private var isBlank = false
    private var currentFrame: Int = 0

    val frameWidth: Int
        get() = if (isBlank) 0 else frames[0].width

    init {

        when (image) {
            "neapolitan" -> {
                frames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                        R.drawable.neapolitan_rainbow_frame0, maxDim))
                frames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                        R.drawable.neapolitan_rainbow_frame1, maxDim))
            }
            "mono" -> {
                frames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                        R.drawable.monochrome_rainbow_0, maxDim))
                frames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                        R.drawable.monochrome_rainbow_1, maxDim))
            }
            "rainbow" -> {
                frames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                        R.drawable.rainbow_frame0, maxDim))
                frames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                        R.drawable.rainbow_frame1, maxDim))
            }
            else -> isBlank = true
        }

        if (!isBlank) {
            rainbowWidth = frames[0].width
        }
    }

    fun draw(c: Canvas, animate: Boolean) {
        if (isBlank) return

        val numberRainbowsFromCenter = c.width / 2 / rainbowWidth

        for (i in 0 until numberRainbowsFromCenter) {
            val toDraw = frames[(currentFrame + i % 2) % 2]
            c.drawBitmap(toDraw, (centerX - toDraw.width / 2 - offset
                    - i * rainbowWidth).toFloat(), (centerY - toDraw.height / 2).toFloat(),
                    paint)
        }
        if (animate) {
            currentFrame++
            currentFrame %= 2
        }
    }

    fun setOffset(offset: Int) {
        this.offset = offset
    }

    fun setCenter(x: Int, y: Int) {
        centerX = x
        centerY = y
    }
}
