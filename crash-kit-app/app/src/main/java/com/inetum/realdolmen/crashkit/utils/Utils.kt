package com.inetum.realdolmen.crashkit.utils
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

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