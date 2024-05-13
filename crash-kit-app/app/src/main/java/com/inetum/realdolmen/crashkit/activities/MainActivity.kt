package com.inetum.realdolmen.crashkit.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.ActivityMainBinding
import com.inetum.realdolmen.crashkit.fragments.LoadingFragment
import com.inetum.realdolmen.crashkit.utils.SecuredPreferences

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingFragment: LoadingFragment

    private lateinit var securedPreferences: SecuredPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        securedPreferences= CrashKitApp.securedPreferences

        // If the JWT token is null, expired, or invalid, show the MainActivity
        checkLoginStatus()

        loadingFragment =
            supportFragmentManager.findFragmentById(R.id.fr_main_loading) as? LoadingFragment
                ?: throw RuntimeException("Expected LoadingFragment not found.")

        //Changes the color of the last word of the description to red
        changeDescriptionTextToRed()

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }

    //Checks to see if the JWT token of the user is still valid if he has one
    @VisibleForTesting
    internal fun checkLoginStatus() {
        Log.d("LoginStatus", "Checking login status...")
        if (securedPreferences.isLoginRemembered()) {
            Log.d("LoginStatus", "User has remembered login.")
            val jwtToken = securedPreferences.getJwtToken()
            if (jwtToken != null) {
                Log.d("LoginStatus", "JWT token exists.")
                val decodedToken = JWT(jwtToken)
                isTokenExpired(decodedToken)
            } else {
                Log.d("LoginStatus", "No JWT token found. Remove JWT token.")
                securedPreferences.deleteJwtToken()
            }
        } else {
            Log.d("LoginStatus", "User has not remembered login. Remove any unwanted JWT token.")
            securedPreferences.deleteJwtToken()
        }
    }

    private fun isTokenExpired(decodedToken: JWT) {
        if (!decodedToken.isExpired(10)) {
            Log.d("LoginStatus", "JWT token is valid. Navigating to HomeActivity.")
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        } else {
            Log.d("LoginStatus", "JWT token is invalid. Remove JWT token.")
            securedPreferences.deleteJwtToken()
        }
    }

    private fun changeDescriptionTextToRed() {
        val text = getString(R.string.main_description)
        // Locate the position of the last space character in the description text.
        // We then add 1 to this index to move past the space character.
        // The resulting 'lastSpaceIndex' points to the first character of the last word in the description text.
        val lastSpaceIndex = text.lastIndexOf(" ") + 1
        val spannableText = SpannableString(text)
        spannableText.setSpan(
            ForegroundColorSpan(Color.RED),
            lastSpaceIndex, // start index
            text.length, // end index
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