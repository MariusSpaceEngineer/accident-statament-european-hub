package com.inetum.realdolmen.crashkit.dto

data class PolicyHolderDTO(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val postalCode: String? = null,
    val insuranceCertificate: InsuranceCertificate? = null
)