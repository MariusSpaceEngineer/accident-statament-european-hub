package com.inetum.realdolmen.crashkit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inetum.realdolmen.crashkit.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingFragment: LoadingFragment
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fieldEmail = binding.etLoginEmail
        val fieldPassword = binding.etLoginPassword
        val loginButton = binding.btnLoginSubmit

        val intent = Intent(this, HomeActivity::class.java)

        loadingFragment =
            supportFragmentManager.findFragmentById(R.id.fr_login_loading) as LoadingFragment


        loginButton.setOnClickListener {

            if (fieldEmail.text.toString().trim().isEmpty()) {
                fieldEmail.error = "Email is required!"
            }
            if (fieldPassword.text.toString().trim().isEmpty()) {
                fieldPassword.error = "Password is required!"
            }
            if (fieldEmail.text.toString().trim().isNotEmpty() && fieldPassword.text.toString()
                    .trim()
                    .isNotEmpty()
            ) {

                val jsonObject = JSONObject()
                jsonObject.put("email", fieldEmail.text.toString())
                jsonObject.put("password", fieldPassword.text.toString())

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/auth/login")
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            println(response.body!!.string())

                            runOnUiThread {
                                loadingFragment.showLoadingFragment()
                                Toast.makeText(this@LoginActivity, "Logged in", Toast.LENGTH_LONG)
                                    .show()

                                startActivity(intent)
                            }
                        }
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }
}