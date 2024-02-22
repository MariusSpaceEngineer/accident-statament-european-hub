package com.inetum.realdolmen.crashkit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inetum.realdolmen.crashkit.databinding.ActivityRegisterBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fieldFirstName = binding.etRegisterFirstName
        val fieldLastName = binding.etRegisterLastName
        val fieldEmail = binding.etRegisterEmail
        val fieldPassword = binding.etRegisterPassword

        val fields = listOf(
            binding.etRegisterFirstName to "First Name is required!",
            binding.etRegisterLastName to "Last Name is required!",
            binding.etRegisterEmail to "Email is required!",
            binding.etRegisterPassword to "Password is required!"
        )

        binding.btnRegisterSubmit.setOnClickListener {
            var allFieldsValid = true
            for ((field, errorMessage) in fields) {
                if (field.text.toString().trim().isEmpty()) {
                    field.error = errorMessage
                    allFieldsValid = false
                }
            }

            if (binding.etRegisterPassword.text.toString() != binding.etRegisterConfirmPassword.text.toString()) {
                binding.etRegisterPassword.error = "Passwords are not the same!"
                binding.etRegisterConfirmPassword.error = "Passwords are not the same!"
                allFieldsValid = false
            }

            if (allFieldsValid) {


                val jsonObject = JSONObject()
                jsonObject.put("firstName", fieldFirstName.text.toString())
                jsonObject.put("lastName", fieldLastName.text.toString())
                jsonObject.put("email", fieldEmail.text.toString())
                jsonObject.put("password", fieldPassword.text.toString())

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/v1/auth/register")
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
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registered",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                            }
                        }
                    }
                })
            }
        }

    }
}