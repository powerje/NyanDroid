package com.powerje.nyan

import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.os.AsyncTask
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat

import com.powerje.nyan.sprites.NyanDroid
import com.powerje.nyan.sprites.Rainbow
import com.powerje.nyan.sprites.Stars
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NyanPaper : WallpaperService() {
    private val droidHandler = Handler()

    override fun onCreateEngine(): Engine {
        return NyanEngine()
    }

    internal inner class NyanEngine : WallpaperService.Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        private val paint = Paint().apply {
            color = -0x1
        }

        private var visible: Boolean = false
        private var hasCenteredImages: Boolean = false
        private var hasLoadedImages: Boolean = false
        private val prefs: SharedPreferences
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

        private val drawFrame = Runnable { drawFrame() }

        private var showDroid: Boolean = false
        private var showRainbow: Boolean = false
        private var showStars: Boolean = false

        init {
            prefs = this@NyanPaper.getSharedPreferences(getString(R.string.shared_preferences_name), 0)
            prefs.registerOnSharedPreferenceChangeListener(this)
            onSharedPreferenceChanged(prefs, null)
            setupPrefs()
        }

        override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
            Log.d(TAG, "prefs changed")
            setupPrefs()
            preferencesChanged = true
        }

        private fun setupPrefs() {
            droidImage = prefs.getString("droid_image", "nyanwich")
            rainbowImage = prefs.getString("rainbow_image", "rainbow")
            starImage = prefs.getString("star_image", "white")
            sizeMod = prefs.getInt("size_mod", 5) + 1
            animationSpeed = prefs.getInt("animation_speed", 3)

            showDroid = "none" != droidImage
            showRainbow = "none" != rainbowImage
            showStars = "none" != starImage
        }

        override fun onDestroy() {
            super.onDestroy()
            droidHandler.removeCallbacks(drawFrame)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                drawFrame()
            } else {
                droidHandler.removeCallbacks(drawFrame)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            hasCenteredImages = false
            preferencesChanged = true
        }

        private fun setupAnimations() {
            hasLoadedImages = false
            GlobalScope.launch {
                val c = applicationContext
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
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            droidHandler.removeCallbacks(drawFrame)
        }

        /**
         * Draw a single animation frame.
         */
        private fun drawFrame() {
            val holder = surfaceHolder

            if (preferencesChanged) {
                setupAnimations()
                preferencesChanged = false
                hasCenteredImages = false
            }

            var c: Canvas? = null
            try {
                c = holder.lockCanvas()
                synchronized(holder) {
                    frameCount++
                    if (c != null && hasLoadedImages) {
                        if (!hasCenteredImages) {
                            rainbow!!.setCenter(c.width / 2,
                                    c.height / 2)
                            nyanDroid!!.setCenter(c.width / 2,
                                    c.height / 2)
                            hasCenteredImages = true
                        }

                        c.drawColor(ContextCompat.getColor(this@NyanPaper, R.color.nyanblue))

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
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c)
            }

            // Reschedule the next redraw
            droidHandler.removeCallbacks(drawFrame)
            if (visible) {
                droidHandler.postDelayed(drawFrame, 0)
            }
        }
    }

    companion object {
        private const val TAG = "NyanPaper"
    }
}