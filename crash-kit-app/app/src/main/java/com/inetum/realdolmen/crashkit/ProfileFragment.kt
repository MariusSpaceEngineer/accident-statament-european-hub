package com.inetum.realdolmen.crashkit

import android.content.Intent
import android.os.Bundle
import android.transition.ChangeTransform
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.inetum.realdolmen.crashkit.databinding.FragmentProfileBinding
import com.inetum.realdolmen.crashkit.dto.PolicyHolderDTO
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var client = CrashKitApp.httpClient
    private val securePreferences = CrashKitApp.securePreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val personalInformationViews = listOf(
            binding.etProfilePersonalFirstNameValue,
            binding.etProfilePersonalLastNameValue,
            binding.etProfilePersonalEmailValue,
            binding.etProfilePersonalAddressValue,
            binding.etProfilePersonalPhoneValue
        )

        val request = createRequest();

        if (request != null) {

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    handleResponse(response)
                }
            })
        }

        binding.ibProfilePersonalCardButton.setOnClickListener {

            val details = binding.llProfilePersonal.visibility
            if (details == View.GONE) {
                binding.llProfilePersonal.visibility = View.VISIBLE
                binding.ibProfilePersonalCardButton.setImageResource(R.drawable.arrow_drop_up)
            } else {
                binding.llProfilePersonal.visibility = View.GONE
                binding.ibProfilePersonalCardButton.setImageResource(R.drawable.arrow_drop_down)

            }

            TransitionManager.beginDelayedTransition(
                binding.clProfilePersonalCard,
                ChangeTransform()
            )
        }

        var beingEdited = false

        binding.tvProfilePersonalCardEdit.setOnClickListener {
            if (!beingEdited) {
                beingEdited = true
                binding.tvProfilePersonalCardEdit.setText("Cancel")

                for (view in personalInformationViews) {
                    view.isEnabled = true
                }

                binding.btnProfilePersonalCardUpdate.visibility = View.VISIBLE


                val details = binding.llProfilePersonal.visibility
                if (details == View.GONE) {
                    binding.llProfilePersonal.visibility = View.VISIBLE
                    binding.ibProfilePersonalCardButton.setImageResource(R.drawable.arrow_drop_up)
                }

                TransitionManager.beginDelayedTransition(
                    binding.clProfilePersonalCard,
                    ChangeTransform()
                )
            } else {
                beingEdited = false
                binding.tvProfilePersonalCardEdit.setText(R.string.edit)

                for (view in personalInformationViews) {
                    view.isEnabled = false
                }

                binding.btnProfilePersonalCardUpdate.visibility = View.GONE
            }
        }

        binding.btnProfilePersonalCardUpdate.setOnClickListener {

            if (areFieldsValid(personalInformationViews)) {
                val requestBody = createRequestBody(
                    binding.etProfilePersonalFirstNameValue,
                    binding.etProfilePersonalLastNameValue,
                    binding.etProfilePersonalEmailValue,
                    binding.etProfilePersonalPhoneValue,
                    binding.etProfilePersonalAddressValue,
                    binding.etProfilePersonalPostalCodeValue
                )

                val postRequest = createPostRequest(requestBody);

                if (postRequest != null) {

                    client.newCall(postRequest).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            handleResponse(response)
                        }
                    })
                }
            } else {
                Log.e("Fields", "All fields need to be filled first")
            }
        }

    }

    private fun areFieldsValid(fields: List<TextInputEditText>): Boolean {
        var allFieldsValid = true
        for (field in fields) {
            if (field.text.toString().trim().isEmpty()) {
                allFieldsValid = false
            }
        }
        return allFieldsValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createRequestBody(
        firstNameField: TextInputEditText,
        lastNameField: TextInputEditText,
        emailField: TextInputEditText,
        phoneNumberField: TextInputEditText,
        addressField: TextInputEditText,
        postalCodeField: TextInputEditText
    ): RequestBody {
        val jsonObject = JSONObject()
        jsonObject.put("firstName", firstNameField.text.toString())
        jsonObject.put("lastName", lastNameField.text.toString())
        jsonObject.put("email", emailField.text.toString())
        jsonObject.put("phoneNumber", phoneNumberField.text.toString())
        jsonObject.put("address", addressField.text.toString())
        jsonObject.put("postalCode", postalCodeField.text.toString())

        val mediaType = "application/json; charset=utf-8".toMediaType()
        return jsonObject.toString().toRequestBody(mediaType)
    }

    private fun createPostRequest(body: RequestBody): Request {
        val jwtToken = securePreferences.getString("jwt_token")
        return Request.Builder()
            .url("https://10.0.2.2:8080/api/v1/user/profile")
            .header("Authorization", "Bearer $jwtToken")
            .put(body)
            .build()
    }


    private fun createRequest(): Request? {
        val jwtToken = securePreferences.getString("jwt_token")
        if (jwtToken != null) {
            return Request.Builder()
                .url("https://10.0.2.2:8080/api/v1/user/profile")
                .header("Authorization", "Bearer $jwtToken")
                .build()
        }
        return null;
    }

    private fun handleResponse(response: Response) {
        response.use {
            val res = response.body?.string()
            if (res != null) {
                val jsonObject = JSONObject(res)

                val policyHolder = PolicyHolderDTO(
                    jsonObject.getString("firstName"),
                    jsonObject.getString("lastName"),
                    jsonObject.getString("email"),
                    jsonObject.getString("phoneNumber"),
                    jsonObject.getString("address"),
                    jsonObject.getString("postalCode")
                )

                // Check if the response was served from the cache or the network
                if (response.networkResponse != null) {
                    Log.i("OkHttp", "Response was served from the network.")
                } else if (response.cacheResponse != null) {
                    Log.i("OkHttp", "Response was served from the cache.")
                }

                activity?.runOnUiThread {
                    binding.etProfilePersonalFirstNameValue.setText(policyHolder.firstName)
                    binding.etProfilePersonalLastNameValue.setText(policyHolder.lastName)
                    binding.etProfilePersonalEmailValue.setText(policyHolder.email)
                    binding.etProfilePersonalPhoneValue.setText(policyHolder.phoneNumber)
                    binding.etProfilePersonalAddressValue.setText(policyHolder.address)
                    binding.etProfilePersonalPostalCodeValue.setText(policyHolder.postalCode)
                }
            }


        }
    }

}