package com.inetum.realdolmen.crashkit

import android.os.Bundle
import android.transition.ChangeTransform
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inetum.realdolmen.crashkit.databinding.FragmentProfileBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
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
            val details = binding.glProfilePersonal.visibility
            if (details == View.GONE) {
                binding.glProfilePersonal.visibility = View.VISIBLE
                binding.ibProfilePersonalCardButton.setImageResource(R.drawable.arrow_drop_up)
            } else {
                binding.glProfilePersonal.visibility = View.GONE
                binding.ibProfilePersonalCardButton.setImageResource(R.drawable.arrow_drop_down)

            }

            TransitionManager.beginDelayedTransition(
                binding.clProfilePersonalCard,
                ChangeTransform()
            )
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                val firstName = jsonObject.getString("firstName")
                val lastName = jsonObject.getString("lastName")
                val email = jsonObject.getString("email")

                // Check if the response was served from the cache or the network
                if (response.networkResponse != null) {
                    Log.i("OkHttp", "Response was served from the network.")
                } else if (response.cacheResponse != null) {
                    Log.i("OkHttp", "Response was served from the cache.")
                }

                activity?.runOnUiThread {
                    binding.tvProfilePersonalFirstNameValue.text = firstName
                    binding.tvProfilePersonalLastNameValue.text = lastName
                    binding.tvProfilePersonalEmailValue.text = email
                }
            }


        }
    }

}