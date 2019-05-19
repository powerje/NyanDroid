package com.powerje.nyan

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import java.io.IOException
import android.view.ViewGroup
import androidx.core.view.ViewCompat


class NyanActivity : AppCompatActivity() {
    private var nyanView: NyanView? = null
    private var player: MediaPlayer? = null
    private var toolbar: Toolbar? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nyan_activity)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
        ViewCompat.setOnApplyWindowInsetsListener(toolbar!!) { v, insets ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets.consumeSystemWindowInsets()
        }
    }

    public override fun onResume() {
        super.onResume()

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

        player?.run {
            setOnPreparedListener {
                it.start()
            }
        }

        nyanView = findViewById(R.id.nyan_view)
        nyanView!!.start()
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
        player?.pause()
        nyanView?.cancel()
        nyanView = null
    }

    public override fun onStop() {
        super.onStop()
        nyanView?.cancel()
        nyanView = null
        System.gc()
    }

    public override fun onDestroy() {
        super.onDestroy()
        nyanView = null
    }

}