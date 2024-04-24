package com.inetum.realdolmen.crashkit.dto

data class TrailerDTO(
    @Transient override val id: Int?,
    @Transient override val licensePlate: String,
    @Transient override val countryOfRegistration: String,
    val hasRegistration: Boolean
) : Vehicle(id, licensePlate, countryOfRegistration)
