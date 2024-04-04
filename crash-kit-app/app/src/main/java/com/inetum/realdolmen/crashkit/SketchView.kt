package com.inetum.realdolmen.crashkit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

class SketchView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val shapes = mutableListOf<Pair<RotatableDrawable, Point>>()
    private var currentShape: Pair<RotatableDrawable, Point>? = null

    private var touchOffset = Point()
    private var mScaleFactor = 1f

    private val rotations = mutableMapOf<RotatableDrawable, Float>()

    private var isScaling: Boolean = false

    private lateinit var deleteButton: Button

    fun setupButton(button: Button){
        deleteButton= button
        deleteButton.setOnClickListener {
            // Remove the currentShape from shapes
            currentShape?.let { shapes.remove(it) }
            currentShape = null
            deleteButton.visibility = View.INVISIBLE // Make the button invisible
            invalidate() // Redraw the view
        }
    }


    private val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                var scaleFactor = detector.scaleFactor

                mScaleFactor *= scaleFactor
                mScaleFactor = max(0.1f, min(mScaleFactor, 5.0f))

                Log.d("SketchView", "onScale called with scale factor ${detector.scaleFactor}")
                currentShape?.let { (drawable, position) ->

                    // Calculate the new size of the drawable
                    var newWidth = (drawable.intrinsicWidth * mScaleFactor).toInt()
                    var newHeight = (drawable.intrinsicHeight * mScaleFactor).toInt()

                    // Keep the initial position of the shape
                    val newX = position.x
                    val newY = position.y

                    drawable.setBounds(
                        newX - newWidth / 2,
                        newY - newHeight / 2,
                        newX + newWidth / 2,
                        newY + newHeight / 2
                    )
                    invalidate()

                    Log.i("Size", drawable.bounds.toString())
                }
                return true
            }

        })

    private val rotationGestureDetector =
        RotationGestureDetector(object : RotationGestureDetector.OnRotationGestureListener {
            override fun onRotation(detector: RotationGestureDetector?): Boolean {
                Log.i("rotate", "rotation tiggered")
                currentShape?.let { (drawable, _) ->
                    // Update the rotation angle of the drawable
                    val rotation = detector?.angle ?: 0f
                    Log.i("angle", rotation.toString())
                    drawable.setRotation(rotation)

                    invalidate()
                }
                return true
            }

        })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 1) {
                    // One finger down, start a move gesture
                    currentShape = findShapeAt(event.x.toInt(), event.y.toInt())
                    currentShape?.let { (_, position) ->
                        touchOffset.set(event.x.toInt() - position.x, event.y.toInt() - position.y)
                        deleteButton.visibility = View.VISIBLE // Make the button visible
                    } ?: run {
                        deleteButton.visibility = View.INVISIBLE // Make the button invisible
                    }

                } else if (event.pointerCount > 1) {
                    // More than one finger down, check if at least one finger is on the figure and near the edge
                    val firstFingerShape = findShapeAt(event.getX(0).toInt(), event.getY(0).toInt())
                    val secondFingerShape =
                        findShapeAt(event.getX(1).toInt(), event.getY(1).toInt())
                    if (firstFingerShape != null &&
                        (isNearEdge(
                            event.getX(0).toInt(),
                            event.getY(0).toInt(),
                            firstFingerShape.first
                        ) ||
                                isNearEdge(
                                    event.getX(1).toInt(),
                                    event.getY(1).toInt(),
                                    firstFingerShape.first
                                ))
                    ) {
                        Log.i("Figure", "at least one finger near the edge of the shape")
                        // At least one finger is on the figure and near the edge, start a scale gesture
                        currentShape = firstFingerShape
                        isScaling = true
                        scaleGestureDetector.onTouchEvent(event)
                    } else if (currentShape != null) {
                        // Two fingers down, but not near the edge, start a rotation gesture
                        isScaling = false
                        rotationGestureDetector.onTouchEvent(event)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1 && !isScaling && currentShape != null) {
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
                } else if (event.pointerCount > 1 && isScaling) {
                    // More than one finger is moving and a scale gesture is in progress, defer to the ScaleGestureDetector
                    scaleGestureDetector.onTouchEvent(event)
                } else if (event.pointerCount > 1 && currentShape != null) {
                    // More than one finger is moving and a shape is selected, defer to the RotationGestureDetector
                    rotationGestureDetector.onTouchEvent(event)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 1) {
                    // All fingers are up or only one finger is left, end the scale gesture
                    isScaling = false
                }
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((drawable, _) in shapes) {
            // Save the current state of the canvas
            val saveCount = canvas.save()

            // Rotate the canvas around the center of the drawable
            val rotation = rotations[drawable] ?: 0f
            val centerX = (drawable.bounds.left + drawable.bounds.right) / 2f
            val centerY = (drawable.bounds.top + drawable.bounds.bottom) / 2f
            canvas.rotate(rotation, centerX, centerY)

            // Draw the shape on the rotated canvas
            drawable.draw(canvas)

            // Draw a rectangle around the shape
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawRect(drawable.bounds, paint)

            // Restore the canvas to its previous state
            canvas.restoreToCount(saveCount)
        }
    }

    fun addShape(resId: Int) {
        val drawable = ContextCompat.getDrawable(context, resId)
        if (drawable != null) {
            val rotatableDrawable = RotatableDrawable(drawable)
            val position = Point(width / 2, height / 2)
            // Set the initial bounds of the drawable to its intrinsic size at the specified position
            rotatableDrawable.setBounds(
                position.x - drawable.intrinsicWidth / 2,
                position.y - drawable.intrinsicHeight / 2,
                position.x + drawable.intrinsicWidth / 2,
                position.y + drawable.intrinsicHeight / 2
            )
            shapes.add(Pair(rotatableDrawable, position))
        }
        invalidate() // Redraw the view
    }

    private fun findShapeAt(x: Int, y: Int): Pair<RotatableDrawable, Point>? {
        val shape = shapes.find { (drawable, _) ->
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

    private fun isNearEdge(
        x: Int,
        y: Int,
        drawable: RotatableDrawable,
        thresholdPercentage: Float = 0.2f
    ): Boolean {
        val bounds = drawable.bounds
        val threshold = (bounds.width() * thresholdPercentage).toInt()
        val result = (x <= bounds.left + threshold || x >= bounds.right - threshold ||
                y <= bounds.top + threshold || y >= bounds.bottom - threshold)

        Log.i("Near the edge", result.toString())
        return result
    }

}




