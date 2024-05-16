package com.inetum.realdolmen.crashkit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object LogTags {
    const val TAG_FIELD_VALIDATION = "FieldValidation"
    const val TAG_NETWORK_REQUEST = "NetworkRequest"
    const val TAG_LOGIN_STATUS= "LoginStatus"
    const val TAG_PARSING_ERROR = "ParsingError"
    const val TAG_QR_CODE = "QRCode"
    const val TAG_CERTIFICATE= "Certificate"
    const val TAG_LOCATION= "Location"
    const val TAG_SKETCH_VIEW= "SketchView"
    const val TAG_ROTATION_GESTURE_DETECTOR= "RotationGestureDetector"
}

fun Context.createSimpleDialog(title: String, message: String) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun LocalDate.toIsoString(): String {
    return this.format(DateTimeFormatter.ISO_LOCAL_DATE)
}

fun LocalDateTime.toIsoString(): String {
    return this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

fun LocalDate.to24Format(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return this.format(formatter)
}

fun LocalDateTime.to24Format(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return this.format(formatter)
}

fun String.toLocalDateTime(): LocalDateTime? {
    return try {
        LocalDateTime.parse(this, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (e: DateTimeParseException) {
        null
    }
}

fun String.toLocalDate(): LocalDate? {
    val formats = listOf("yyyy-MM-dd", "dd/MM/yyyy")
    for (format in formats) {
        try {
            return LocalDate.parse(this, DateTimeFormatter.ofPattern(format))
        } catch (e: DateTimeParseException) {
            // Ignore and try the next format
        }
    }
    // All formats failed
    return null
}

fun Bitmap.toByteArray(): ByteArray {
    val maxSize = 65535
    var quality = 100
    var byteArray: ByteArray

    do {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        byteArray = outputStream.toByteArray()

        // Reduce the quality for the next iteration (if needed)
        quality -= 10
    } while (byteArray.size > maxSize && quality > 0)

    return byteArray
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun View.createBitmapFromView(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}






