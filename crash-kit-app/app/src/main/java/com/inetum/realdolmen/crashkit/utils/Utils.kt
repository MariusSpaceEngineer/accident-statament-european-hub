package com.inetum.realdolmen.crashkit.utils

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun Context.createSimpleDialog(title: String, message: String) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun Map<TextInputEditText, String>.areFieldsValid(): Boolean {
    var allFieldsValid = true
    for ((field, errorMessage) in this) {
        if (field.text.toString().trim().isEmpty()) {
            field.error = errorMessage
            allFieldsValid = false
        }
    }
    return allFieldsValid
}

fun FragmentManager.printBackStack() {
    val count = this.backStackEntryCount
    for (i in 0 until count) {
        val entry: FragmentManager.BackStackEntry = this.getBackStackEntryAt(i)
        Log.i("BackStack", "Entry at $i: ${entry.name}")
    }
    Log.i("BackStack", "BackStack count: $count")
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


