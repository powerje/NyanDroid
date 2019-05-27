package com.powerje.nyan

import android.service.wallpaper.WallpaperService

class NyanPaper : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return NyanEngine()
    }

    internal inner class NyanEngine : WallpaperService.Engine() {
        private val nyanAnimation = NyanAnimation(applicationContext, surfaceHolder)

        override fun onVisibilityChanged(visible: Boolean) {
            nyanAnimation.onVisibilityChanged(visible)
        }
    }
}