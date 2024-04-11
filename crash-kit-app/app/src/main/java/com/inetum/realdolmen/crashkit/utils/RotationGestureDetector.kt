package com.inetum.realdolmen.crashkit.utils

import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import kotlin.math.atan2

class RotationGestureDetector(private val mListener: OnRotationGestureListener) {
    private var startPoint = PointF()
    private var endPoint = PointF()
    private var pivotPoint = PointF()
    private var matrix = Matrix()

    interface OnRotationGestureListener {
        fun onRotation(rotationDetector: RotationGestureDetector?): Boolean
    }

    var angle: Float = 0.toFloat()
        private set

    fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("RotationGestureDetector", "Touch event: ${event.actionMasked}")
        when (event.actionMasked) {
            //When one pointer is down it sets that as the start point for the rotation
            MotionEvent.ACTION_DOWN -> {
                startPoint.set(event.x, event.y)
            }
            //When a pointer moves it adjusts the angle of the figure
            MotionEvent.ACTION_MOVE -> {
                endPoint.set(event.x, event.y)
                pivotPoint.set(startPoint)
                angle = calculateAngle(event)
                matrix.setRotate(angle, pivotPoint.x, pivotPoint.y)
                mListener.onRotation(this)
            }
        }
        return true
    }

    private fun calculateAngle(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        val angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()))
        Log.d("RotationGestureDetector", "Calculated angle: $angle")
        return angle.toFloat()
    }
}




