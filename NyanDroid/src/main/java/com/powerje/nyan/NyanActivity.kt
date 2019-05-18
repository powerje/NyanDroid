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
    private var root: NyanView? = null
    private var player: MediaPlayer? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        actionBar!!.setBackgroundDrawable(null)
    }

    public override fun onResume() {
        super.onResume()
        // scale by whichever is larger, width or height
        val metrics = this.resources.displayMetrics
        val h = metrics.heightPixels
        val w = metrics.widthPixels
        val largest = if (h > w) h else w

        root = NyanView(this, largest)
        setContentView(root)

        if (player == null) {
            player = MediaPlayer.create(this, R.raw.dyan_loop)
            player!!.isLooping = true
        } else {
            player!!.start()
        }

        player!!.setScreenOnWhilePlaying(true)

        try {
            player!!.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        player!!.setOnPreparedListener { player!!.start() }
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
        player!!.pause()
    }

    public override fun onStop() {
        super.onStop()
        root!!.cancel()
        root = null
        System.gc()
    }

    public override fun onDestroy() {
        super.onDestroy()
        root = null
    }

}