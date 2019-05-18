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
/**
 * Construct NyanDroid.
 *
 * @param c
 * context to be drawn in.
 * @param maxDim
 * maximize size of a side.
 * @param paint
 * used to draw.
 */
(
        /** Context NyanDroid is being drawn in.  */
        internal val mContext: Context, maxDim: Int,
        /** Paint with which to draw.  */
        private val mPaint: Paint, private val mDroid: String) {
    /** NyanDroid frames.  */
    internal val mFrames: ArrayList<Bitmap>

    /** Current y offset.  */
    private var yOffset: Int = 0
    /** True iff NyanDroid is moving upwards  */
    internal var mMovingUp: Boolean = false
    /** Center x coordinate.  */
    private var mCenterX: Int = 0
    /** Center y coordinate.  */
    private var mCenterY: Int = 0
    /** Current frame NyanDroid is in.  */
    private var currentFrame: Int = 0

    private var isBlank = false

    /**
     * @return the height of an individual frame.
     */
    val frameHeight: Int
        get() = if (isBlank) 256 else mFrames[0].height

    /**
     * @return the width of an individual frame.
     */
    val frameWidth: Int
        get() = if (isBlank) 256 else mFrames[0].width

    init {
        var maxDim = maxDim
        var repeatingFrame: Bitmap

        mFrames = ArrayList()

        if (mDroid == "droidtv") {
            repeatingFrame = NyanUtils.scaleWithRatio(mContext,
                    R.drawable.superman_gtv0, maxDim)
            mFrames.add(repeatingFrame)
            mFrames.add(repeatingFrame)
            mFrames.add(repeatingFrame)
            repeatingFrame = NyanUtils.scaleWithRatio(mContext,
                    R.drawable.superman_gtv1, maxDim)
            mFrames.add(repeatingFrame)
            mFrames.add(repeatingFrame)
            mFrames.add(repeatingFrame)
        } else if (mDroid == "ics_egg") {
            // hack because image sizes are different
            maxDim += 20
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid00, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid01, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid02, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid03, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid04, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid05, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid06, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid07, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid08, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid09, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid10, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.nyandroid11, maxDim))
        } else if (mDroid == "tardis") {
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.tardis,
                    maxDim))
        } else if (mDroid == "grump") {
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.grump_frame_0, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.grump_frame_1, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.grump_frame_2, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.grump_frame_3, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.grump_frame_4, maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.grump_frame_5, maxDim))
        } else if (mDroid == "nyanwich") {
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame0,
                    maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame1,
                    maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame2,
                    maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame3,
                    maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext,
                    R.drawable.superman0, maxDim))

            repeatingFrame = NyanUtils.scaleWithRatio(mContext,
                    R.drawable.superman1, maxDim)
            mFrames.add(repeatingFrame)
            mFrames.add(repeatingFrame)
            mFrames.add(repeatingFrame)

            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame4,
                    maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame5,
                    maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame6,
                    maxDim))
            mFrames.add(NyanUtils.scaleWithRatio(mContext, R.drawable.frame7,
                    maxDim))
        } else {
            // Setting up to return some default values so
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
            val toDraw = mFrames[currentFrame]
            c.drawBitmap(toDraw, (mCenterX - toDraw.width / 2).toFloat(),
                    (mCenterY - toDraw.height / 2 + yOffset).toFloat(), mPaint)
            if (animate) {
                currentFrame = if (currentFrame == mFrames.size - 1)
                    0
                else
                    currentFrame + 1

                if (mDroid != "ics_egg") {
                    if (mMovingUp) {
                        yOffset += 3
                        if (yOffset > 2)
                            mMovingUp = false
                    } else {
                        yOffset -= 3
                        if (yOffset < -2)
                            mMovingUp = true
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
