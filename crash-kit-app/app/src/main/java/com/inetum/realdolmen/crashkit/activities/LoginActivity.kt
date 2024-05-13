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
import com.inetum.realdolmen.crashkit.databinding.ActivityLoginBinding
import com.inetum.realdolmen.crashkit.dto.LoginData
import com.inetum.realdolmen.crashkit.dto.LoginResponse
import com.inetum.realdolmen.crashkit.fragments.LoadingFragment
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_FIELD_VALIDATION
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_LOGIN_STATUS
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_NETWORK_REQUEST
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_PARSING_ERROR
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginActivity : AppCompatActivity(), ValidationConfigure {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingFragment: LoadingFragment
    private lateinit var formHelper: FormHelper

    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> =
        mutableListOf()

    private val apiService = CrashKitApp.apiService
    private val securedPreferences = CrashKitApp.securedPreferences
    private val gson = CrashKitApp.gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupForm()

        loadingFragment = getLoadingFragment()

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }

    override fun setupValidation() {
        this.fields = mutableListOf(
            binding.etLoginEmail,
            binding.etLoginPassword
        )

        this.validationRules = mutableListOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etLoginEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etLoginEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            ),
            Triple(
                binding.etLoginPassword,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            )
        )
    }

    private fun setupForm() {
        formHelper = FormHelper(this@LoginActivity, fields)
        setupValidation()
    }

    private fun getLoadingFragment(): LoadingFragment {
        return supportFragmentManager.findFragmentById(R.id.fr_login_loading) as? LoadingFragment
            ?: throw RuntimeException("Expected LoadingFragment not found.")
    }

    private fun setupClickListeners() {
        binding.btnLoginSubmit.setOnClickListener {
            validateFields()
        }

        binding.tvLoginForgotPassword.setOnClickListener {
            startForgotCredentialsActivity()
        }
    }

    private fun validateFields() {
        formHelper.clearErrors()
        formHelper.validateFields(validationRules)
        if (fields.none { it.error != null }) {
            Log.d(TAG_FIELD_VALIDATION, "All fields are valid.")
            lifecycleScope.launch {
                performLogin(binding.etLoginEmail, binding.etLoginPassword)
            }
        } else {
            Log.d(TAG_FIELD_VALIDATION, "Fields are not invalid.")
        }
    }

    private fun startForgotCredentialsActivity() {
        val intent = Intent(this, ForgotCredentialsActivity::class.java)
        startActivity(intent)
    }

    private suspend fun performLogin(
        emailField: TextInputEditText,
        passwordField: TextInputEditText
    ) {
        val loginData = LoginData(emailField.text.toString(), passwordField.text.toString())
        loadingFragment.showLoadingFragment()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.login(loginData)
                withContext(Dispatchers.Main) {
                    handleResponse(response)
                }
            } catch (e: Exception) {
                handleNetworkError(e)
            }
        }
    }

    private suspend fun handleNetworkError(e: Exception) {
        Log.e(TAG_NETWORK_REQUEST, "Exception occurred: ", e)
        withContext(Dispatchers.Main) {
            val message = when (e) {
                is java.net.SocketTimeoutException -> getString(R.string.error_network)
                else -> getString(R.string.unknown_error)
            }
            loadingFragment.hideLoadingFragment()
            createSimpleDialog(getString(R.string.error), message)
        }
    }

    private fun handleResponse(response: Response<LoginResponse>) {
        Log.d(TAG_NETWORK_REQUEST, "Request code: ${response.code()}")
        if (response.isSuccessful) {
            handleSuccessfulResponse(response)
        } else {
            handleErrorResponse(response)
        }
    }

    private fun handleSuccessfulResponse(response: Response<LoginResponse>) {
        val loginResponse = response.body()

        if (loginResponse?.token != null) {
            if (binding.cbLoginRemember.isChecked) {
                Log.d(TAG_LOGIN_STATUS, "Login saved.")
                securedPreferences.rememberLogin()
            }
            securedPreferences.putJwtToken(loginResponse.token)
            securedPreferences.loggedAsUser()
            Log.d(TAG_LOGIN_STATUS, "Logged as user.")

            showToastAndStartHomeActivity()
        }
    }

    private fun showToastAndStartHomeActivity() {
        Toast.makeText(
            this,
            this@LoginActivity.getString(R.string.logged_in_toast),
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun handleErrorResponse(response: Response<LoginResponse>) {
        try {
            val errorResponse =
                gson.fromJson(response.errorBody()?.charStream(), LoginResponse::class.java)
            val errorMessage =
                errorResponse?.errorMessage ?: this@LoginActivity.getString(R.string.unknown_error)

            createSimpleDialog(getString(R.string.error), errorMessage)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG_PARSING_ERROR, "Error parsing error response", e)
            createSimpleDialog(getString(R.string.error), getString(R.string.unknown_error))
        }
        loadingFragment.hideLoadingFragment()
    }
}
