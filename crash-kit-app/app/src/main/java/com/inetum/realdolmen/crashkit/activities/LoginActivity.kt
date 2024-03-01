package com.inetum.realdolmen.crashkit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.fragments.LoadingFragment
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.ActivityLoginBinding
import com.inetum.realdolmen.crashkit.dto.LoginData
import com.inetum.realdolmen.crashkit.dto.LoginResponse
import com.inetum.realdolmen.crashkit.utils.areFieldsValid
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingFragment: LoadingFragment

    private val apiService = CrashKitApp.apiService
    private val securePreference = CrashKitApp.securePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val emailField = binding.etLoginEmail
        val passwordField = binding.etLoginPassword
        val fields = mapOf(
            emailField to getString(R.string.email_required),
            passwordField to getString(R.string.password_required),
        )

        val rememberCheckbox = binding.cbLoginRemember
        val loginButton = binding.btnLoginSubmit

        loadingFragment =
            supportFragmentManager.findFragmentById(R.id.fr_login_loading) as LoadingFragment

        loginButton.setOnClickListener {
            if (fields.areFieldsValid()) {

                lifecycleScope.launch {
                    performLogin(emailField, passwordField, rememberCheckbox)
                }
            }
        }
    }

    private suspend fun performLogin(
        emailField: TextInputEditText,
        passwordField: TextInputEditText,
        rememberLogin: CheckBox
    ) {
        val loginData = LoginData(emailField.text.toString(), passwordField.text.toString())

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.login(loginData)
            withContext(Dispatchers.Main) {
                handleResponse(response, rememberLogin)
            }
        }
    }

    private fun handleResponse(response: Response<LoginResponse>, rememberLogin: CheckBox) {

        if (response.isSuccessful) {
            val loginResponse = response.body()

            if (loginResponse?.token != null) {

                loadingFragment.showLoadingFragment()

                if (rememberLogin.isChecked) {
                    securePreference.putJwtToken(loginResponse.token)
                }

                Toast.makeText(
                    this@LoginActivity,
                    "Logged in",
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

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }
}