package com.inetum.realdolmen.crashkit.dto

data class PolicyHolderResponse(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val address: String?,
    val postalCode: String?,
    val insuranceCertificates: List<InsuranceCertificate>?
)

data class PolicyHolderVehicleBResponse(
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
    val insuranceCompany: InsuranceCompany?,
    val vehicle: Vehicle?
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

