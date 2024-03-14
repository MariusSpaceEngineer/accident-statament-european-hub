package com.inetum.realdolmen.crashkit.dto

import java.time.LocalDate

data class AccidentStatementData(
    val date: String? = null,
    val location: String? = null,
    val injured: Boolean? = null,
    val damageToOtherCars: Boolean? = null,
    val damageToObjects: Boolean? = null,
    val numberOfCircumstances: Int? = null,
    val sketchOfImage: Byte? = null,
    val initialImpactVehicleA: Byte? = null,
    val initialImpactVehicleB: Byte? = null,
    val remarkVehicleA: String? = null,
    val remarkVehicleB: String? = null,
    val visibleDamageVehicleA: String? = null,
    val visibleDamageVehicleB: String? = null,
    val signatureVehicleA: String? = null,
    val signatureVehicleB: String? = null,
    val drivers: List<DriverDTO>? = null,
    val witnesses: List<WitnessDTO>? = null,
    val insuranceCertificates: List<InsuranceCertificate>? = null,
    val motors: List<MotorDTO>? = null,
    val trailers: List<TrailerDTO>? = null
)

