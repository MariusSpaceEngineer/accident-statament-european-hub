package com.inetum.realdolmen.crashkit.utils

/**
 * This interface defines the setupValidation method. Before calling this method,
 * the following properties must be set:
 *
 * - inputFieldsErrors: An instance of InputFieldsErrors.
 * - fields: A list of TextViews that represent the fields to be validated.
 * - validationRules: A list of rules for validation. Each rule is a Triple containing a TextView, a validation function, and an error message.
 * - formHelper: An instance of FormHelper.
 */
interface ValidationConfigure {
    fun setupValidation()
}

