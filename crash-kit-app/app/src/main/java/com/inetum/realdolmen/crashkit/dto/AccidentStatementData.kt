package com.inetum.realdolmen.crashkit.dto

data class AccidentStatementData(
    val date: String? = null,
    val location: String? = null,
    val injured: Boolean? = null,
    val damageToOtherCars: Boolean? = null,
    val damageToObjects: Boolean? = null,
    val numberOfCircumstances: Int? = null,
    val sketchOfAccident: ByteArray? = null,
    val drivers: List<DriverDTO>? = null,
    val witness: WitnessDTO? = null,
    val policyHolders: List<PolicyHolderDTO>? = null,
    val unregisteredTrailers: List<TrailerDTO?>? = null,
    val vehicleACircumstances: List<String>? = null,
    val vehicleAInitialImpactSketch: ByteArray? = null,
    val vehicleAVisibleDamageDescription: String? = null,
    val vehicleAAccidentImages: MutableList<AccidentImageDTO>? = null,
    val vehicleARemark: String? = null,
    val vehicleASignature: ByteArray? = null,
    val vehicleBCircumstances: List<String>? = null,
    val vehicleBInitialImpactSketch: ByteArray? = null,
    val vehicleBVisibleDamageDescription: String? = null,
    val vehicleBAccidentImages: MutableList<AccidentImageDTO>? = null,
    val vehicleBRemark: String? = null,
    val vehicleBSignature: ByteArray? = null,
    )

