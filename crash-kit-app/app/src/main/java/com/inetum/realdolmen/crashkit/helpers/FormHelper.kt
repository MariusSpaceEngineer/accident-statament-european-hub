package com.inetum.realdolmen.crashkit.helpers

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.inetum.realdolmen.crashkit.R

class FormHelper(
    private val context: Context,
    private val fields: List<TextView>
) {

    val errors = InputFieldsErrors(
        context.getString(R.string.field_required),
        context.getString(R.string.no_digits_allowed),
        context.getString(R.string.no_letters_allowed),
        context.getString(R.string.field_needs_characters),
        context.getString(R.string.future_date),
        context.getString(R.string.past_date),
        context.getString(R.string.invalid_email)
    )

    fun validateFields(validationRules: List<Triple<EditText, (String?) -> Boolean, String?>>) {
        validationRules.forEach { (field, validationRule, errorMessage) ->
            if (validationRule(field.text.toString())) {
                field.error = errorMessage
            }
        }
    }

    fun validatePassword(password: String?): String? {
        val passwordErrors = mutableListOf<String>()
        if (password.isNullOrEmpty()){
            passwordErrors.add(errors.fieldRequired)
        }
        if ((password?.length ?: 0) < 6) {
            passwordErrors.add(context.getString(R.string.password_too_short))
        }
        if (password?.any { it.isUpperCase() } != true) {
            passwordErrors.add(context.getString(R.string.password_no_uppercase))
        }
        if (password?.any { it.isLowerCase() } != true) {
            passwordErrors.add(context.getString(R.string.password_no_lowercase))
        }
        if (password?.any { it.isDigit() } != true) {
            passwordErrors.add(context.getString(R.string.password_no_digits))
        }
        if (password?.contains(Regex("[@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")) != true) {
            passwordErrors.add(context.getString(R.string.password_no_special_character))
        }
        if (password?.contains(" ") == true) {
            passwordErrors.add(context.getString(R.string.password_whitespace))
        }
        // If no errors, return null
        return if (passwordErrors.isEmpty()) null else passwordErrors.joinToString(separator = "\n")
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

