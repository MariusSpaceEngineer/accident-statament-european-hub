package com.inetum.realdolmen.crashkit.utils

import android.widget.TextView
import com.inetum.realdolmen.crashkit.helpers.FormHelper

interface ValidationConfigure {
    fun setupValidation(
        errors: StatementDataErrors,
        fields: List<TextView>,
        validationRules: List<Triple<TextView, (String?) -> Boolean, String>>,
        formHelper: FormHelper

    )
}
