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
(context: Context, maxDim: Int, private val paint: Paint, private val droid: String) {
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
        var dim = maxDim
        var repeatingFrame: Bitmap

        when (droid) {
            "droidtv" -> {
                repeatingFrame = NyanUtils.scaleWithRatio(context,
                        R.drawable.superman_gtv0, dim)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                repeatingFrame = NyanUtils.scaleWithRatio(context,
                        R.drawable.superman_gtv1, dim)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
            }
            "ics_egg" -> {
                // hack because image sizes are different
                dim += 20
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid00, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid01, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid02, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid03, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid04, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid05, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid06, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid07, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid08, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid09, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid10, dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.nyandroid11, dim))
            }
            "tardis" -> frames.add(NyanUtils.scaleWithRatio(context, R.drawable.tardis,
                    dim))
            "grump" -> {
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_0, dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_1, dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_2, dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_3, dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_4, dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.grump_frame_5, dim))
            }
            "nyanwich" -> {
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame0,
                        dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame1,
                        dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame2,
                        dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame3,
                        dim))
                frames.add(NyanUtils.scaleWithRatio(context,
                        R.drawable.superman0, dim))

                repeatingFrame = NyanUtils.scaleWithRatio(context,
                        R.drawable.superman1, dim)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)
                frames.add(repeatingFrame)

                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame4,
                        dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame5,
                        dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame6,
                        dim))
                frames.add(NyanUtils.scaleWithRatio(context, R.drawable.frame7,
                        dim))
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
        if (isBlank) return

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
}
