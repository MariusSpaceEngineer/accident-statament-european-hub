package com.inetum.realdolmen.crashkit.dto

sealed class Vehicle(open val id: Int?, open val licensePlate: String, open val countryOfRegistration: String)