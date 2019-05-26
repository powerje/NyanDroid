package com.powerje.nyan

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class NyanPaper : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return NyanEngine()
    }

    internal inner class NyanEngine : WallpaperService.Engine() {
        private val nyanAnimation = NyanAnimation(this@NyanPaper.getSharedPreferences(getString(R.string.shared_preferences_name), 0), applicationContext, surfaceHolder)

        override fun onVisibilityChanged(visible: Boolean) {
            nyanAnimation.onVisibilityChanged(visible)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            nyanAnimation.onSurfaceChanged(holder, format, width, height)
        }
    }
}