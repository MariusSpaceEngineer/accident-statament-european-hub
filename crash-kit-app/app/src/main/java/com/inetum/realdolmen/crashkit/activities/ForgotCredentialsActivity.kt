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
import com.inetum.realdolmen.crashkit.utils.areFieldsValid
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ForgotCredentialsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotCredentialsBinding
    private val apiService = CrashKitApp.apiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotCredentialsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fields = mapOf(
            binding.etForgotCredentialsEmail to "Email is required!",
            binding.etForgotCredentialsNewPassword to "New password is required!",
            binding.etForgotCredentialsNewPasswordConfirm to "Confirm password is required!",
        )

        binding.btnForgotCredentialsSubmit.setOnClickListener {
            if (fields.areFieldsValid()) {
                if (binding.etForgotCredentialsNewPassword.text.toString() != binding.etForgotCredentialsNewPasswordConfirm.text.toString()) {
                    binding.etForgotCredentialsNewPassword.error = "Passwords are not the same!"
                    binding.etForgotCredentialsNewPasswordConfirm.error =
                        "Passwords are not the same!"
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val resetPasswordData = ResetPasswordData(
                                binding.etForgotCredentialsEmail.text.toString(),
                                null,
                                null
                            )
                            apiService.resetPassword(resetPasswordData)

                            withContext(Dispatchers.Main) {
                                // Show the dialog to enter the new password
                                createCustomDialog(
                                    this@ForgotCredentialsActivity,
                                    R.layout.password_reset_dialog,
                                    R.color.input_field_background,
                                    R.color.input_field_background,
                                    "Submit",
                                    "Cancel",
                                    R.drawable.reset_password_dialog_background,
                                ) { securityCode, errorMessage ->
                                    // Make the API call to update the password
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val newPasswordData = ResetPasswordData(
                                                binding.etForgotCredentialsEmail.text.toString(),
                                                binding.etForgotCredentialsNewPassword.text.toString(),
                                                securityCode
                                            )
                                            Log.i("data", newPasswordData.toString())
                                            val response =
                                                apiService.updatePassword(newPasswordData)
                                            withContext(Dispatchers.Main) {
                                                if (errorMessage != null) {
                                                    handleResponse(response, errorMessage)
                                                }
                                            }
                                        } catch (e: Exception) {
                                            // Handle exception
                                        }
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("NetworkRequest", "Exception occurred: ", e)
                            withContext(Dispatchers.Main) {
                                if (e is java.net.SocketTimeoutException) {
                                    this@ForgotCredentialsActivity.createSimpleDialog(
                                        getString(R.string.error),
                                        this@ForgotCredentialsActivity.getString(
                                            R.string.error_network
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createCustomDialog(
        context: Context,
        layoutResId: Int,
        positiveButtonColorResId: Int,
        negativeButtonColorResId: Int,
        positiveButtonText: String,
        negativeButtonText: String?,
        backgroundColorResId: Int,
        shouldRedirectOnDismiss: Boolean = false, // New parameter with default value as false
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

        if (shouldRedirectOnDismiss) { // Only set the dismiss listener if the parameter is true
            dialog.setOnDismissListener {
                // Redirects to the new activity when the dialog is dismissed
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }


    private fun handleResponse(response: Response<RequestResponse>, errorMessage: TextView) {
        Log.i("Response", response.message())

        if (response.isSuccessful) {
            closeKeyboard(this)
            createCustomDialog(
                this@ForgotCredentialsActivity,
                R.layout.password_reset_success_dialog,
                R.color.input_field_background,
                R.color.input_field_background,
                "OK",
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

}

