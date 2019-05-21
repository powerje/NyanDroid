package com.powerje.nyan.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.powerje.nyan.NyanUtils
import com.powerje.nyan.R

import java.util.ArrayList
import java.util.Random

class Stars(mContext: Context, maxDim: Int, private val paint: Paint, image: String, internal val speed: Int) {
    private val largeStarFrames: ArrayList<Bitmap>
    private val mediumStarFrames: ArrayList<Bitmap>
    private val smallStarFrames: ArrayList<Bitmap>

    private val stars = ArrayList<Star>()
    private val reusableStars = ArrayList<Star>()

    private val random = Random()
    private var reverse = false
    private var isBlank = false

    private val whiteDrawableFrames = intArrayOf(R.drawable.star0, R.drawable.star1, R.drawable.star2, R.drawable.star3, R.drawable.star4, R.drawable.star5, R.drawable.star6, R.drawable.star7, R.drawable.star8, R.drawable.star9)
    private val yellowDrawableFrames = intArrayOf(R.drawable.yellow_star0, R.drawable.yellow_star1, R.drawable.yellow_star2, R.drawable.yellow_star3, R.drawable.yellow_star4, R.drawable.yellow_star5, R.drawable.yellow_star6, R.drawable.yellow_star7, R.drawable.yellow_star8, R.drawable.yellow_star9)
    private val noDrawableFrame = intArrayOf(R.drawable.no)
    private val icsDrawableFrames = intArrayOf(R.drawable.nyandroid00, R.drawable.nyandroid01, R.drawable.nyandroid02, R.drawable.nyandroid03, R.drawable.nyandroid04, R.drawable.nyandroid05, R.drawable.nyandroid06, R.drawable.nyandroid07, R.drawable.nyandroid08, R.drawable.nyandroid09, R.drawable.nyandroid10, R.drawable.nyandroid11)

    private val NUMBER_OF_FRAMES: Int
    private var MAX_TOTAL_STARS: Int
    private val WEIGHTED_NEW_STAR_COUNT = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2)

    internal class Star {
        var x: Float = 0f
        var y: Float = 0f
        var frame: Int = 0
        var speed: Int = 0
        var width: Int = 0
        var frames: ArrayList<Bitmap>? = null
    }

    init {
        val drawables: IntArray

        var dimMod = 1
        when (image) {
            "white" -> {
                drawables = whiteDrawableFrames
                MAX_TOTAL_STARS = 100
            }
            "yellow" -> {
                drawables = yellowDrawableFrames
                MAX_TOTAL_STARS = 100
            }
            "no" -> {
                drawables = noDrawableFrame
                MAX_TOTAL_STARS = 30
            }
            "ics_egg" -> {
                drawables = icsDrawableFrames
                reverse = true
                dimMod = 0
                MAX_TOTAL_STARS = 250
            }
            else -> {
                drawables = intArrayOf()
                MAX_TOTAL_STARS = 0
            }
        }
        NUMBER_OF_FRAMES = drawables.size

        largeStarFrames = ArrayList()
        for (i in drawables.indices) {
            largeStarFrames.add(NyanUtils.scaleWithRatio(mContext, drawables[i], maxDim / (dimMod + 1)))
        }

        mediumStarFrames = ArrayList()
        for (i in drawables.indices) {
            mediumStarFrames.add(NyanUtils.scaleWithRatio(mContext, drawables[i], maxDim / (dimMod + 2)))
        }

        smallStarFrames = ArrayList()
        for (i in drawables.indices) {
            smallStarFrames.add(NyanUtils.scaleWithRatio(mContext, drawables[i], maxDim / (dimMod + 3)))
        }
    }

    fun draw(c: Canvas, animate: Boolean) {
        if (isBlank) return

        if (animate) {
            addStars(c)
        }

        var i = 0
        while (i < stars.size) {
            val s = stars[i]
            c.drawBitmap(s.frames!![s.frame], s.x, s.y, paint)

            if (animate) {
                s.frame++
                s.frame %= NUMBER_OF_FRAMES
            }
            if (reverse) {
                s.x += s.speed
                if (s.x > c.width) {
                    val star = stars.removeAt(i)
                    addReusableStarIfSpaceAvailable(star)
                    i--
                }
            } else {
                s.x -= s.speed
                if (s.x < -c.width) {
                    val star = stars.removeAt(i)
                    addReusableStarIfSpaceAvailable(star)
                    i--
                }
            }

            i++
        }
    }

    private fun addStars(c: Canvas) {
        val newStarCount = WEIGHTED_NEW_STAR_COUNT.random()
        for (i in 0..newStarCount) {
            if (stars.count() >= MAX_TOTAL_STARS) break

            val s: Star
            if (reusableStars.isNotEmpty()) {
                s = reusableStars.removeAt(0)
            } else {
                s = Star()
                s.frame = random.nextInt(NUMBER_OF_FRAMES)
                when (random.nextInt(3)) {
                    0 -> {
                        s.speed = random.nextInt(10)
                        s.width = largeStarFrames[0].width
                        s.frames = largeStarFrames
                    }
                    1 -> {
                        s.speed = random.nextInt(5)
                        s.width = mediumStarFrames[0].width
                        s.frames = mediumStarFrames
                    }
                    else -> {
                        s.speed = 1
                        s.width = smallStarFrames[0].width
                        s.frames = smallStarFrames
                    }
                }
                s.speed += speed * 5
            }

            if (reverse) {
                s.x = -(s.frames!![s.frame].width).toFloat()
            } else {
                s.x = c.width.toFloat()
            }
            s.y = random.nextInt(c.height).toFloat()

            stars.add(s)
        }
    }

    private fun addReusableStarIfSpaceAvailable(star: Star) {
        if (reusableStars.count() < MAX_TOTAL_STARS) {
            reusableStars.add(star)
        }
    }
}
