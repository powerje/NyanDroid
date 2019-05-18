package com.powerje.nyan

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Canvas
import android.graphics.Paint
import android.os.AsyncTask
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat

import com.powerje.nyan.sprites.NyanDroid
import com.powerje.nyan.sprites.Rainbow
import com.powerje.nyan.sprites.Stars

/**
 * NyanView draws NyanDroid flying through space distributing Ice Cream Nyanwich.
 * @author powerj
 */
class NyanView(private val mContext: Context, scaleBy: Int) : SurfaceView(mContext), SurfaceHolder.Callback, OnSharedPreferenceChangeListener {

    /** Paint to draw with.  */
    private val mPaint = Paint()
    /** True iff dimensions have been setup.  */
    private var hasSetup: Boolean = false

    private var mPrefs: SharedPreferences? = null
    private var mPreferencesChanged: Boolean = false
    private var mDroidImage: String? = null
    private var mRainbowImage: String? = null
    private var mStarImage: String? = null
    private var mShowDroid: Boolean = false
    private var mShowRainbow: Boolean = false
    private var mShowStars: Boolean = false
    private var mMaxDim: Int = 0
    private var mAnimationSpeed: Int = 0
    private var mSizeMod: Int = 0
    /** Animated NyanDroid.  */
    private var mNyanDroid: NyanDroid? = null
    /** Animated rainbow.  */
    private var mRainbow: Rainbow? = null
    /** Animated star field.  */
    private var mStars: Stars? = null
    /** Count number of elapsed frames to time animations.  */
    private var frameCount: Int = 0

    private var mThread: DrawingThread? = null

    init {
        mPaint.color = -0x1
        init(scaleBy)
    }

    private fun init(scaleBy: Int) {
        mPrefs = mContext.getSharedPreferences(NyanPaper.SHARED_PREFS_NAME, 0)
        mPrefs?.let {
            it.registerOnSharedPreferenceChangeListener(this)
            onSharedPreferenceChanged(it, null)
        }

        setupPrefs()
        holder.addCallback(this)
        hasSetup = false
        setupAnimations()
    }

    fun cancel() {
        // Lazy way to ensure in post 4.0 that this executes after the asynctask in the start method
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                mThread!!.setRunning(false)
                mNyanDroid!!.recycle()
                mStars!!.recycle()
                mRainbow!!.recycle()
            }
        }.execute()
    }

    private fun setupAnimations() {
        mMaxDim = 64 * mSizeMod

        val width = this.context.resources.displayMetrics.widthPixels
        mMaxDim = if (mMaxDim < width) mMaxDim else width - 64

        mNyanDroid = NyanDroid(mContext, mMaxDim, mPaint, mDroidImage!!)

        // initialize Rainbow
        mMaxDim = (mNyanDroid!!.frameHeight * .4).toInt()
        mRainbow = Rainbow(mContext, mMaxDim, mPaint, mRainbowImage!!)

        // remember offset for when drawing rainbows
        mRainbow!!.setOffset(mNyanDroid!!.frameWidth / 2 - mRainbow!!.frameWidth)

        mStars = Stars(mContext, mMaxDim, mPaint, mStarImage!!, mAnimationSpeed)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        setupPrefs()
        mPreferencesChanged = true
    }

    private fun setupPrefs() {
        mDroidImage = mPrefs!!.getString("droid_image", "nyanwich")
        mRainbowImage = mPrefs!!.getString("rainbow_image", "rainbow")
        mStarImage = mPrefs!!.getString("star_image", "white")
        mSizeMod = mPrefs!!.getInt("size_mod", 5)
        mAnimationSpeed = mPrefs!!.getInt("animation_speed", 3)
        mShowDroid = "none" != mDroidImage
        mShowRainbow = "none" != mRainbowImage
        mShowStars = "none" != mStarImage
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mThread = DrawingThread(getHolder(), this)
        mThread!!.setRunning(true)
        mThread!!.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d("NyanView", "Surface destroyed")
        var retry = true
        mThread!!.setRunning(false)
        while (retry) {
            try {
                mThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

    override fun draw(c: Canvas?) {
        frameCount++
        if (c != null) {
            if (mPreferencesChanged) {
                setupAnimations()
                mPreferencesChanged = false
                //must reset centers
                hasSetup = false
            }

            if (!hasSetup) {
                mRainbow!!.setCenter(c.width / 2, c.height / 2)
                mNyanDroid!!.setCenter(c.width / 2, c.height / 2)
                hasSetup = true
            }

            c.drawColor(ContextCompat.getColor(mContext, R.color.nyanblue))
            if (mShowStars) {
                mStars!!.draw(c)
            }

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

    class DrawingThread(private val myThreadSurfaceHolder: SurfaceHolder, private val myThreadSurfaceView: NyanView) : Thread() {
        private var myThreadRun = false

        fun setRunning(b: Boolean) {
            myThreadRun = b
        }

        override fun run() {
            while (myThreadRun) {
                var c: Canvas? = null
                try {
                    c = myThreadSurfaceHolder.lockCanvas(null)
                    synchronized(myThreadSurfaceHolder) {
                        myThreadSurfaceView.draw(c)
                    }

                    sleep((1000 / 30).toLong())

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    if (c != null) {
                        myThreadSurfaceHolder.unlockCanvasAndPost(c)
                    }
                }
            }
        }
    }
}