package com.inetum.realdolmen.crashkit.dto

data class ResetPasswordData(
    val email: String?,
    val newPassword: String?,
    val securityCode: String?
)
