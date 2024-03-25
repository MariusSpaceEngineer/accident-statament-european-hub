package com.inetum.realdolmen.crashkit.helpers

import android.widget.TextView

class FormHelper(
    private val fields: List<TextView>) {

    fun validateFields(validationRules: List<Triple<TextView, (String?) -> Boolean, String>>) {
        validationRules.forEach { (field, validationRule, errorMessage) ->
            if (validationRule(field.text.toString())) {
                field.error = errorMessage
            }
        }
    }

    fun clearErrors() {
        fields.forEach { it.error = null }
    }
}

data class InputFieldsErrors(
    var fieldRequired: String = "Field is required",
    var noDigitsAllowed: String = "No digits allowed",
    var noLettersAllowed: String = "No letters allowed",
    var fieldNeedsCharacters: String = "Field needs certain characters:",
    var futureDate: String = "Date is in the future",
    var pastDate: String = "Date is in the past",
    var invalidEmail: String = "Invalid email"
)

