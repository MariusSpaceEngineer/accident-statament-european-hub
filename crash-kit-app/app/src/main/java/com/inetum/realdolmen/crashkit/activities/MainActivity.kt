package com.inetum.realdolmen.crashkit.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.ActivityMainBinding
import com.inetum.realdolmen.crashkit.fragments.LoadingFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingFragment: LoadingFragment

    private val securedPreferences = CrashKitApp.securedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If the JWT token is null, expired, or invalid, initialize the login/register components
        checkLoginStatus()

        loadingFragment =
            supportFragmentManager.findFragmentById(R.id.fr_main_loading) as LoadingFragment

        //Changes the color of the last word of the description to red
        changeDescriptionTextToRed()

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }

    private fun checkLoginStatus() {
        if (securedPreferences.isLoginRemembered()) {

            val jwtToken = securedPreferences.getJwtToken()
            if (jwtToken != null) {
                val decodedToken = JWT(jwtToken)

                if (!decodedToken.isExpired(10)) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    return
                }
            }
        }
        securedPreferences.deleteJwtToken()
    }

    private fun changeDescriptionTextToRed() {
        val spannableText = SpannableString("Save time and hassle after a crash")
        spannableText.setSpan(
            ForegroundColorSpan(Color.RED), // change the color as needed
            29, // start index
            34, // end index
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.tvMainDescription.text = spannableText
    }

    private fun setupClickListeners() {
        binding.btnMainLoginRedirect.setOnClickListener {
            loadingFragment.showLoadingFragment()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnMainRegisterRedirect.setOnClickListener {
            loadingFragment.showLoadingFragment()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnMainGuestRedirect.setOnClickListener {
            securedPreferences.loggedAsGuest()
            loadingFragment.showLoadingFragment()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}