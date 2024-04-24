package com.inetum.realdolmen.crashkit.dto

data class MotorDTO(
    @Transient override val id: Int?,
    @Transient override val licensePlate: String,
    @Transient override val countryOfRegistration: String,
    var markType: String
) : Vehicle(id, licensePlate, countryOfRegistration)
