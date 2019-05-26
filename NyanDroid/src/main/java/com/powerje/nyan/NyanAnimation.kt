package com.powerje.nyan

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat
import com.powerje.nyan.sprites.NyanDroid
import com.powerje.nyan.sprites.Rainbow
import com.powerje.nyan.sprites.Stars

class NyanAnimation(private val sharedPreferences: SharedPreferences, private val context: Context, private val holder: SurfaceHolder) : SharedPreferences.OnSharedPreferenceChangeListener, SurfaceHolder.Callback {
    private val paint = Paint().apply {
        color = -0x1
    }

    private var hasCenteredImages: Boolean = false
    private var hasLoadedImages: Boolean = false
    private var preferencesChanged: Boolean = false

    private var nyanDroid: NyanDroid? = null
    private var rainbow: Rainbow? = null
    private var stars: Stars? = null

    private var droidImage: String? = null
    private var rainbowImage: String? = null
    private var starImage: String? = null

    private var animationSpeed: Int = 0
    private var sizeMod: Int = 0
    private var maxDim: Int = 0

    private var frameCount: Int = 0

    private var showDroid: Boolean = false
    private var showRainbow: Boolean = false
    private var showStars: Boolean = false
    private var thread: DrawingThread? = null

    private var visible = false

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        onSharedPreferenceChanged(sharedPreferences, null)
        holder.addCallback(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        droidImage = sharedPreferences.getString("droid_image", "nyanwich")
        rainbowImage = sharedPreferences.getString("rainbow_image", "rainbow")
        starImage = sharedPreferences.getString("star_image", "white")
        sizeMod = sharedPreferences.getInt("size_mod", 5) + 1
        animationSpeed = sharedPreferences.getInt("animation_speed", 3)

        showDroid = "none" != droidImage
        showRainbow = "none" != rainbowImage
        showStars = "none" != starImage
        preferencesChanged = true
    }

    fun onVisibilityChanged(visible: Boolean) {
        this.visible = visible
        if (visible) {
            thread = DrawingThread(this)
            thread!!.setRunning(true)
            thread!!.start()
        } else {
            thread?.setRunning(false)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        hasCenteredImages = false
        preferencesChanged = true
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        onVisibilityChanged(false)
        var retry = true
        thread!!.setRunning(false)
        while (retry) {
            try {
                thread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

    private fun setupAnimations() {
        hasLoadedImages = false

        val c = context
        maxDim = 64 * sizeMod
        val width = c.resources.displayMetrics.widthPixels
        maxDim = if (maxDim < width) maxDim else width - 64
        nyanDroid = NyanDroid(c, maxDim, paint, droidImage!!)

        // initialize Rainbow
        maxDim = (nyanDroid!!.frameHeight * .4).toInt()
        rainbow = Rainbow(c, maxDim, paint, rainbowImage!!)

        // remember offset for when drawing rainbows
        rainbow!!.setOffset(nyanDroid!!.frameWidth / 2 - rainbow!!.frameWidth)
        stars = Stars(c, maxDim, paint, starImage!!, animationSpeed)

        hasLoadedImages = true
    }

    private fun drawFrame(c: Canvas?) {
        if (!visible) return

        if (preferencesChanged) {
            setupAnimations()
            preferencesChanged = false
            hasCenteredImages = false
        }

        frameCount++
        if (c != null && hasLoadedImages) {
            if (!hasCenteredImages) {
                rainbow!!.setCenter(c.width / 2,
                        c.height / 2)
                nyanDroid!!.setCenter(c.width / 2,
                        c.height / 2)
                hasCenteredImages = true
            }

            c.drawColor(ContextCompat.getColor(context, R.color.nyanblue))

            if (showStars) {
                stars!!.draw(c, frameCount % 3 == 0)
            }

            if (showRainbow) {
                rainbow!!.draw(c, frameCount % 12 == 0)
            }

            if (showDroid) {
                nyanDroid!!.draw(c, frameCount % 6 == 0)
            }
        }
        frameCount %= 24
    }

    class DrawingThread(private val nyanAnimation: NyanAnimation) : Thread() {
        private var myThreadRun = false

        fun setRunning(b: Boolean) {
            myThreadRun = b
        }

        override fun run() {
            while (myThreadRun) {
                var c: Canvas? = null
                try {
                    c = nyanAnimation.holder.lockCanvas(null)
                    synchronized(nyanAnimation.holder) {
                        nyanAnimation.drawFrame(c)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    if (c != null) {
                        nyanAnimation.holder.unlockCanvasAndPost(c)
                    }
                }
            }
        }
    }
}