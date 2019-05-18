package com.powerje.nyan

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window

import java.io.IOException

class NyanActivity : Activity() {
    private var mRoot: NyanView? = null
    private var mPlayer: MediaPlayer? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
            actionBar!!.setBackgroundDrawable(null)
        }
    }

    public override fun onResume() {
        super.onResume()
        // scale by whichever is larger, width or height
        val metrics = this.resources.displayMetrics
        val h = metrics.heightPixels
        val w = metrics.widthPixels
        val largest = if (h > w) h else w

        mRoot = NyanView(this, largest)
        setContentView(mRoot)

        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.dyan_loop)
            mPlayer!!.isLooping = true
        } else {
            mPlayer!!.start()
        }

        mPlayer!!.setScreenOnWhilePlaying(true)

        try {
            mPlayer!!.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mPlayer!!.setOnPreparedListener { mPlayer!!.start() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> startActivity(Intent(this@NyanActivity, NyanSettings::class.java))
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    public override fun onPause() {
        super.onPause()
        mPlayer!!.pause()
    }

    public override fun onStop() {
        super.onStop()
        mRoot!!.cancel()
        mRoot = null
        System.gc()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mRoot = null
    }

}