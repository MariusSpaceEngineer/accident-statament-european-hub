package com.inetum.realdolmen.crashkit.utils

import android.graphics.Bitmap
import android.graphics.Point
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inetum.realdolmen.crashkit.accidentsketch.IAccidentDrawable
import java.time.LocalDate
import java.time.LocalDateTime

class NewStatementViewModel : ViewModel() {
    val statementData: MutableLiveData<StatementData> by lazy {
        MutableLiveData<StatementData>(StatementData())
    }

    val accidentSketchShapes: MutableLiveData<List<Triple<IAccidentDrawable, Point, TextView?>>> by lazy {
        MutableLiveData<List<Triple<IAccidentDrawable, Point, TextView?>>>()
    }

    val pointOfImpactVehicleASketchShapes: MutableLiveData<List<Pair<IAccidentDrawable, Point>>> by lazy {
        MutableLiveData<List<Pair<IAccidentDrawable, Point>>>()
    }

    val pointOfImpactVehicleBSketchShapes: MutableLiveData<List<Pair<IAccidentDrawable, Point>>> by lazy {
        MutableLiveData<List<Pair<IAccidentDrawable, Point>>>()
    }
}

//TODO look which properties can't be null

data class StatementData(
    var dateOfAccident: LocalDateTime? = null,
    var accidentLocation: String = "",
    var injured: Boolean = false,
    var materialDamageToOtherVehicles: Boolean = false,
    var materialDamageToObjects: Boolean = false,
    var witnessIsPresent: Boolean= true,
    var witnessName: String = "",
    var witnessAddress: String = "",
    var witnessPhoneNumber: String = "",
    var policyHolderALastName: String = "",
    var policyHolderAFirstName: String = "",
    var policyHolderAAddress: String = "",
    var policyHolderAPostalCode: String = "",
    var policyHolderAPhoneNumber: String = "",
    var policyHolderAEmail: String = "",
    var vehicleAMotorPresent: Boolean= false,
    var vehicleAMarkType: String = "",
    var vehicleARegistrationNumber: String = "",
    var vehicleACountryOfRegistration: String = "",
    var vehicleATrailerPresent: Boolean= false,
    var vehicleATrailerRegistrationNumber: String= "",
    var vehicleATrailerCountryOfRegistration: String = "",
    var vehicleAInsuranceCompanyName: String = "",
    var vehicleAInsuranceCompanyPolicyNumber: String = "",
    var vehicleAInsuranceCompanyGreenCardNumber: String = "",
    var vehicleAInsuranceCertificateAvailabilityDate: LocalDate? = null,
    var vehicleAInsuranceCertificateExpirationDate: LocalDate? = null,
    var vehicleAInsuranceAgencyName: String = "",
    var vehicleAInsuranceAgencyAddress: String = "",
    var vehicleAInsuranceAgencyCountry: String = "",
    var vehicleAInsuranceAgencyPhoneNumber: String = "",
    var vehicleAInsuranceAgencyEmail: String = "",
    var vehicleAMaterialDamageCovered: Boolean = false,
    var vehicleADriverIsPolicyHolder: Boolean= false,
    var vehicleADriverLastName: String = "",
    var vehicleADriverFirstName: String = "",
    var vehicleADriverDateOfBirth: LocalDate? = null,
    var vehicleADriverAddress: String = "",
    var vehicleADriverCountry: String = "",
    var vehicleADriverPhoneNumber: String = "",
    var vehicleADriverEmail: String = "",
    var vehicleADriverDrivingLicenseNr: String = "",
    var vehicleADriverDrivingLicenseExpirationDate: LocalDate? = null,
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
    var vehicleAAccidentPhotos: MutableList<Bitmap>? = null,
    var vehicleAPointOfImpactSketch: Bitmap? = null,
    var vehicleARemarks: String = "",
    var vehicleADamageDescription: String = "",
    var policyHolderBLastName: String = "",
    var policyHolderBFirstName: String = "",
    var policyHolderBAddress: String = "",
    var policyHolderBPostalCode: String = "",
    var policyHolderBPhoneNumber: String = "",
    var policyHolderBEmail: String = "",
    var vehicleBMotorPresent: Boolean= false,
    var vehicleBMarkType: String = "",
    var vehicleBRegistrationNumber: String = "",
    var vehicleBCountryOfRegistration: String = "",
    var vehicleBTrailerPresent: Boolean= false,
    var vehicleBTrailerRegistrationNumber: String= "",
    var vehicleBTrailerCountryOfRegistration: String = "",
    var vehicleBInsuranceCompanyName: String = "",
    var vehicleBInsuranceCompanyPolicyNumber: String = "",
    var vehicleBInsuranceCompanyGreenCardNumber: String = "",
    var vehicleBInsuranceCertificateAvailabilityDate: LocalDate? = null,
    var vehicleBInsuranceCertificateExpirationDate: LocalDate? = null,
    var vehicleBInsuranceAgencyName: String = "",
    var vehicleBInsuranceAgencyAddress: String = "",
    var vehicleBInsuranceAgencyCountry: String = "",
    var vehicleBInsuranceAgencyPhoneNumber: String = "",
    var vehicleBInsuranceAgencyEmail: String = "",
    var vehicleBMaterialDamageCovered: Boolean = false,
    var vehicleBDriverIsPolicyHolder: Boolean= false,
    var vehicleBDriverLastName: String = "",
    var vehicleBDriverFirstName: String = "",
    var vehicleBDriverDateOfBirth: LocalDate? = null,
    var vehicleBDriverAddress: String = "",
    var vehicleBDriverCountry: String = "",
    var vehicleBDriverPhoneNumber: String = "",
    var vehicleBDriverEmail: String = "",
    var vehicleBDriverDrivingLicenseNr: String = "",
    var vehicleBDriverDrivingLicenseExpirationDate: LocalDate? = null,
    var vehicleBParkedStopped: Boolean = false,
    var vehicleBLeavingParkingOpeningDoor: Boolean = false,
    var vehicleBEnteringParking: Boolean = false,
    var vehicleBEmergingParkPrivateGroundTrack: Boolean = false,
    var vehicleBEnteringCarParkPrivateGroundTrack: Boolean = false,
    var vehicleBEnteringRoundabout: Boolean = false,
    var vehicleBCirculatingRoundabout: Boolean = false,
    var vehicleBStrikingRearSameDirectionLane: Boolean = false,
    var vehicleBGoingSameDirectionDifferentLane: Boolean = false,
    var vehicleBChangingLane: Boolean = false,
    var vehicleBOvertaking: Boolean = false,
    var vehicleBTurningRight: Boolean = false,
    var vehicleBTurningLeft: Boolean = false,
    var vehicleBReversing: Boolean = false,
    var vehicleBEncroachingLaneOppositeDirection: Boolean = false,
    var vehicleBComingRightJunction: Boolean = false,
    var vehicleBNotObservedSignRedLight: Boolean = false,
    var vehicleBAccidentPhotos: MutableList<Bitmap>? = null,
    var vehicleBPointOfImpactSketch: Bitmap? = null,
    var vehicleBRemarks: String = "",
    var vehicleBDamageDescription: String = "",
    var accidentSketch: Bitmap? = null,
    var driverASignature: Bitmap?= null,
    var driverBSignature: Bitmap?= null
)
