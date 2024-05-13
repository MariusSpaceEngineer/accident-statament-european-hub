package com.inetum.realdolmen.crashkit.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonSyntaxException
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.ActivityRegisterBinding
import com.inetum.realdolmen.crashkit.dto.LoginResponse
import com.inetum.realdolmen.crashkit.dto.RegisterData
import com.inetum.realdolmen.crashkit.dto.RegisterResponse
import com.inetum.realdolmen.crashkit.fragments.LoadingFragment
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.IValidationConfigure
import com.inetum.realdolmen.crashkit.utils.LogTags
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), IValidationConfigure {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var loadingFragment: LoadingFragment
    private lateinit var formHelper: FormHelper

    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String?>> =
        mutableListOf()

    private val apiService = CrashKitApp.apiService
    private val securedPreference = CrashKitApp.securedPreferences
    private val gson = CrashKitApp.gson

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupForm()

        loadingFragment = getLoadingFragment()

        setupClickListeners()
    }

    override fun setupValidation() {
        this.fields = mutableListOf(
            binding.etRegisterFirstName,
            binding.etRegisterLastName,
            binding.etRegisterEmail,
            binding.etRegisterPhoneNumber,
            binding.etRegisterAddress,
            binding.etRegisterPostalCode,
            binding.etRegisterPassword,
            binding.etRegisterConfirmPassword
        )

        this.validationRules = mutableListOf<Triple<EditText, (String?) -> Boolean, String?>>(
            Triple(
                binding.etRegisterFirstName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etRegisterFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etRegisterLastName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etRegisterLastName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etRegisterEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etRegisterEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            ),
            Triple(
                binding.etRegisterPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etRegisterPhoneNumber,
                { value -> !value.isNullOrEmpty() && value.any { it.isLetter() } },
                formHelper.errors.noLettersAllowed
            ),
            Triple(
                binding.etRegisterAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etRegisterPostalCode,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etRegisterConfirmPassword,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            )
        )
    }

    private fun setupClickListeners() {
        binding.btnRegisterSubmit.setOnClickListener {
            validateForm()
            if (fields.none { it.error != null }) {
                Log.d(LogTags.TAG_FIELD_VALIDATION, "All fields are valid.")
                lifecycleScope.launch {
                    performRegister(
                        binding.etRegisterFirstName,
                        binding.etRegisterLastName,
                        binding.etRegisterEmail,
                        binding.etRegisterPhoneNumber,
                        binding.etRegisterAddress,
                        binding.etRegisterPostalCode,
                        binding.etRegisterPassword
                    )
                }
            }
        }
    }

    private fun getLoadingFragment(): LoadingFragment {
        return supportFragmentManager.findFragmentById(R.id.fr_register_loading) as? LoadingFragment
            ?: throw RuntimeException("Expected LoadingFragment not found.")
    }

    private fun validateForm() {
        formHelper.clearErrors()
        formHelper.validateFields(validationRules)
        validatePasswords()
    }

    private fun validatePasswords() {
        val passwordError =
            formHelper.validatePassword(binding.etRegisterPassword.text.toString())
        if (passwordError != null) {
            binding.etRegisterPassword.error = passwordError
        }
        if (binding.etRegisterPassword.text.toString() != binding.etRegisterConfirmPassword.text.toString()) {
            Log.d(LogTags.TAG_FIELD_VALIDATION, "Passwords are not the same.")
            binding.etRegisterConfirmPassword.error =
                this@RegisterActivity.getString(R.string.passwords_not_the_same)
        }
    }

    private fun setupForm() {
        formHelper = FormHelper(this@RegisterActivity, fields)
        setupValidation()
    }

    private suspend fun performRegister(
        firstNameField: TextInputEditText,
        lastNameField: TextInputEditText,
        emailField: TextInputEditText,
        phoneNumberField: TextInputEditText,
        addressField: TextInputEditText,
        postalCodeField: TextInputEditText,
        passwordField: TextInputEditText
    ) {
        val registerData = RegisterData(
            firstNameField.text.toString(),
            lastNameField.text.toString(),
            emailField.text.toString(),
            phoneNumberField.text.toString(),
            addressField.text.toString(),
            postalCodeField.text.toString(),
            passwordField.text.toString()
        )
        loadingFragment.showLoadingFragment()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.register(registerData)
                withContext(Dispatchers.Main) {
                    handleResponse(response)
                }
            } catch (e: Exception) {
                handleNetworkError(e)
            }
        }
    }

    private suspend fun handleNetworkError(e: Exception) {
        Log.e(LogTags.TAG_NETWORK_REQUEST, "Exception occurred: ", e)
        withContext(Dispatchers.Main) {
            val message = when (e) {
                is java.net.SocketTimeoutException -> getString(R.string.error_network)
                else -> getString(R.string.unknown_error)
            }
            loadingFragment.hideLoadingFragment()
            createSimpleDialog(getString(R.string.error), message)
        }
    }

    private fun handleResponse(response: Response<RegisterResponse>) {
        if (response.isSuccessful) {
            handleSuccessfulResponse(response)
        } else {
            handleErrorResponse(response)
        }
    }

    private fun handleErrorResponse(response: Response<RegisterResponse>) {
        try {
            val errorResponse =
                gson.fromJson(response.errorBody()?.charStream(), LoginResponse::class.java)
            val errorMessage =
                errorResponse?.errorMessage
                    ?: this@RegisterActivity.getString(R.string.unknown_error)

            createSimpleDialog(getString(R.string.error), errorMessage)
        } catch (e: JsonSyntaxException) {
            Log.e(LogTags.TAG_PARSING_ERROR, "Error parsing error response", e)
            createSimpleDialog(getString(R.string.error), getString(R.string.unknown_error))
        }
        loadingFragment.hideLoadingFragment()
    }

    private fun handleSuccessfulResponse(response: Response<RegisterResponse>) {
        val registerResponse = response.body()

        if (registerResponse?.token != null) {
            securedPreference.putJwtToken(registerResponse.token)
            securedPreference.loggedAsUser()
            Log.d(LogTags.TAG_LOGIN_STATUS, "Logged as user.")

            showToastAndStartHomeActivity()
        }
    }

    private fun showToastAndStartHomeActivity() {
        Toast.makeText(
            this,
            this@RegisterActivity.getString(R.string.logged_in_toast),
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this, HomeActivity::class.java))
    }
}