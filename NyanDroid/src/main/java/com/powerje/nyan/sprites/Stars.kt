package com.powerje.nyan.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.powerje.nyan.NyanUtils
import com.powerje.nyan.R

import java.util.ArrayList
import java.util.Random

class Stars(mContext: Context, maxDim: Int, private val mPaint: Paint, image: String, private val mSpeed: Int) {

    private val mLargeStars: ArrayList<Bitmap>
    private val mMediumStars: ArrayList<Bitmap>
    private val mSmallStars: ArrayList<Bitmap>

    private val stars = ArrayList<Star>()

    private val mRandom = Random()
    private var reverse = false
    private var isBlank = false

    private val whiteDrawables = intArrayOf(R.drawable.star0, R.drawable.star1, R.drawable.star2, R.drawable.star3, R.drawable.star4, R.drawable.star5, R.drawable.star6, R.drawable.star7, R.drawable.star8, R.drawable.star9)

    private val yellowDrawables = intArrayOf(R.drawable.yellow_star0, R.drawable.yellow_star1, R.drawable.yellow_star2, R.drawable.yellow_star3, R.drawable.yellow_star4, R.drawable.yellow_star5, R.drawable.yellow_star6, R.drawable.yellow_star7, R.drawable.yellow_star8, R.drawable.yellow_star9)

    private val noDrawable = intArrayOf(R.drawable.no)

    private val icsDrawables = intArrayOf(R.drawable.nyandroid00, R.drawable.nyandroid01, R.drawable.nyandroid02, R.drawable.nyandroid03, R.drawable.nyandroid04, R.drawable.nyandroid05, R.drawable.nyandroid06, R.drawable.nyandroid07, R.drawable.nyandroid08, R.drawable.nyandroid09, R.drawable.nyandroid10, R.drawable.nyandroid11)

    private val mNumberOfFrames: Int

    internal class Star {
        var x: Int = 0
        var y: Int = 0
        var frame: Int = 0
        var speed: Int = 0
        var width: Int = 0
        var stars: ArrayList<Bitmap>? = null
    }

    init {
        val drawables: IntArray

        var dimMod = 1
        when (image) {
            "white" -> drawables = whiteDrawables
            "yellow" -> drawables = yellowDrawables
            "no" -> {
                drawables = noDrawable
                // The 'no' can be overwhelming
                MAX_NEW_STARS = 1
            }
            else -> { //if (image.equals("ics_egg")) {
                drawables = icsDrawables
                reverse = true
                MAX_NEW_STARS = 1
                dimMod = 0
            }
        }

        // Add a little bit of crowd control for slow speeds
        if (mSpeed < 4)
            MAX_NEW_STARS = mSpeed

        mNumberOfFrames = drawables.size

        mLargeStars = ArrayList()
        for (i in drawables.indices) {
            mLargeStars.add(NyanUtils.scaleWithRatio(mContext, drawables[i],
                    maxDim / (dimMod + 1)))
        }

        mMediumStars = ArrayList()
        for (i in drawables.indices) {
            mMediumStars.add(NyanUtils.scaleWithRatio(mContext, drawables[i],
                    maxDim / (dimMod + 2)))
        }

        mSmallStars = ArrayList()
        for (i in drawables.indices) {
            mSmallStars.add(NyanUtils.scaleWithRatio(mContext, drawables[i],
                    maxDim / (dimMod + 3)))
        }
    }

    fun draw(c: Canvas) {
        synchronized(this) {
            if (isBlank) return

            var newStars = 0
            // create some arbitrary number of stars up to a given max
            while (mRandom.nextInt(100) > 40 && newStars < MAX_NEW_STARS) {
                // create new star
                val s = Star()
                if (reverse) {
                    s.x = -40
                } else {
                    s.x = c.width
                }
                s.y = mRandom.nextInt(c.height)
                s.frame = mRandom.nextInt(mNumberOfFrames)

                when (mRandom.nextInt(3)) {
                    0 -> {
                        s.speed = 10
                        s.width = mLargeStars[0].width
                        s.stars = mLargeStars
                    }
                    1 -> {
                        s.speed = 5
                        s.width = mMediumStars[0].width
                        s.stars = mMediumStars
                    }
                    else -> {
                        s.speed = 1
                        s.width = mSmallStars[0].width
                        s.stars = mSmallStars
                    }
                }

                s.speed += mSpeed * 5

                stars.add(s)
                newStars++
            }

            var i = 0
            while (i < stars.size) {
                val s = stars[i]
                c.drawBitmap(s.stars!![s.frame], s.x.toFloat(), s.y.toFloat(), mPaint)
                s.frame++
                s.frame %= mNumberOfFrames
                if (reverse) {
                    s.x += s.speed
                    if (s.x > c.width) {
                        stars.removeAt(i)
                        i--
                    }
                } else {
                    s.x -= s.speed
                    if (s.x < -c.width) {
                        stars.removeAt(i)
                        i--
                    }
                }
                i++
            }
        }
    }

    private fun recycleBitmapsInList(l: List<Bitmap>) {
        for (b in l) {
            b.recycle()
        }
    }

    fun recycle() {
        synchronized(this) {
            isBlank = true
            recycleBitmapsInList(mLargeStars)
            recycleBitmapsInList(mMediumStars)
            recycleBitmapsInList(mSmallStars)
        }
    }

    companion object {
        private var MAX_NEW_STARS = 5
    }
}
