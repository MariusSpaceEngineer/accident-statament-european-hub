package com.inetum.realdolmen.crashkit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewStatementViewModel : ViewModel() {
    val statementData: MutableLiveData<StatementData> by lazy {
        MutableLiveData<StatementData>(StatementData())
    }
}

data class StatementData(
    var dateOfAccident: String = "",
    var accidentLocation: String = "",
    var materialDamageToOtherVehicles: Boolean = false,
    var materialDamageToObjects: Boolean = false,
    var witnessName: String = "",
    var witnessAddress: String = "",
    var witnessPhoneNumber: String = "",
    var policyHolderALastName: String = "",
    var policyHolderAFirstName: String = "",
    var policyHolderAAddress: String = "",
    var policyHolderAPhoneNumber: String = "",
    var policyHolderAEmail: String = "",
    var vehicleAMarkType: String = "",
    var vehicleARegistrationNumber: String = "",
    var vehicleACountryOfRegistration: String = "",
    var vehicleAInsuranceCompanyName: String = "",
    var vehicleAInsuranceCompanyPolicyNumber: String = "",
    var vehicleAInsuranceCompanyGreenCardNumber: String = "",
    var vehicleAInsuranceCertificateAvailabilityDate: String = "",
    var vehicleAInsuranceCertificateExpirationDate: String = "",
    var vehicleAInsuranceAgencyName: String = "",
    var vehicleAInsuranceAgencyAddress: String = "",
    var vehicleAInsuranceAgencyCountry: String = "",
    var vehicleAInsuranceAgencyPhoneNumber: String = "",
    var vehicleAInsuranceAgencyEmail: String = "",
    var vehicleAMaterialDamageCovered: Boolean = false,
    var vehicleADriverLastName: String = "",
    var vehicleADriverFirstName: String = "",
    var vehicleADriverDateOfBirth: String = "",
    var vehicleADriverAddress: String = "",
    var vehicleADriverCountry: String = "",
    var vehicleADriverPhoneNumber: String = "",
    var vehicleADriverEmail: String = "",
    var vehicleADriverDrivingLicenseNr: String = "",
    var vehicleADriverDrivingLicenseExpirationDate: String = "",
    var vehicleAParkedStopped: Boolean = false,
    var vehicleALeavingParkingOpeningDoor: Boolean = false,
    var vehicleAEnteringParking: Boolean = false,
    var vehicleAEmergingParkPrivateGroundTrack: Boolean = false,
    var vehicleAEnteringCarParkPrivateGroundTrack: Boolean = false,
    var vehicleAEnteringRoundabout: Boolean = false,
    var vehicleACirculatingRoundabout: Boolean = false,
    var vehicleAStrikingRearSameDirectionLane: Boolean = false,
    var vehicleAGoingSameDirectionDifferentLane: Boolean = false,
    var vehicleAChangingLane: Boolean = false,
    var vehicleAOvertaking: Boolean = false,
    var vehicleATurningRight: Boolean = false,
    var vehicleATurningLeft: Boolean = false,
    var vehicleAReversing: Boolean = false,
    var vehicleAEncroachingLaneOppositeDirection: Boolean = false,
    var vehicleAComingRightJunction: Boolean = false,
    var vehicleANotObservedSignRedLight: Boolean = false,
    var vehicleARemarks: String = "",
    var vehicleADamageDescription: String = ""
)
