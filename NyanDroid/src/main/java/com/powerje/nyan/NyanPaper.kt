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

class NyanPaper : WallpaperService() {
    private val mDroidHandler = Handler()

    override fun onCreateEngine(): Engine {
        return NyanEngine()
    }

    internal inner class NyanEngine : WallpaperService.Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        private val mPaint = Paint()

        private var mVisible: Boolean = false
        private var hasCenteredImages: Boolean = false
        private var hasLoadedImages: Boolean = false
        private val mPrefs: SharedPreferences
        private var mPreferencesChanged: Boolean = false

        private var mNyanDroid: NyanDroid? = null
        private var mRainbow: Rainbow? = null
        private var mStars: Stars? = null

        private var mDroidImage: String? = null
        private var mRainbowImage: String? = null
        private var mStarImage: String? = null

        private var mAnimationSpeed: Int = 0
        private var mSizeMod: Int = 0
        private var mMaxDim: Int = 0


        private var frameCount: Int = 0

        private val mDrawFrame = Runnable { drawFrame() }

        private var mShowDroid: Boolean = false
        private var mShowRainbow: Boolean = false
        private var mShowStars: Boolean = false

        init {
            mPaint.color = -0x1
            mPrefs = this@NyanPaper.getSharedPreferences(SHARED_PREFS_NAME, 0)
            mPrefs.registerOnSharedPreferenceChangeListener(this)
            onSharedPreferenceChanged(mPrefs, null)
            setupPrefs()
        }

        override fun onSharedPreferenceChanged(prefs: SharedPreferences,
                                               key: String?) {
            Log.d(TAG, "prefs changed")
            setupPrefs()
            mPreferencesChanged = true
        }

        private fun setupPrefs() {
            mDroidImage = mPrefs.getString("droid_image", "nyanwich")
            mRainbowImage = mPrefs.getString("rainbow_image", "rainbow")
            mStarImage = mPrefs.getString("star_image", "white")
            mSizeMod = mPrefs.getInt("size_mod", 5)
            mAnimationSpeed = mPrefs.getInt("animation_speed", 3)

            mShowDroid = "none" != mDroidImage
            mShowRainbow = "none" != mRainbowImage
            mShowStars = "none" != mStarImage
        }

        override fun onDestroy() {
            super.onDestroy()
            mDroidHandler.removeCallbacks(mDrawFrame)
        }


        override fun onVisibilityChanged(visible: Boolean) {
            mVisible = visible
            if (visible) {
                drawFrame()
            } else {
                mDroidHandler.removeCallbacks(mDrawFrame)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int,
                                      width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            mWidth = width
            hasCenteredImages = false
            mPreferencesChanged = true
        }

        private fun setupAnimations() {
            hasLoadedImages = false
            object : AsyncTask<Void?, Void?, Void?>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    val c = applicationContext
                    mMaxDim = 64 * mSizeMod
                    val width = c.resources.displayMetrics.widthPixels
                    mMaxDim = if (mMaxDim < width) mMaxDim else width - 64
                    mNyanDroid = NyanDroid(c, mMaxDim, mPaint, mDroidImage!!)

                    // initialize Rainbow
                    mMaxDim = (mNyanDroid!!.frameHeight * .4).toInt()
                    mRainbow = Rainbow(c, mMaxDim, mPaint, mRainbowImage!!)

                    // remember offset for when drawing rainbows
                    mRainbow!!.setOffset(mNyanDroid!!.frameWidth / 2 - mRainbow!!.frameWidth)

                    mStars = Stars(c, mMaxDim, mPaint, mStarImage!!, mAnimationSpeed)
                    return null
                }

                override fun onPostExecute(aVoid: Void?) {
                    hasLoadedImages = true
                }

            }.execute()

        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            mVisible = false
            mDroidHandler.removeCallbacks(mDrawFrame)
        }

        /**
         * Draw a single animation frame.
         */
        private fun drawFrame() {
            val holder = surfaceHolder

            if (mPreferencesChanged) {
                setupAnimations()
                mPreferencesChanged = false
                //must reset centers
                hasCenteredImages = false
            }

            var c: Canvas? = null
            try {
                c = holder.lockCanvas()
                synchronized(holder) {
                    frameCount++
                    if (c != null && hasLoadedImages) {
                        if (!hasCenteredImages) {
                            mRainbow!!.setCenter(c.width / 2,
                                    c.height / 2)
                            mNyanDroid!!.setCenter(c.width / 2,
                                    c.height / 2)
                            hasCenteredImages = true
                        }

                        c.drawColor(ContextCompat.getColor(this@NyanPaper, R.color.nyanblue))

                        if (mShowStars) {
                            mStars!!.draw(c)
                        }

                        // This is ugly and dumb
                        val animateFrame = frameCount == 3

                        if (mShowRainbow) {
                            mRainbow!!.draw(c, animateFrame)
                        }

                        if (mShowDroid) {
                            mNyanDroid!!.draw(c, animateFrame)
                        }

                    }
                    frameCount %= 3
                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c)
            }

            // Reschedule the next redraw
            mDroidHandler.removeCallbacks(mDrawFrame)
            if (mVisible) {
                // approx 30 fps
                mDroidHandler.postDelayed(mDrawFrame, (1000 / 30).toLong())
            }
        }
    }

    companion object {
        const val SHARED_PREFS_NAME = "nyandroidsettings"
        private const val TAG = "NyanPaper"
        private var mWidth: Int = 0
    }
}