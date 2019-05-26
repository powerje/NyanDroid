package com.powerje.nyan

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView

/**
 * NyanView draws NyanDroid flying through space distributing Ice Cream Nyanwich.
 * @author powerj
 */
class NyanView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : SurfaceView(context, attrs, defStyle) {
    private val nyanAnimation = NyanAnimation(context.getSharedPreferences(context.getString(R.string.shared_preferences_name), 0), getContext(), holder)

    fun start() {
        nyanAnimation.onVisibilityChanged(true)
    }

    fun cancel() {
        nyanAnimation.onVisibilityChanged(false)
    }

}