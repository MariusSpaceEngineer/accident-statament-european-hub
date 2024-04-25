package com.inetum.realdolmen.crashkit.utils

import android.graphics.Bitmap
import android.graphics.Point
import android.widget.CheckBox
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

    val vehicleACircumstances: MutableLiveData<List<CheckBox>> by lazy {
        MutableLiveData<List<CheckBox>>()
    }

    val vehicleBCircumstances: MutableLiveData<List<CheckBox>> by lazy {
        MutableLiveData<List<CheckBox>>()
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
    var vehicleAMotorAbsent: Boolean= false,
    var vehicleAMotorMarkType: String = "",
    var vehicleAMotorLicensePlate: String = "",
    var vehicleAMotorCountryOfRegistration: String = "",
    var vehicleATrailerPresent: Boolean= false,
    var vehicleATrailerHasRegistration: Boolean= false,
    var vehicleATrailerLicensePlate: String= "",
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
    var vehicleATrailerInsuranceCompanyName: String = "",
    var vehicleATrailerInsuranceCompanyPolicyNumber: String = "",
    var vehicleATrailerInsuranceCompanyGreenCardNumber: String = "",
    var vehicleATrailerInsuranceCertificateAvailabilityDate: LocalDate? = null,
    var vehicleATrailerInsuranceCertificateExpirationDate: LocalDate? = null,
    var vehicleATrailerInsuranceAgencyName: String = "",
    var vehicleATrailerInsuranceAgencyAddress: String = "",
    var vehicleATrailerInsuranceAgencyCountry: String = "",
    var vehicleATrailerInsuranceAgencyPhoneNumber: String = "",
    var vehicleATrailerInsuranceAgencyEmail: String = "",
    var vehicleATrailerMaterialDamageCovered: Boolean = false,
    var vehicleADriverIsPolicyHolder: Boolean= false,
    var vehicleADriverLastName: String = "",
    var vehicleADriverFirstName: String = "",
    var vehicleADriverDateOfBirth: LocalDate? = null,
    var vehicleADriverAddress: String = "",
    var vehicleADriverCountry: String = "",
    var vehicleADriverPhoneNumber: String = "",
    var vehicleADriverEmail: String = "",
    var vehicleADriverDrivingLicenseCategory: String= "",
    var vehicleADriverDrivingLicenseNr: String = "",
    var vehicleADriverDrivingLicenseExpirationDate: LocalDate? = null,
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
    var vehicleBMotorAbsent: Boolean= false,
    var vehicleBMotorMarkType: String = "",
    var vehicleBMotorLicensePlate: String = "",
    var vehicleBMotorCountryOfRegistration: String = "",
    var vehicleBTrailerPresent: Boolean= false,
    var vehicleBTrailerHasRegistration: Boolean= false,
    var vehicleBTrailerLicensePlate: String= "",
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
    var vehicleBTrailerInsuranceCompanyName: String = "",
    var vehicleBTrailerInsuranceCompanyPolicyNumber: String = "",
    var vehicleBTrailerInsuranceCompanyGreenCardNumber: String = "",
    var vehicleBTrailerInsuranceCertificateAvailabilityDate: LocalDate? = null,
    var vehicleBTrailerInsuranceCertificateExpirationDate: LocalDate? = null,
    var vehicleBTrailerInsuranceAgencyName: String = "",
    var vehicleBTrailerInsuranceAgencyAddress: String = "",
    var vehicleBTrailerInsuranceAgencyCountry: String = "",
    var vehicleBTrailerInsuranceAgencyPhoneNumber: String = "",
    var vehicleBTrailerInsuranceAgencyEmail: String = "",
    var vehicleBTrailerMaterialDamageCovered: Boolean = false,
    var vehicleBDriverIsPolicyHolder: Boolean= false,
    var vehicleBDriverLastName: String = "",
    var vehicleBDriverFirstName: String = "",
    var vehicleBDriverDateOfBirth: LocalDate? = null,
    var vehicleBDriverAddress: String = "",
    var vehicleBDriverCountry: String = "",
    var vehicleBDriverPhoneNumber: String = "",
    var vehicleBDriverEmail: String = "",
    var vehicleBDriverDrivingLicenseCategory: String= "",
    var vehicleBDriverDrivingLicenseNr: String = "",
    var vehicleBDriverDrivingLicenseExpirationDate: LocalDate? = null,
    var vehicleBAccidentPhotos: MutableList<Bitmap>? = null,
    var vehicleBPointOfImpactSketch: Bitmap? = null,
    var vehicleBRemarks: String = "",
    var vehicleBDamageDescription: String = "",
    var accidentSketch: Bitmap? = null,
    var driverASignature: Bitmap?= null,
    var driverBSignature: Bitmap?= null
)
