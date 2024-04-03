package com.inetum.realdolmen.crashkit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

class SketchView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val shapes = mutableListOf<Pair<Drawable, Point>>()
    private var currentShape: Pair<Drawable, Point>? = null
    private var touchOffset = Point()

    private var mScaleFactor = 1f

    private val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                var scaleFactor = detector.scaleFactor

                // Limit the scale factor to half (0.5) or double (2.0) the current size
                scaleFactor = when {
                    scaleFactor < 1f -> max(
                        0.5f,
                        scaleFactor
                    ) // If scaling down, limit to half the size
                    scaleFactor > 1f -> min(
                        2.0f,
                        scaleFactor
                    ) // If scaling up, limit to double the size
                    else -> scaleFactor
                }

                mScaleFactor *= scaleFactor
                mScaleFactor = max(0.1f, min(mScaleFactor, 5.0f))

                Log.d("SketchView", "onScale called with scale factor ${detector.scaleFactor}")
                // Find the shape under the scale gesture
                currentShape = findShapeAt(detector.focusX.toInt(), detector.focusY.toInt())
                currentShape?.let { (drawable, position) ->
                    val scaleFactor = detector.scaleFactor

                    // Calculate the new size of the drawable
                    val newWidth = (drawable.intrinsicWidth * scaleFactor).toInt()
                    val newHeight = (drawable.intrinsicHeight * scaleFactor).toInt()

                    // Calculate the new position from the middle of the shape
                    val newX = position.x + newWidth / 2
                    val newY = position.y + newHeight / 2

                    drawable.setBounds(
                        newX - newWidth / 2,
                        newY - newHeight / 2,
                        newX + newWidth / 2,
                        newY + newHeight / 2
                    )
                    invalidate()
                }
                return true
            }
        })

    fun addShape(resId: Int) {
        val drawable = ContextCompat.getDrawable(context, resId)
        if (drawable != null) {
            val position = Point(width / 2, height / 2)
            // Set the initial bounds of the drawable to its intrinsic size at the specified position
            drawable.setBounds(
                position.x - drawable.intrinsicWidth / 2,
                position.y - drawable.intrinsicHeight / 2,
                position.x + drawable.intrinsicWidth / 2,
                position.y + drawable.intrinsicHeight / 2
            )
            shapes.add(Pair(drawable, position))
        }
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((drawable, _) in shapes) {
            // Draw the shape on the canvas at its current bounds
            drawable.draw(canvas)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 1) {
                    // One finger down, start a move gesture
                    currentShape = findShapeAt(event.x.toInt(), event.y.toInt())
                    currentShape?.let { (_, position) ->
                        touchOffset.set(event.x.toInt() - position.x, event.y.toInt() - position.y)
                    }
                } else {
                    // More than one finger down, start a scale gesture
                    currentShape = findShapeAt(event.x.toInt(), event.y.toInt())
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1) {
                    // One finger is moving, move the shape
                    currentShape?.let { (drawable, position) ->
                        // Calculate the new position
                        val newX = event.x.toInt() - touchOffset.x
                        val newY = event.y.toInt() - touchOffset.y

                        // Update the position
                        position.set(newX, newY)

                        // Update the bounds of the drawable to its current size at the new position
                        drawable.setBounds(
                            newX - drawable.bounds.width() / 2,
                            newY - drawable.bounds.height() / 2,
                            newX + drawable.bounds.width() / 2,
                            newY + drawable.bounds.height() / 2
                        )

                        invalidate()
                    }
                } else {
                    // More than one finger is moving, defer to the ScaleGestureDetector
                    scaleGestureDetector.onTouchEvent(event)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount == 1) {
                    // Last finger lifted, end the gesture
                    currentShape = null
                }
            }
        }
        return true
    }


    private fun findShapeAt(x: Int, y: Int): Pair<Drawable, Point>? {
        var shape = shapes.find { (drawable, _) ->
            // Check if the point is within the current bounds of the drawable
            drawable.bounds.contains(x, y)
        }
        if (shape != null)
            Log.i("Shape", "Shape found at ${shape.second.x} -${shape.second.y}")
        else {
            Log.i("Shape", "Shape not found")

        }
        return shape
    }
}




