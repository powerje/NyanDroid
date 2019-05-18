package com.powerje.nyan.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.powerje.nyan.NyanUtils
import com.powerje.nyan.R

import java.util.ArrayList

class Rainbow(internal val mContext: Context, maxDim: Int, private val mPaint: Paint, image: String) {
    internal val mFrames: ArrayList<Bitmap>
    private var rainbowWidth: Int = 0

    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private var mOffset: Int = 0
    private var isBlank = false
    private var currentFrame: Int = 0

    val frameWidth: Int
        get() = if (isBlank) 0 else mFrames[0].width

    init {

        mFrames = ArrayList()
        if (image == "neapolitan") {
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                    R.drawable.neapolitan_rainbow_frame0, maxDim))
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                    R.drawable.neapolitan_rainbow_frame1, maxDim))
        } else if (image == "mono") {
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                    R.drawable.monochrome_rainbow_0, maxDim))
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                    R.drawable.monochrome_rainbow_1, maxDim))
        } else if (image == "rainbow") {
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                    R.drawable.rainbow_frame0, maxDim))
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(mContext,
                    R.drawable.rainbow_frame1, maxDim))
        } else {
            isBlank = true
        }

        if (!isBlank) {
            rainbowWidth = mFrames[0].width
        }
    }

    fun draw(c: Canvas, animate: Boolean) {
        synchronized(this) {
            if (isBlank) return


            val numberRainbowsFromCenter = c.width / 2 / rainbowWidth

            for (i in 0 until numberRainbowsFromCenter) {
                val toDraw = mFrames[(currentFrame + i % 2) % 2]
                c.drawBitmap(toDraw, (mCenterX - toDraw.width / 2 - mOffset
                        - i * rainbowWidth).toFloat(), (mCenterY - toDraw.height / 2).toFloat(),
                        mPaint)
            }
            if (animate) {
                currentFrame++
                currentFrame %= 2
            }
        }
    }

    fun setOffset(offset: Int) {
        mOffset = offset
    }

    fun setCenter(x: Int, y: Int) {
        mCenterX = x
        mCenterY = y
    }

    fun recycle() {
        synchronized(this) {
            isBlank = true
            for (b in mFrames) {
                b.recycle()
            }
        }
    }
}
