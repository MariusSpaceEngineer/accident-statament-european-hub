package com.inetum.realdolmen.crashkit.accidentsketch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.RotationGestureDetector
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class SketchView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    val shapes = mutableListOf<Triple<IAccidentDrawable, Point, TextView?>>()
    private var currentShape: Triple<IAccidentDrawable, Point, TextView?>? = null

    private var touchOffset = Point()
    private var mScaleFactor = 1f

    private val rotations = mutableMapOf<RotatableDrawable, Float>()

    private var isScaling: Boolean = false
    private var isRotating: Boolean = false

    private lateinit var deleteButton: Button
    private lateinit var changeAddressButton: Button

    private lateinit var _viewModel: NewStatementViewModel

    var viewModel: NewStatementViewModel? = null

    fun setupButtons(deleteBtn: Button, changeAddressBtn: Button) {
        deleteButton = deleteBtn
        deleteButton.setOnClickListener {
            // Remove the currentShape from shapes
            currentShape?.let { shapes.remove(it) }
            currentShape = null
            deleteButton.visibility = INVISIBLE // Make the button invisible
            changeAddressButton.visibility = INVISIBLE // Make the button invisible
            invalidate() // Redraw the view
        }
        changeAddressButton = changeAddressBtn
        changeAddressButton.setOnClickListener {
            // Create a MaterialAlertDialogBuilder
            val builder = MaterialAlertDialogBuilder(context)

            // Create an EditText
            val editText = EditText(context)

            // Set the dialog title, view, and buttons
            builder.setTitle("Enter Address")
                .setView(editText)
                .setPositiveButton("OK") { dialog, _ ->
                    // Create a new TextView and set its text to the text entered in the EditText
                    val textView = TextView(context)
                    textView.text = editText.text.toString()

                    currentShape?.let {
                        currentShape?.third?.text = textView.text
                        invalidate() // Redraw the view
                    }

                    // Dismiss the dialog
                    dialog.dismiss()

                    // Redraw the view
                    invalidate()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }

            // Show the dialog
            builder.show()
        }
    }


    private val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                var scaleFactor = detector.scaleFactor

                mScaleFactor *= scaleFactor
                mScaleFactor = max(0.1f, min(mScaleFactor, 5.0f))

                Log.d("SketchView", "onScale called with scale factor ${detector.scaleFactor}")
                currentShape?.let { (accidentDrawable, position) ->
                    val drawable = accidentDrawable as Drawable

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
                    if (drawable is RotatableDrawable) {
                        // Update the rotation angle of the drawable
                        val rotation = detector?.angle ?: 0f
                        Log.i("angle", rotation.toString())
                        drawable.setRotation(rotation)

                        invalidate()
                    }
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
                    currentShape?.let { (drawable, position) ->

                        touchOffset.set(event.x.toInt() - position.x, event.y.toInt() - position.y)
                        deleteButton.visibility = VISIBLE // Make the delete button visible

                        if (drawable.resId == R.drawable.road_90 || drawable.resId == R.drawable.road_180) {
                            changeAddressButton.visibility =
                                VISIBLE // Make the change address button visible
                        } else {
                            changeAddressButton.visibility =
                                INVISIBLE // Make the change address button invisible
                        }
                    } ?: run {
                        deleteButton.visibility = INVISIBLE // Make the delete button invisible
                        changeAddressButton.visibility =
                            INVISIBLE // Make the change address button invisible
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
                            firstFingerShape.first as Drawable
                        ) ||
                                isNearEdge(
                                    event.getX(1).toInt(),
                                    event.getY(1).toInt(),
                                    firstFingerShape.first as Drawable
                                ))
                    ) {
                        Log.i("Figure", "at least one finger near the edge of the shape")
                        // At least one finger is on the figure and near the edge, start a scale gesture
                        currentShape = firstFingerShape
                        isScaling = true
                        isRotating = false
                        scaleGestureDetector.onTouchEvent(event)
                    } else if (currentShape != null && currentShape!!.first is RotatableDrawable) {
                        // Two fingers down, but not near the edge, start a rotation gesture
                        isScaling = false
                        isRotating = true
                        rotationGestureDetector.onTouchEvent(event)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1 && !isScaling && !isRotating) {
                    // One finger is moving, move the shape
                    currentShape?.let { (accidentDrawable, position) ->

                        val drawable = accidentDrawable as Drawable

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
                } else if (event.pointerCount > 1 && isRotating) {
                    // More than one finger is moving and a shape is selected, defer to the RotationGestureDetector
                    rotationGestureDetector.onTouchEvent(event)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 1) {
                    // All fingers are up or only one finger is left, end the scale gesture
                    isScaling = false
                    isRotating = false

                }
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((accidentDrawable, _, textView) in shapes) {
            val resId = accidentDrawable.resId
            val drawable = accidentDrawable as Drawable

            // Save the current state of the canvas
            val saveCount = canvas.save()

            // Rotate the canvas around the center of the drawable
            val rotation = rotations[drawable] ?: 0f
            val centerX = (drawable.bounds.left + drawable.bounds.right) / 2f
            val centerY = (drawable.bounds.top + drawable.bounds.bottom) / 2f
            canvas.rotate(rotation, centerX, centerY)

            // Draw the shape on the rotated canvas
            drawable.draw(canvas)

            // Draw the text on the canvas
            textView?.let {
                val text = it.text.toString().uppercase(Locale.getDefault())

                // Adjust the text size based on the drawable size
                val textSize = min(
                    drawable.bounds.width(),
                    drawable.bounds.height()
                ) / 8f // Adjust this value as needed

                val textPaint = TextPaint().apply {
                    color = Color.BLUE
                    this.textSize = textSize
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                }

                // Create a StaticLayout for the text
                val layout = StaticLayout.Builder.obtain(
                    text,
                    0,
                    text.length,
                    textPaint,
                    drawable.bounds.width()
                )
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0.0f, 1.0f)
                    .setIncludePad(false)
                    .build()

                // Draw the text
                canvas.save()

                if (resId == R.drawable.road_180) {
                    // Position the text on the left side of the drawable
                    canvas.translate(
                        centerX - (drawable.bounds.width()),
                        centerY - (layout.height / 2f)
                    )
                } else if (resId == R.drawable.road_90) {
                    // Position the text under the drawable
                    canvas.translate(centerX, centerY + (drawable.bounds.height() / 2))
                }

                layout.draw(canvas)
                canvas.restore()
            }


            // Create a path that represents the rotated rectangle
            val path = Path()
            path.addRect(RectF(drawable.bounds), Path.Direction.CW)
            path.transform(matrix)

            // Draw the path
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawPath(path, paint)

            // Restore the canvas to its previous state
            canvas.restoreToCount(saveCount)
        }
    }

    private fun findShapeAt(x: Int, y: Int): Triple<IAccidentDrawable, Point, TextView?>? {
        val shapesAtPoint = shapes.filter { (accidentDrawable, _) ->
            val drawable = accidentDrawable as Drawable

            // Check if the point is within the current bounds of the drawable
            drawable.bounds.contains(x, y)
        }
        if (shapesAtPoint.isNotEmpty()) {
            // Find the shape with the highest priority
            val shape = shapesAtPoint.maxByOrNull { (drawable, _) ->
                drawable.priority
            }
            Log.i("Shape", "Shape found at ${shape?.second?.x} -${shape?.second?.y}")
            return shape
        } else {
            Log.i("Shape", "Shape not found")
            return null
        }
    }

    fun addShape(resId: Int, priority: Int) {
        val drawable = ContextCompat.getDrawable(context, resId)
        if (drawable != null) {

            val accidentDrawable =
                if (resId == R.drawable.road_90 || resId == R.drawable.road_180 ||
                    resId == R.drawable.four_road_junction || resId == R.drawable.roundabout
                ) {
                    NonRotatableDrawable(drawable, resId, priority)
                } else {
                    RotatableDrawable(drawable, resId, priority)
                }

            val position = Point(width / 2, height / 2)
            // Set the initial bounds of the drawable to its intrinsic size at the specified position
            accidentDrawable.setBounds(
                position.x - accidentDrawable.intrinsicWidth / 2,
                position.y - accidentDrawable.intrinsicHeight / 2,
                position.x + accidentDrawable.intrinsicWidth / 2,
                position.y + accidentDrawable.intrinsicHeight / 2
            )

            if (resId == R.drawable.road_90 || resId == R.drawable.road_180) {
                shapes.add(Triple(accidentDrawable, position, TextView(context)))

            } else {
                shapes.add(Triple(accidentDrawable, position, null))
            }

            viewModel?.accidentSketchShapes?.value = shapes
        }
        invalidate() // Redraw the view
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




