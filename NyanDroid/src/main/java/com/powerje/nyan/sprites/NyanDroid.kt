package com.powerje.nyan.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.powerje.nyan.NyanUtils
import com.powerje.nyan.R

import java.util.ArrayList

/**
 * NyanDroid is a class for drawing an animated nyan droid.
 *
 * @author powerj
 */
class NyanDroid
(
        /** Context NyanDroid is being drawn in.  */
        context: Context, maxDim: Int,
        /** Paint with which to draw.  */
        private val paint: Paint, private val droid: String) {
    /** NyanDroid frames.  */
    private val frames = ArrayList<Bitmap>()

    /** Current y offset.  */
    private var yOffset: Int = 0
    /** True iff NyanDroid is moving upwards  */
    private var isMovingUp: Boolean = false
    /** Center x coordinate.  */
    private var centerX: Int = 0
    /** Center y coordinate.  */
    private var centerY: Int = 0
    /** Current frame NyanDroid is in.  */
    private var currentFrame: Int = 0

    private var isBlank = false

    /**
     * @return the height of an individual frame.
     */
    val frameHeight: Int
        get() = if (isBlank) 256 else frames[0].height

    /**
     * @return the width of an individual frame.
     */
    val frameWidth: Int
        get() = if (isBlank) 256 else frames[0].width

    init {
        var maxDim = maxDim
        var repeatingFrame: Bitmap

        when (droid) {
            "droidtv" -> {
                repeatingFrame = NyanUtils.scaleWithRatio(context,
                        R.drawable.superman_gtv0, maxDim)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                repeatingFrame = NyanUtils.scaleWithRatio(context,
                        R.drawable.superman_gtv1, maxDim)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
            }
            "ics_egg" -> {
                // hack because image sizes are different
                maxDim += 20
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid00, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid01, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid02, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid03, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid04, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid05, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid06, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid07, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid08, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid09, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid10, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid11, maxDim))
            }
            "tardis" -> frames.add(NyanUtils.scaleWithRatio(context, R.drawable.tardis,
                    maxDim))
            "grump" -> {
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_0, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_1, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_2, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_3, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_4, maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_5, maxDim))
            }
            "nyanwich" -> {
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame0,
                        maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame1,
                        maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame2,
                        maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame3,
                        maxDim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.superman0, maxDim))

                repeatingFrame = NyanUtils.scaleWithRatio(context,
                        R.drawable.superman1, maxDim)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)

                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame4,
                        maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame5,
                        maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame6,
                        maxDim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame7,
                        maxDim))
            }
            else -> // Setting up to return some default values so
                // weirdos who disappear this but still have
                // the rainbow have it centered properly etc
                isBlank = true
        }
    }

    /**
     * Draw NyanDroid.
     *
     * @param c
     * canvas to draw on.
     * @param animate
     * move to next frame after drawing.
     */
    fun draw(c: Canvas, animate: Boolean) {
        synchronized(this) {
            val toDraw = frames[currentFrame]
            c.drawBitmap(toDraw, (centerX - toDraw.width / 2).toFloat(),
                    (centerY - toDraw.height / 2 + yOffset).toFloat(), paint)
            if (animate) {
                currentFrame = if (currentFrame == frames.size - 1)
                    0
                else
                    currentFrame + 1

                if (droid != "ics_egg") {
                    if (isMovingUp) {
                        yOffset += 3
                        if (yOffset > 2)
                            isMovingUp = false
                    } else {
                        yOffset -= 3
                        if (yOffset < -2)
                            isMovingUp = true
                    }
                }
            }
        }
    }

    /**
     * Set the coordinates with which to center the drawing.
     *
     * @param x
     * center x coordinate
     * @param y
     * center y coordinate
     */
    fun setCenter(x: Int, y: Int) {
        centerX = x
        centerY = y
    }

    fun recycle() {
        synchronized(this) {
            isBlank = true
            for (b in frames) {
                b.recycle()
            }
        }
    }
}
