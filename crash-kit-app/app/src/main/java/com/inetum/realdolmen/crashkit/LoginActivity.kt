package com.inetum.realdolmen.crashkit

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.inetum.realdolmen.crashkit.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingFragment: LoadingFragment

    private val client = CrashKitApp.httpClient
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
            if (areFieldsValid(fields)) {
                performLogin(emailField, passwordField, rememberCheckbox)
            }
        }
    }

    private fun areFieldsValid(fields: Map<TextInputEditText, String>): Boolean {
        var allFieldsValid = true
        for ((field, errorMessage) in fields) {
            if (field.text.toString().trim().isEmpty()) {
                field.error = errorMessage
                allFieldsValid = false
            }
        }
        return allFieldsValid
    }

    private fun performLogin(
        emailField: TextInputEditText,
        passwordField: TextInputEditText,
        rememberLogin: CheckBox
    ) {
        val jsonObject = createRequestBody(emailField, passwordField)
        val request = createRequest(jsonObject)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    createSimpleDialog(
                        getString(R.string.error),
                        getString(R.string.network_error)
                    )
                }
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, rememberLogin)
            }
        })
    }

    private fun createRequestBody(
        emailField: TextInputEditText,
        passwordField: TextInputEditText
    ): RequestBody {
        val jsonObject = JSONObject()
        jsonObject.put("email", emailField.text.toString())
        jsonObject.put("password", passwordField.text.toString())

        val mediaType = "application/json; charset=utf-8".toMediaType()
        return jsonObject.toString().toRequestBody(mediaType)
    }

    private fun createRequest(body: RequestBody): Request {
        return Request.Builder()
            .url("https://10.0.2.2:8080/api/v1/auth/login")
            .post(body)
            .build()
    }

    private fun handleResponse(response: Response, rememberLogin: CheckBox) {
        response.use {
            val jsonObject = JSONObject(response.body!!.string())
            if (!response.isSuccessful) {
                val errorMessage = jsonObject.getString("errorMessage")

                runOnUiThread {
                    createSimpleDialog(getString(R.string.error), errorMessage)
                }

            } else {

                //TODO: Add logic to prevent user from going to
                // the profile fragment if he is a quest/not logged in
                /*if (rememberLogin.isChecked) {*/
                val token = jsonObject.getString("token")
                securePreference.putString("jwt_token", token)
                //}

                runOnUiThread {
                    loadingFragment.showLoadingFragment()
                    Toast.makeText(
                        this@LoginActivity,
                        "Logged in",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }
        }
    }

    private fun createSimpleDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }


}