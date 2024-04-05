package com.inetum.realdolmen.crashkit

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.util.Log

class RotatableDrawable(private val drawable: Drawable, val resId: Int) : DrawableWrapper(drawable) {
    private var rotation = 0f


    fun setRotation(rotation: Float) {
        Log.d("RotatableDrawable", "Setting rotation to $rotation")
        this.rotation = rotation
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        Log.d("RotatableDrawable", "Drawing with rotation $rotation")
        val saveCount = canvas.save()

        val bounds = bounds
        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()
        canvas.rotate(rotation, cx, cy)

        drawable.draw(canvas)

        canvas.restoreToCount(saveCount)
    }

}
