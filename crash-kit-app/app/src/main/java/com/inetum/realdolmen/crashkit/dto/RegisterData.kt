package com.inetum.realdolmen.crashkit.dto

data class RegisterData(
    val firstName:String,
    val lastName:String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val postalCode: String,
    val password: String
)
