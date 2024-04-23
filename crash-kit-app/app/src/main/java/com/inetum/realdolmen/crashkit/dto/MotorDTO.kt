package com.inetum.realdolmen.crashkit.dto

data class MotorDTO(
    @Transient override val licensePlate: String,
    @Transient override val countryOfRegistration: String,
    val markType: String
) : Vehicle(licensePlate, countryOfRegistration)
