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
class NyanView(internal val context: Context, scaleBy: Int) : SurfaceView(context), SurfaceHolder.Callback, OnSharedPreferenceChangeListener {

    /** Paint to draw with.  */
    private val paint = Paint()
    /** True iff dimensions have been setup.  */
    private var hasSetup: Boolean = false

    private var prefs: SharedPreferences? = null
    private var preferencesChanged: Boolean = false
    private var droidImage: String? = null
    private var rainbowImage: String? = null
    private var starImage: String? = null
    private var showDroid: Boolean = false
    private var showRainbow: Boolean = false
    private var showStars: Boolean = false
    private var maxDim: Int = 0
    private var animationSpeed: Int = 0
    private var sizeMod: Int = 0
    /** Animated NyanDroid.  */
    private var nyanDroid: NyanDroid? = null
    /** Animated rainbow.  */
    private var rainbow: Rainbow? = null
    /** Animated star field.  */
    private var stars: Stars? = null
    /** Count number of elapsed frames to time animations.  */
    private var frameCount: Int = 0

    private var thread: DrawingThread? = null

    init {
        paint.color = -0x1
        init(scaleBy)
    }

    private fun init(scaleBy: Int) {
        prefs = context.getSharedPreferences(NyanPaper.SHARED_PREFS_NAME, 0)
        prefs?.let {
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
                thread!!.setRunning(false)
                nyanDroid!!.recycle()
                stars!!.recycle()
                rainbow!!.recycle()
            }
        }.execute()
    }

    private fun setupAnimations() {
        maxDim = 64 * sizeMod

        val width = this.context.resources.displayMetrics.widthPixels
        maxDim = if (maxDim < width) maxDim else width - 64

        nyanDroid = NyanDroid(context, maxDim, paint, droidImage!!)

        // initialize Rainbow
        maxDim = (nyanDroid!!.frameHeight * .4).toInt()
        rainbow = Rainbow(context, maxDim, paint, rainbowImage!!)

        // remember offset for when drawing rainbows
        rainbow!!.setOffset(nyanDroid!!.frameWidth / 2 - rainbow!!.frameWidth)

        stars = Stars(context, maxDim, paint, starImage!!, animationSpeed)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        setupPrefs()
        preferencesChanged = true
    }

    private fun setupPrefs() {
        droidImage = prefs!!.getString("droid_image", "nyanwich")
        rainbowImage = prefs!!.getString("rainbow_image", "rainbow")
        starImage = prefs!!.getString("star_image", "white")
        sizeMod = prefs!!.getInt("size_mod", 5)
        animationSpeed = prefs!!.getInt("animation_speed", 3)
        showDroid = "none" != droidImage
        showRainbow = "none" != rainbowImage
        showStars = "none" != starImage
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = DrawingThread(getHolder(), this)
        thread!!.setRunning(true)
        thread!!.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d("NyanView", "Surface destroyed")
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

    override fun draw(c: Canvas?) {
        frameCount++
        if (c != null) {
            if (preferencesChanged) {
                setupAnimations()
                preferencesChanged = false
                //must reset centers
                hasSetup = false
            }

            if (!hasSetup) {
                rainbow!!.setCenter(c.width / 2, c.height / 2)
                nyanDroid!!.setCenter(c.width / 2, c.height / 2)
                hasSetup = true
            }

            c.drawColor(ContextCompat.getColor(context, R.color.nyanblue))
            if (showStars) {
                stars!!.draw(c)
            }

            val animateFrame = frameCount == 3

            if (showRainbow) {
                rainbow!!.draw(c, animateFrame)
            }

            if (showDroid) {
                nyanDroid!!.draw(c, animateFrame)
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