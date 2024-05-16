package com.inetum.realdolmen.crashkit.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.ActivityForgotCredentialsBinding
import com.inetum.realdolmen.crashkit.dto.RequestResponse
import com.inetum.realdolmen.crashkit.dto.ResetPasswordData
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.IValidationConfigure
import com.inetum.realdolmen.crashkit.utils.LogTags
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_NETWORK_REQUEST
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException

class ForgotCredentialsActivity : AppCompatActivity(), IValidationConfigure {
    private lateinit var binding: ActivityForgotCredentialsBinding
    private val apiService = CrashKitApp.apiService
    private lateinit var formHelper: FormHelper


    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> =
        mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotCredentialsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupForm()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnForgotCredentialsSubmit.setOnClickListener {
            validateForm()
            if (fields.none { it.error != null }) {
                resetPassword()
            }
        }
    }

    private fun resetPassword() {
        showPasswordResetDialog()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resetPasswordData = createResetPasswordData()
                apiService.resetPassword(resetPasswordData)

            } catch (_: Exception) {
            }
        }
    }

    private fun createResetPasswordData(): ResetPasswordData {
        return ResetPasswordData(
            binding.etForgotCredentialsEmail.text.toString(),
            null,
            null
        )
    }

    private fun showPasswordResetDialog() {
        createCustomDialog(
            this@ForgotCredentialsActivity,
            R.layout.password_reset_dialog,
            R.color.input_field_background,
            R.color.input_field_background,
            getString(R.string.submit_button),
            getString(R.string.cancel_button),
            R.drawable.reset_password_dialog_background,
        ) { securityCode, errorMessage ->
            if (securityCode != null) {
                updatePassword(securityCode, errorMessage)
            }
        }
    }

    private fun updatePassword(securityCode: String, errorMessage: TextView?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newPasswordData = createNewPasswordData(securityCode)
                val response = apiService.updatePassword(newPasswordData)
                withContext(Dispatchers.Main) {
                    if (errorMessage != null) {
                        handleResponse(response, errorMessage)
                    }
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun createNewPasswordData(securityCode: String): ResetPasswordData {
        return ResetPasswordData(
            binding.etForgotCredentialsEmail.text.toString(),
            binding.etForgotCredentialsNewPassword.text.toString(),
            securityCode
        )
    }

    private suspend fun handleException(e: Exception) {
        Log.e("NetworkRequest", "Exception occurred: ", e)
        withContext(Dispatchers.Main) {
            if (e is SocketTimeoutException) {
                this@ForgotCredentialsActivity.createSimpleDialog(
                    getString(R.string.error),
                    this@ForgotCredentialsActivity.getString(
                        R.string.error_network
                    )
                )
            }
        }
    }

    private fun setupForm() {
        formHelper = FormHelper(this@ForgotCredentialsActivity, fields)
        setupValidation()
    }

    private fun validateForm() {
        formHelper.clearErrors()
        formHelper.validateFields(validationRules)
        validatePasswords()
    }

    private fun validatePasswords() {
        val passwordError =
            formHelper.validatePassword(binding.etForgotCredentialsNewPassword.text.toString())
        if (passwordError != null) {
            binding.etForgotCredentialsNewPassword.error = passwordError
        }
        if (binding.etForgotCredentialsNewPassword.text.toString() != binding.etForgotCredentialsNewPasswordConfirm.text.toString()) {
            Log.d(LogTags.TAG_FIELD_VALIDATION, "Passwords are not the same.")
            binding.etForgotCredentialsNewPasswordConfirm.error =
                this@ForgotCredentialsActivity.getString(R.string.passwords_not_the_same)
        }
    }
    /**
     * This function creates a custom dialog with the specified layout and buttons.
     *
     * @param context The context in which the dialog should be created.
     * @param layoutResId The resource ID of the layout to inflate for the dialog.
     * @param positiveButtonColorResId The resource ID of the color to use for the positive button text.
     * @param negativeButtonColorResId The resource ID of the color to use for the negative button text.
     * @param positiveButtonText The text to display on the positive button.
     * @param negativeButtonText The text to display on the negative button. If null, no negative button is created.
     * @param backgroundColorResId The resource ID of the color to use for the dialog's background.
     * @param shouldRedirectOnDismiss If true, the function will start a new LoginActivity when the dialog is dismissed.
     * @param onPositiveClick A lambda function to execute when the positive button is clicked. It receives the text from the security code EditText and the error message TextView as parameters.
     */
    private fun createCustomDialog(
        context: Context,
        layoutResId: Int,
        positiveButtonColorResId: Int,
        negativeButtonColorResId: Int,
        positiveButtonText: String,
        negativeButtonText: String?,
        backgroundColorResId: Int,
        shouldRedirectOnDismiss: Boolean = false,
        onPositiveClick: (String?, TextView?) -> Unit,
    ) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(layoutResId, null)

        // Check if the security code field exists in the dialog layout
        val securityCodeEditText =
            dialogLayout.findViewById<TextInputEditText>(R.id.et_forgot_credentials_email)
        val errorMessageTextView =
            dialogLayout.findViewById<TextView>(R.id.tv_forgot_credentials_error_message)

        builder.setView(dialogLayout)
        builder.setPositiveButton(positiveButtonText, null)
        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, null)
        }
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(backgroundColorResId)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(ContextCompat.getColor(context, positiveButtonColorResId))
            setOnClickListener {
                val securityCode = securityCodeEditText.text.toString()
                onPositiveClick(securityCode, errorMessageTextView)
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setTextColor(ContextCompat.getColor(context, negativeButtonColorResId))
            setOnClickListener {
                dialog.cancel()
            }
        }

        if (shouldRedirectOnDismiss) {
            dialog.setOnDismissListener {
                // Redirects to the new activity when the dialog is dismissed
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }

    private fun handleResponse(response: Response<RequestResponse>, errorMessage: TextView) {
        Log.d(TAG_NETWORK_REQUEST, response.message())

        if (response.isSuccessful) {
            closeKeyboard(this)
            createCustomDialog(
                this@ForgotCredentialsActivity,
                R.layout.password_reset_success_dialog,
                R.color.input_field_background,
                R.color.input_field_background,
                getString(R.string.ok),
                null,
                R.drawable.submit_dialog_background,
                true
            ) { _, _ ->
                // On successful password reset, close all dialogs and navigate to login activity
                finish()
                startActivity(Intent(this@ForgotCredentialsActivity, LoginActivity::class.java))
            }
        } else {
            errorMessage.visibility = View.VISIBLE
        }
    }

    private fun closeKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun setupValidation() {
        this.fields = mutableListOf(
            binding.etForgotCredentialsEmail,
            binding.etForgotCredentialsNewPassword,
            binding.etForgotCredentialsNewPasswordConfirm,
        )

        this.validationRules = mutableListOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etForgotCredentialsEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etForgotCredentialsEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            ),
            Triple(
                binding.etForgotCredentialsNewPassword,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etForgotCredentialsNewPasswordConfirm,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            )
        )
    }

}

