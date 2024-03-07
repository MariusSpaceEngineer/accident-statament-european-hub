package com.inetum.realdolmen.crashkit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.fragments.LoadingFragment
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.ActivityRegisterBinding
import com.inetum.realdolmen.crashkit.dto.LoginResponse
import com.inetum.realdolmen.crashkit.dto.RegisterData
import com.inetum.realdolmen.crashkit.dto.RegisterResponse
import com.inetum.realdolmen.crashkit.utils.areFieldsValid
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var loadingFragment: LoadingFragment

    private val apiService = CrashKitApp.apiService
    private val securedPreference = CrashKitApp.securedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fieldFirstName = binding.etRegisterFirstName
        val fieldLastName = binding.etRegisterLastName
        val fieldEmail = binding.etRegisterEmail
        val fieldPassword = binding.etRegisterPassword
        val fields = mapOf(
            binding.etRegisterFirstName to "First Name is required!",
            binding.etRegisterLastName to "Last Name is required!",
            binding.etRegisterEmail to "Email is required!",
            binding.etRegisterPassword to "Password is required!"
        )

        loadingFragment =
            supportFragmentManager.findFragmentById(R.id.fr_register_loading) as LoadingFragment

        binding.btnRegisterSubmit.setOnClickListener {
            if (fields.areFieldsValid()) {
                if (binding.etRegisterPassword.text.toString() != binding.etRegisterConfirmPassword.text.toString()) {
                    binding.etRegisterPassword.error = "Passwords are not the same!"
                    binding.etRegisterConfirmPassword.error = "Passwords are not the same!"
                } else {
                    lifecycleScope.launch {
                        performRegister(fieldFirstName, fieldLastName, fieldEmail, fieldPassword)
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }

    private suspend fun performRegister(
        firstNameField: TextInputEditText, lastNameField: TextInputEditText,
        emailField: TextInputEditText,
        passwordField: TextInputEditText
    ) {
        val registerData = RegisterData(
            firstNameField.text.toString(),
            lastNameField.text.toString(),
            emailField.text.toString(),
            passwordField.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.register(registerData)
            withContext(Dispatchers.Main) {
                handleResponse(response)
            }
        }
    }

    private fun handleResponse(response: Response<RegisterResponse>) {

        if (response.isSuccessful) {
            val registerResponse = response.body()

            if (registerResponse?.token != null) {

                loadingFragment.showLoadingFragment()
                securedPreference.putJwtToken(registerResponse.token)
                securedPreference.loggedAsUser()

                Toast.makeText(
                    this@RegisterActivity,
                    "Registration successful",
                    Toast.LENGTH_LONG
                )
                    .show()
                startActivity(Intent(this, HomeActivity::class.java))
            }
        } else {
            val errorResponse =
                Gson().fromJson(response.errorBody()?.charStream(), LoginResponse::class.java)
            val errorMessage = errorResponse?.errorMessage ?: "Unknown error"
            this.createSimpleDialog(getString(R.string.error), errorMessage)
        }
    }
}