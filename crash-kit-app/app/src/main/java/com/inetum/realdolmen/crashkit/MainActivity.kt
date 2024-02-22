package com.inetum.realdolmen.crashkit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import com.inetum.realdolmen.crashkit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val spannableText = SpannableString("Save time and hassle after a crash")
        spannableText.setSpan(
            ForegroundColorSpan(Color.RED), // change the color as needed
            29, // start index
            34, // end index
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.tvMainDescription.text = spannableText


        binding.btnMainLoginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}