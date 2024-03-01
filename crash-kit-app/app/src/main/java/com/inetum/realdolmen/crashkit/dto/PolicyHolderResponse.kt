package com.inetum.realdolmen.crashkit.dto

data class PolicyHolderResponse(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val address: String?,
    val postalCode: String?,
    val insuranceCertificate: InsuranceCertificate?
)

data class InsuranceCertificate(
    val id: Int?,
    val policyNumber: String?,
    val greenCardNumber: String?,
    val availabilityDate: String?,
    val expirationDate: String?,
    val insuranceAgency: InsuranceAgency?,
    val insuranceCompany: InsuranceCompany?
)

data class InsuranceAgency(
    val id: Int?,
    val name: String?,
    val address: String?,
    val country: String?,
    val phoneNumber: String?,
    val email: String?
)

data class InsuranceCompany(
    val id: Int?,
    val name: String?
)

