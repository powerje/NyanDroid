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

    private val largeStars: ArrayList<Bitmap>
    private val mediumStars: ArrayList<Bitmap>
    private val smallStars: ArrayList<Bitmap>
    private val stars = ArrayList<Star>()

    private val random = Random()
    private var reverse = false
    private var isBlank = false

    private val whiteDrawables = intArrayOf(R.drawable.star0, R.drawable.star1, R.drawable.star2, R.drawable.star3, R.drawable.star4, R.drawable.star5, R.drawable.star6, R.drawable.star7, R.drawable.star8, R.drawable.star9)
    private val yellowDrawables = intArrayOf(R.drawable.yellow_star0, R.drawable.yellow_star1, R.drawable.yellow_star2, R.drawable.yellow_star3, R.drawable.yellow_star4, R.drawable.yellow_star5, R.drawable.yellow_star6, R.drawable.yellow_star7, R.drawable.yellow_star8, R.drawable.yellow_star9)
    private val noDrawable = intArrayOf(R.drawable.no)
    private val icsDrawables = intArrayOf(R.drawable.nyandroid00, R.drawable.nyandroid01, R.drawable.nyandroid02, R.drawable.nyandroid03, R.drawable.nyandroid04, R.drawable.nyandroid05, R.drawable.nyandroid06, R.drawable.nyandroid07, R.drawable.nyandroid08, R.drawable.nyandroid09, R.drawable.nyandroid10, R.drawable.nyandroid11)

    private val NUMBER_OF_FRAMES: Int
    private val MAX_TOTAL_STARS: Int
    private val WEIGHTED_NEW_STAR_COUNT = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 3, 4, 5)

    internal class Star {
        var x: Float = 0f
        var y: Float = 0f
        var frame: Int = 0
        var speed: Int = 0
        var width: Int = 0
        var stars: ArrayList<Bitmap>? = null
    }

    init {
        val drawables: IntArray

        var dimMod = 1
        when (image) {
            "white" -> {
                drawables = whiteDrawables
                MAX_TOTAL_STARS = 100
            }
            "yellow" -> {
                drawables = yellowDrawables
                MAX_TOTAL_STARS = 100
            }
            "no" -> {
                drawables = noDrawable
                MAX_TOTAL_STARS = 60
            }
            "ics_egg" -> {
                drawables = icsDrawables
                reverse = true
                dimMod = 0
                MAX_TOTAL_STARS = 40
            }
            else -> {
                drawables = intArrayOf()
                MAX_TOTAL_STARS = 0
            }
        }

        NUMBER_OF_FRAMES = drawables.size

        largeStars = ArrayList()
        for (i in drawables.indices) {
            largeStars.add(NyanUtils.scaleWithRatio(mContext, drawables[i],
                    maxDim / (dimMod + 1)))
        }

        mediumStars = ArrayList()
        for (i in drawables.indices) {
            mediumStars.add(NyanUtils.scaleWithRatio(mContext, drawables[i],
                    maxDim / (dimMod + 2)))
        }

        smallStars = ArrayList()
        for (i in drawables.indices) {
            smallStars.add(NyanUtils.scaleWithRatio(mContext, drawables[i],
                    maxDim / (dimMod + 3)))
        }
    }

    fun draw(c: Canvas) {
        if (isBlank) return

        val newStarCount = WEIGHTED_NEW_STAR_COUNT.random()
        for (i in 0..newStarCount) {
            if (stars.count() >= MAX_TOTAL_STARS) break

            // create new star
            val s = Star()
            if (reverse) {
                s.x = -40f
            } else {
                s.x = c.width.toFloat()
            }
            s.y = random.nextInt(c.height).toFloat()
            s.frame = random.nextInt(NUMBER_OF_FRAMES)

            when (random.nextInt(3)) {
                0 -> {
                    s.speed = 10
                    s.width = largeStars[0].width
                    s.stars = largeStars
                }
                1 -> {
                    s.speed = 5
                    s.width = mediumStars[0].width
                    s.stars = mediumStars
                }
                else -> {
                    s.speed = 1
                    s.width = smallStars[0].width
                    s.stars = smallStars
                }
            }

            s.speed += speed * 5

            stars.add(s)
        }

        var i = 0
        while (i < stars.size) {
            val s = stars[i]
            c.drawBitmap(s.stars!![s.frame], s.x, s.y, paint)
            s.frame++
            s.frame %= NUMBER_OF_FRAMES
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
