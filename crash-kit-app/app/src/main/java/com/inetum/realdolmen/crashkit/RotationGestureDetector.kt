package com.inetum.realdolmen.crashkit

import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import kotlin.math.atan2

class RotationGestureDetector(private val mListener: OnRotationGestureListener) {
    private var mStartPoint = PointF()
    private var mEndPoint = PointF()
    private var mPivotPoint = PointF()
    private var mMatrix = Matrix()

    interface OnRotationGestureListener {
        fun onRotation(rotationDetector: RotationGestureDetector?): Boolean
    }

    var angle: Float = 0.toFloat()
        private set

    fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("RotationGestureDetector", "Touch event: ${event.actionMasked}")
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mStartPoint.set(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                mEndPoint.set(event.x, event.y)
                mPivotPoint.set(mStartPoint)
                angle = calculateAngle(event) // Update the angle here
                mMatrix.setRotate(angle, mPivotPoint.x, mPivotPoint.y)
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




