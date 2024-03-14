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
