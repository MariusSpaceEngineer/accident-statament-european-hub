package com.inetum.realdolmen.crashkit.accidentsketch

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper

class NonRotatableDrawable(private val drawable: Drawable, override val resId: Int, override val priority: Int) : DrawableWrapper(drawable),
    IAccidentDrawable {

    override fun draw(canvas: Canvas) {
        drawable.draw(canvas)
    }
}
