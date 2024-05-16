package com.inetum.realdolmen.crashkit.accidentsketch

import android.annotation.SuppressLint
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
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_SKETCH_VIEW
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.RotationGestureDetector
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class SketchView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //The buttons are not in the custom view but in the fragment
    private lateinit var deleteButton: Button
    private lateinit var changeAddressButton: Button

    //Keeps the shape list after going to another view
    var viewModel: NewStatementViewModel? = null

    val shapes = mutableListOf<Triple<IAccidentDrawable, Point, TextView?>>()
    private var currentShape: Triple<IAccidentDrawable, Point, TextView?>? = null

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
                Log.d(TAG_SKETCH_VIEW, "onScale called with scale factor $scaleFactor")

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
                    invalidate()
                }
                return true
            }
        })

    private val rotationGestureDetector =
        RotationGestureDetector(object : RotationGestureDetector.OnRotationGestureListener {
            override fun onRotation(rotationDetector: RotationGestureDetector?): Boolean {
                val rotationAngle = rotationDetector?.angle ?: 0f
                Log.d(TAG_SKETCH_VIEW, "onRotation called with rotation angle $rotationDetector")

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
    /**
     * This function handles touch events on the view.
     *
     * @param event The MotionEvent object containing full information about the event.
     * @return A boolean indicating whether the event was handled.
     *
     * The function handles different types of motion events:
     * - ACTION_DOWN and ACTION_POINTER_DOWN: These events occur when a pointer (finger) touches the screen.
     *   If there's only one pointer, it updates the button's visibility. If there are multiple pointers, it checks if at least one finger is on the figure and near the edge.
     * - ACTION_MOVE: This event occurs when a pointer moves. If there's only one pointer and no scaling or rotating gesture is in progress, it moves the shape.
     *   If there are multiple pointers and a scaling or rotating gesture is in progress, it defers to the ScaleGestureDetector or RotationGestureDetector respectively.
     * - ACTION_UP and ACTION_POINTER_UP: These events occur when a pointer leaves the screen. If all fingers are up or only one finger is left, it ends the multiple pointer gestures.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            // When a pointer (finger) is on the screen
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 1) {
                    // If one pointer is on a figure update the button's visibility
                    updateButtonVisibility(event)
                    //When multiple pointers are on the screen
                } else if (event.pointerCount > 1) {
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
        for ((accidentDrawable, _, textView) in shapes) {
            val resId = accidentDrawable.resId
            val drawable = accidentDrawable as Drawable

            // Save the current state of the canvas
            val saveCount = canvas.save()

            // Rotate the canvas around the center of the drawable
            val (centerX, centerY) = rotateCanvas(drawable, canvas)

            // Draw the shape on the rotated canvas
            drawable.draw(canvas)

            // Draw the text on the rotated canvas
            textView?.let {
                drawText(it, drawable, canvas, resId, centerX, centerY)
            }

            // Restore the canvas to its previous state
            canvas.restoreToCount(saveCount)
        }
    }

    fun setupButtons(deleteBtn: Button, changeAddressBtn: Button) {
        setupDeleteButton(deleteBtn)
        setupChangeAddressButton(changeAddressBtn)
    }

    private fun setupDeleteButton(deleteBtn: Button) {
        deleteButton = deleteBtn
        deleteButton.setOnClickListener {
            // Remove the currentShape from shapes
            currentShape?.let { shapes.remove(it) }
            currentShape = null

            //Make the buttons invisible as no currentShape is selected anymore
            deleteButton.visibility = INVISIBLE
            changeAddressButton.visibility = INVISIBLE

            // Redraw the view
            invalidate()
        }
    }

    private fun setupChangeAddressButton(changeAddressBtn: Button) {
        changeAddressButton = changeAddressBtn
        changeAddressButton.setOnClickListener {
            showChangeAddressDialog()
        }
    }

    private fun showChangeAddressDialog() {
        val builder = MaterialAlertDialogBuilder(context)

        // Create an EditText that can be used by the user to add a address name
        val editText = EditText(context)

        builder.setTitle(context.getString(R.string.accident_sketch_change_address_dialog))
            .setView(editText)
            .setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
                updateShapeText(editText.text.toString())
                dialog.dismiss()

                // Redraw the view
                invalidate()
            }
            .setNegativeButton(context.getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
        // Show the dialog
        builder.show()
    }

    private fun updateShapeText(text: String) {
        // Create a new TextView and set its text to the text entered in the EditText
        val textView = TextView(context)
        textView.text = text

        currentShape?.let {
            //Add the text to the shape
            it.third?.text = textView.text
        }
    }


    fun addShape(resId: Int, priority: Int) {
        val drawable = ContextCompat.getDrawable(context, resId)
        if (drawable != null) {

            val accidentDrawable =
                if (resId == R.drawable.road_90 || resId == R.drawable.road_180 ||
                    resId == R.drawable.four_road_junction || resId == R.drawable.roundabout
                ) {
                    NonRotatableMovableDrawable(drawable, resId, priority)
                } else {
                    RotatableMovableDrawable(drawable, resId, priority)
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

    private fun moveShape(
        accidentDrawable: IAccidentDrawable,
        event: MotionEvent,
        position: Point
    ) {
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

    private fun handleMultiFingerDownEvent(event: MotionEvent) {
        val shape = findShapeAt(event.getX(0).toInt(), event.getY(0).toInt())
        //Check if one of the pointers is near the edge of the shape
        if (shape != null &&
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
            Log.d(TAG_SKETCH_VIEW, "At least one finger is near the edge of a shape")
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

    private fun updateButtonVisibility(event: MotionEvent) {
        currentShape = findShapeAt(event.x.toInt(), event.y.toInt())
        currentShape?.let { (drawable, position) ->
            //Determines the position of the pointer so that the figure doesn't jump when touched
            touchOffset.set(event.x.toInt() - position.x, event.y.toInt() - position.y)

            deleteButton.visibility = VISIBLE

            if (drawable.resId == R.drawable.road_90 || drawable.resId == R.drawable.road_180) {
                changeAddressButton.visibility = VISIBLE
            } else {
                changeAddressButton.visibility = INVISIBLE
            }
            //If no figure is selected
        } ?: run {
            deleteButton.visibility = INVISIBLE
            changeAddressButton.visibility = INVISIBLE
        }
    }

    private fun drawShapeRectangle(
        drawable: Drawable,
        canvas: Canvas
    ) {
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
    }

    private fun drawText(
        it: TextView,
        drawable: Drawable,
        canvas: Canvas,
        resId: Int,
        centerX: Float,
        centerY: Float
    ) {
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

    private fun findShapeAt(x: Int, y: Int): Triple<IAccidentDrawable, Point, TextView?>? {
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
            Log.d(TAG_SKETCH_VIEW, "Shape found at ${shape?.second?.x} -${shape?.second?.y}")
            shape
        } else {
            Log.d(TAG_SKETCH_VIEW, "Shape not found")
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

        return (x <= bounds.left + threshold || x >= bounds.right - threshold ||
                y <= bounds.top + threshold || y >= bounds.bottom - threshold)
    }

}




