package com.inetum.realdolmen.crashkit.dto

import java.time.LocalDate

data class DriverDTO(
    var firstName: String? = null,
    var lastName: String? = null,
    var birthday: String? = null,
    var address: String? = null,
    var country: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var drivingLicenseNr: String? = null,
    var category: String? = null,
    var drivingLicenseExpirationDate: String? = null
)

