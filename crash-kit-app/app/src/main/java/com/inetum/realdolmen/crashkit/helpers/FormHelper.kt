package com.inetum.realdolmen.crashkit.helpers

import android.content.Context
import android.widget.TextView
import com.inetum.realdolmen.crashkit.R

class FormHelper(
    context: Context,
    private val fields: List<TextView>
) {

    public val errors = InputFieldsErrors(
        context.getString(R.string.field_required),
        context.getString(R.string.no_digits_allowed),
        context.getString(R.string.no_letters_allowed),
        context.getString(R.string.field_needs_characters),
        context.getString(R.string.future_date),
        context.getString(R.string.past_date),
        context.getString(R.string.invalid_email)
    )

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
    var fieldRequired: String,
    var noDigitsAllowed: String,
    var noLettersAllowed: String,
    var fieldNeedsCharacters: String,
    var futureDate: String,
    var pastDate: String,
    var invalidEmail: String
)

