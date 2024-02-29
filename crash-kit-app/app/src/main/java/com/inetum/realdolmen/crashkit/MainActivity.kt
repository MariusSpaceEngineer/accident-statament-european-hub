package com.inetum.realdolmen.crashkit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.inetum.realdolmen.crashkit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingFragment: LoadingFragment

    private val securePreferences = CrashKitApp.securePreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jwtToken = securePreferences.getString("jwt_token")
        if (jwtToken != null) {
            val decodedToken = JWT(jwtToken)

            if (!decodedToken.isExpired(10)) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
                return
            } else {
                securePreferences.removeString("jwt_token")
            }
        }

        // If the JWT token is null, expired, or invalid, initialize the login/register components
        loadingFragment =
            supportFragmentManager.findFragmentById(R.id.fr_main_loading) as LoadingFragment

        val spannableText = SpannableString("Save time and hassle after a crash")
        spannableText.setSpan(
            ForegroundColorSpan(Color.RED), // change the color as needed
            29, // start index
            34, // end index
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.tvMainDescription.text = spannableText

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
            loadingFragment.showLoadingFragment()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadingFragment.hideLoadingFragment()
    }
}