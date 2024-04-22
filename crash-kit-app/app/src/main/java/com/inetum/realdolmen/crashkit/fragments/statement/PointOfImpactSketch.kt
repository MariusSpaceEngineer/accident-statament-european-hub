package com.inetum.realdolmen.crashkit.fragments.statement

import android.annotation.SuppressLint
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
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.accidentsketch.IAccidentDrawable
import com.inetum.realdolmen.crashkit.accidentsketch.NonRotatableMovableDrawable
import com.inetum.realdolmen.crashkit.accidentsketch.RotatableMovableDrawable
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.RotationGestureDetector
import kotlin.math.max
import kotlin.math.min

class PointOfImpactSketch(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //Keeps the shape list after going to another view
    var viewModel: NewStatementViewModel? = null

    val shapes = mutableListOf<Pair<IAccidentDrawable, Point>>()
    private var currentShape: Pair<IAccidentDrawable, Point>? = null

    //Used by the ScaleGestureDetector
    private var touchOffset = Point()
    private var mScaleFactor = 1f

    //Used by the RotationGestureDetector
    private val rotations = mutableMapOf<RotatableMovableDrawable, Float>()

    //Booleans needed to keep track which gesture is happening
    private var isScaling: Boolean = false
    private var isRotating: Boolean = false

    private val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                Log.d("PointOfImpactSketchView", "onScale called with scale factor $scaleFactor")

                mScaleFactor *= scaleFactor
                mScaleFactor = max(0.1f, min(mScaleFactor, 5.0f))

                currentShape?.let { (accidentDrawable, position) ->
                    val drawable = accidentDrawable as Drawable

                    // Calculate the new size of the drawable
                    val newWidth = (drawable.intrinsicWidth * mScaleFactor).toInt()
                    val newHeight = (drawable.intrinsicHeight * mScaleFactor).toInt()

                    // Keep the initial position of the shape
                    val newX = position.x
                    val newY = position.y

                    // Resize the drawable
                    drawable.setBounds(
                        newX - newWidth / 2,
                        newY - newHeight / 2,
                        newX + newWidth / 2,
                        newY + newHeight / 2
                    )
                    Log.i("Size", drawable.bounds.toString())

                    invalidate()
                }
                return true
            }
        })

    private val rotationGestureDetector =
        RotationGestureDetector(object : RotationGestureDetector.OnRotationGestureListener {
            override fun onRotation(rotationDetector: RotationGestureDetector?): Boolean {
                val rotationAngle = rotationDetector?.angle ?: 0f
                Log.i("PointOfImpactSketchView", "onRotation called with rotation angle $rotationDetector")

                currentShape?.let { (drawable, _) ->
                    if (drawable is RotatableMovableDrawable) {
                        // Update the rotation angle of the drawable
                        drawable.setRotation(rotationAngle)

                        invalidate()
                    }
                }
                return true
            }
        })

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            // When a pointer (finger) is on the screen
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 1) {
                    currentShape = findShapeAt(event.x.toInt(), event.y.toInt())
                    currentShape?.let { (_, position) ->
                        //Determines the position of the pointer so that the figure doesn't jump when touched
                        touchOffset.set(event.x.toInt() - position.x, event.y.toInt() - position.y)
                    }
                }
                if (event.pointerCount > 1) {
                    // More than one finger down, check if at least one finger is on the figure and near the edge
                    handleMultiFingerDownEvent(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1 && !isScaling && !isRotating) {
                    // One finger is moving, move the shape
                    currentShape?.let { (accidentDrawable, position) ->
                        moveShape(accidentDrawable, event, position)
                    }
                } else if (event.pointerCount > 1 && isScaling) {
                    // More than one finger is moving and a scale gesture is in progress, defer to the ScaleGestureDetector
                    scaleGestureDetector.onTouchEvent(event)
                } else if (event.pointerCount > 1 && isRotating) {
                    // More than one finger is moving and a shape is selected, defer to the RotationGestureDetector
                    rotationGestureDetector.onTouchEvent(event)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 1) {
                    // All fingers are up or only one finger is left, end the multiple pointer gestures
                    isScaling = false
                    isRotating = false

                }
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((accidentDrawable, _) in shapes) {
            val drawable = accidentDrawable as Drawable

            // Save the current state of the canvas
            val saveCount = canvas.save()

            // Rotate the canvas around the center of the drawable
            rotateCanvas(drawable, canvas)

            // Draw the shape on the rotated canvas
            drawable.draw(canvas)
            // Restore the canvas to its previous state
            canvas.restoreToCount(saveCount)
        }
    }

    fun addShapes(shapeList: List<Int>) {
        val shapeSpacing = 150 // The distance between shapes

        for (resId in shapeList) {
            val drawable = ContextCompat.getDrawable(context, resId)

            val accidentDrawable =
                if (resId == R.drawable.personal_car_vehicle || resId == R.drawable.motorcycle_vehicle ||
                    resId == R.drawable.truck_vehicle
                ) {
                    NonRotatableMovableDrawable(drawable!!, resId, 0)
                } else {
                    RotatableMovableDrawable(drawable!!, resId, 1)
                }

            // Calculate a new position for each shape so they don't overlap
            val position = Point(width + 250+ shapes.size * (accidentDrawable.intrinsicWidth + shapeSpacing), height + 180 + accidentDrawable.intrinsicHeight / 2)

            // Set the initial bounds of the drawable to its intrinsic size at the specified position
            accidentDrawable.setBounds(
                position.x - accidentDrawable.intrinsicWidth / 2,
                position.y - accidentDrawable.intrinsicHeight / 2,
                position.x + accidentDrawable.intrinsicWidth / 2,
                position.y + accidentDrawable.intrinsicHeight / 2
            )

            shapes.add(Pair(accidentDrawable, position))

        }
        viewModel?.pointOfImpactVehicleASketchShapes?.value = shapes
        invalidate() // Redraw the view
    }


    private fun moveShape(
        accidentDrawable: IAccidentDrawable,
        event: MotionEvent,
        position: Point
    ) {
        val drawable = accidentDrawable as Drawable

        if (accidentDrawable is RotatableMovableDrawable) {

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
    }

    private fun handleMultiFingerDownEvent(event: MotionEvent) {
        val shape = findShapeAt(event.getX(0).toInt(), event.getY(0).toInt())
        //Check if one of the pointers is near the edge of the shape
        if (shape != null && shape.first is RotatableMovableDrawable &&
            (isNearEdge(
                event.getX(0).toInt(),
                event.getY(0).toInt(),
                shape.first as Drawable
            ) ||
                    isNearEdge(
                        event.getX(1).toInt(),
                        event.getY(1).toInt(),
                        shape.first as Drawable
                    ))
        ) {
            Log.i("PointOfImpactSketchView", "At least one finger is near the edge of a shape")
            // At least one finger is on the figure and near the edge, start a scale gesture
            currentShape = shape
            isScaling = true
            isRotating = false
            scaleGestureDetector.onTouchEvent(event)
        } else if (currentShape != null && currentShape!!.first is RotatableMovableDrawable) {
            // Two fingers down, but not near the edge, start a rotation gesture
            isScaling = false
            isRotating = true
            rotationGestureDetector.onTouchEvent(event)
        }
    }

    private fun rotateCanvas(
        drawable: Drawable,
        canvas: Canvas
    ): Pair<Float, Float> {
        val rotation = rotations[drawable] ?: 0f
        val centerX = (drawable.bounds.left + drawable.bounds.right) / 2f
        val centerY = (drawable.bounds.top + drawable.bounds.bottom) / 2f
        canvas.rotate(rotation, centerX, centerY)
        return Pair(centerX, centerY)
    }

    private fun findShapeAt(x: Int, y: Int): Pair<IAccidentDrawable, Point>? {
        val shapesAtPoint = shapes.filter { (accidentDrawable, _) ->
            val drawable = accidentDrawable as Drawable

            // Check if the point is within the current bounds of the drawable
            drawable.bounds.contains(x, y)
        }
        return if (shapesAtPoint.isNotEmpty()) {
            // Find the shape with the highest priority
            val shape = shapesAtPoint.maxByOrNull { (drawable, _) ->
                drawable.priority
            }
            Log.i("PointOfImpactSketchView", "Shape found at ${shape?.second?.x} -${shape?.second?.y}")
            shape
        } else {
            Log.i("PointOfImpactSketchView", "Shape not found")
            null
        }
    }

    private fun isNearEdge(
        x: Int,
        y: Int,
        drawable: Drawable,
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