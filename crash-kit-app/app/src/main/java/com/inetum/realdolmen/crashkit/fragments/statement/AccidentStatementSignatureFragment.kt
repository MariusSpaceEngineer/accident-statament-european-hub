package com.inetum.realdolmen.crashkit.fragments.statement

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentStatementSignatureBinding
import com.inetum.realdolmen.crashkit.dto.AccidentImageDTO
import com.inetum.realdolmen.crashkit.dto.AccidentStatementData
import com.inetum.realdolmen.crashkit.dto.DriverDTO
import com.inetum.realdolmen.crashkit.dto.InsuranceAgency
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.InsuranceCompany
import com.inetum.realdolmen.crashkit.dto.MotorDTO
import com.inetum.realdolmen.crashkit.dto.PolicyHolderDTO
import com.inetum.realdolmen.crashkit.dto.RequestResponse
import com.inetum.realdolmen.crashkit.dto.TrailerDTO
import com.inetum.realdolmen.crashkit.dto.WitnessDTO
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.toByteArray
import com.inetum.realdolmen.crashkit.utils.toIsoString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AccidentStatementSignatureFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private val apiService = CrashKitApp.apiService

    private var _binding: FragmentAccidentStatementSignatureBinding? = null
    private val binding get() = _binding!!

    private lateinit var vehicleASignaturePad: SignaturePad
    private lateinit var vehicleBSignaturePad: SignaturePad

    private var driverASigned = false
    private var driverBSigned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccidentStatementSignatureBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::navController.isInitialized) {
            // Save the NavController's state
            outState.putBundle("nav_state", navController.saveState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        vehicleASignaturePad = binding.spStatementVehicleA
        vehicleASignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {

            override fun onStartSigning() {
                driverASigned = true
            }

            override fun onSigned() {
                // Event triggered when the pad is signed
            }

            override fun onClear() {
                driverASigned = false
            }
        })

        vehicleBSignaturePad = binding.spStatementVehicleB
        vehicleBSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {

            override fun onStartSigning() {
                driverBSigned = true
            }

            override fun onSigned() {
                // Event triggered when the pad is signed
            }

            override fun onClear() {
                driverBSigned = false
            }
        })

        binding.btnStatementVehicleADisagree.setOnClickListener {
            createCustomDialog(
                requireContext(),
                R.layout.disagree_dialog,
                R.color.secondary,
                R.color.input_field_background,
                R.drawable.disagree_dialog_background,
                "Proceed",
                "Revert"
            ) { _, _ ->
                navController.popBackStack(R.id.homeFragment, false)
            }
        }

        binding.btnStatementVehicleBDisagree.setOnClickListener {
            createCustomDialog(
                requireContext(),
                R.layout.disagree_dialog,
                R.color.secondary,
                R.color.input_field_background,
                R.drawable.disagree_dialog_background,
                "Proceed",
                "Revert"
            ) { _, _ ->
            }
        }


        binding.btnStatementAccidentSubmit.setOnClickListener {
            if (driversAgree()) {
                binding.tvStatementSignatureNeededError.visibility = View.GONE
                createCustomDialog(
                    requireContext(),
                    R.layout.submit_dialog,
                    R.color.primary800,
                    R.color.input_field_background,
                    R.drawable.submit_dialog_background,
                    "Proceed",
                    "Revert"
                ) { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {


                            val accidentStatement = createAccidentStatement(model)
                            val response = apiService.createAccidentStatement(accidentStatement)
                            withContext(Dispatchers.Main) {
                                handleAccidentStatementResponse(response)
                            }
                        } catch (e: Exception) {
                            Log.e("NetworkRequest", "Exception occurred: ", e)
                            withContext(Dispatchers.Main) {
                                val message = when (e) {
                                    is java.net.SocketTimeoutException -> requireContext().getString(
                                        R.string.error_network
                                    )

                                    else -> requireContext().getString(R.string.unknown_error)
                                }
                                requireContext().createSimpleDialog(
                                    getString(R.string.error),
                                    message
                                )
                            }
                        }
                    }
                }
                updateViewModelFromUI(model)
            } else {
                binding.tvStatementSignatureNeededError.visibility = View.VISIBLE
            }
        }

        binding.btnStatementAccidentPrevious.setOnClickListener {
            navController.popBackStack()
        }

    }

    private fun createCustomDialog(
        context: Context,
        layoutResId: Int,
        positiveButtonColorResId: Int,
        negativeButtonColorResId: Int,
        backgroundColorResId: Int,
        positiveButtonText: String,
        negativeButtonText: String,
        onPositiveClick: (Any, Any) -> Unit,
    ) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(layoutResId, null)

        builder.setView(dialogLayout)
        builder.setPositiveButton(positiveButtonText, null)
        builder.setNegativeButton(negativeButtonText, null)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(backgroundColorResId)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(ContextCompat.getColor(context, positiveButtonColorResId))
            setOnClickListener { view ->
                onPositiveClick(view, this)
                dialog.cancel()
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setTextColor(ContextCompat.getColor(context, negativeButtonColorResId))
            setOnClickListener {
                dialog.cancel()
            }
        }
    }

    private fun driversAgree(): Boolean {
        return driverASigned && driverBSigned
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        TODO("Not yet implemented")
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.driverASignature = vehicleASignaturePad.signatureBitmap
            this.driverBSignature = vehicleBSignaturePad.signatureBitmap
        }
    }

    private fun createAccidentStatement(model: NewStatementViewModel): AccidentStatementData {
        val statementData = model.statementData.value

        val driverA = DriverDTO(
            statementData?.vehicleADriverFirstName,
            statementData?.vehicleADriverLastName,
            statementData?.vehicleADriverDateOfBirth?.toIsoString(),
            statementData?.vehicleADriverAddress,
            statementData?.vehicleADriverCountry,
            statementData?.vehicleADriverPhoneNumber,
            statementData?.vehicleADriverEmail,
            statementData?.vehicleADriverDrivingLicenseNr,
            statementData?.vehicleADriverDrivingLicenseCategory,
            statementData?.vehicleADriverDrivingLicenseExpirationDate?.toIsoString()
        )

        val driverB = DriverDTO(
            statementData?.vehicleBDriverFirstName,
            statementData?.vehicleBDriverLastName,
            statementData?.vehicleBDriverDateOfBirth?.toIsoString(),
            statementData?.vehicleBDriverAddress,
            statementData?.vehicleBDriverCountry,
            statementData?.vehicleBDriverPhoneNumber,
            statementData?.vehicleBDriverEmail,
            statementData?.vehicleBDriverDrivingLicenseNr,
            statementData?.vehicleBDriverDrivingLicenseCategory,
            statementData?.vehicleBDriverDrivingLicenseExpirationDate?.toIsoString()
        )
        val drivers = listOf(driverA, driverB)

        val witness = if (statementData?.witnessIsPresent == true) {
            WitnessDTO(
                statementData.witnessName,
                statementData.witnessAddress,
                statementData.witnessPhoneNumber
            )
        } else {
            null
        }

        //The motors and trailers lists are later added
        // to their insurance certificate
        val motors = mutableListOf<MotorDTO>()
        if (statementData?.vehicleAMotorAbsent == false) {
            val motorA = MotorDTO(
                null,
                statementData.vehicleAMotorLicensePlate,
                statementData.vehicleAMotorCountryOfRegistration,
                statementData.vehicleAMotorMarkType,
            )
            motors.add(motorA)
        }

        if (statementData?.vehicleBMotorAbsent == false) {
            val motorB = MotorDTO(
                null,
                statementData.vehicleBMotorLicensePlate,
                statementData.vehicleBMotorCountryOfRegistration,
                statementData.vehicleBMotorMarkType
            )
            motors.add(motorB)
        }

        val trailers = mutableListOf<TrailerDTO>()
        if (statementData?.vehicleATrailerPresent == true) {
            val trailerA = TrailerDTO(
                null,
                statementData.vehicleATrailerLicensePlate,
                statementData.vehicleATrailerCountryOfRegistration,
                statementData.vehicleATrailerHasRegistration,
                null
            )
            trailers.add(trailerA)
        }
        if (statementData?.vehicleBTrailerPresent == true) {
            val trailerB = TrailerDTO(
                null,
                statementData.vehicleBTrailerLicensePlate,
                statementData.vehicleBTrailerCountryOfRegistration,
                statementData.vehicleBTrailerHasRegistration,
                null
            )
            trailers.add(trailerB)
        }

        val unregisteredTrailers = mutableListOf<TrailerDTO?>()
        if (statementData?.vehicleATrailerPresent == true && !statementData.vehicleATrailerHasRegistration) {
            val trailerA = TrailerDTO(null, null, null, false, "Vehicle A")
            unregisteredTrailers.add(trailerA)
        }
        if (statementData?.vehicleBTrailerPresent == true && !statementData.vehicleBTrailerHasRegistration) {
            val trailerB = TrailerDTO(null, null, null, false, "Vehicle B")
            unregisteredTrailers.add(trailerB)
        }

        //InsuranceCertificates vehicle A
        val vehicleAInsuranceCertificates = mutableListOf<InsuranceCertificate>()

        if (statementData?.vehicleAMotorAbsent == false) {
            val insuranceCompanyVehicleA =
                InsuranceCompany(null, statementData.vehicleAInsuranceCompanyName)

            val insuranceAgencyVehicleA = InsuranceAgency(
                null,
                statementData.vehicleAInsuranceAgencyName,
                statementData.vehicleAInsuranceAgencyAddress,
                statementData.vehicleAInsuranceAgencyCountry,
                statementData.vehicleAInsuranceAgencyPhoneNumber,
                statementData.vehicleAInsuranceAgencyEmail
            )

            val insuranceCertificateVehicleA = InsuranceCertificate(
                null,
                statementData.vehicleAInsuranceCompanyPolicyNumber,
                statementData.vehicleAInsuranceCompanyGreenCardNumber,
                statementData.vehicleAInsuranceCertificateAvailabilityDate?.toIsoString(),
                statementData.vehicleAInsuranceCertificateExpirationDate?.toIsoString(),
                insuranceAgencyVehicleA,
                insuranceCompanyVehicleA,
                motors.find { motorDTO ->
                    motorDTO.licensePlate == statementData.vehicleAMotorLicensePlate
                            && motorDTO.markType == statementData.vehicleAMotorMarkType
                            && motorDTO.countryOfRegistration == statementData.vehicleAMotorCountryOfRegistration
                }
            )

            vehicleAInsuranceCertificates.add(insuranceCertificateVehicleA)
        }

        if (statementData?.vehicleATrailerPresent == true && statementData.vehicleATrailerHasRegistration) {
            val insuranceCompanyVehicleA =
                InsuranceCompany(null, statementData.vehicleATrailerInsuranceCompanyName)

            val insuranceAgencyVehicleA = InsuranceAgency(
                null,
                statementData.vehicleATrailerInsuranceAgencyName,
                statementData.vehicleATrailerInsuranceAgencyAddress,
                statementData.vehicleATrailerInsuranceAgencyCountry,
                statementData.vehicleATrailerInsuranceAgencyPhoneNumber,
                statementData.vehicleATrailerInsuranceAgencyEmail
            )

            val insuranceCertificateVehicleA = InsuranceCertificate(
                null,
                statementData.vehicleATrailerInsuranceCompanyPolicyNumber,
                statementData.vehicleATrailerInsuranceCompanyGreenCardNumber,
                statementData.vehicleATrailerInsuranceCertificateAvailabilityDate?.toIsoString(),
                statementData.vehicleATrailerInsuranceCertificateExpirationDate?.toIsoString(),
                insuranceAgencyVehicleA,
                insuranceCompanyVehicleA,
                trailers.find { trailerDTO ->
                    trailerDTO.licensePlate == statementData.vehicleATrailerLicensePlate
                            && trailerDTO.countryOfRegistration == statementData.vehicleATrailerCountryOfRegistration
                })

            Log.i("trailer insurance", insuranceCertificateVehicleA.toString())

            vehicleAInsuranceCertificates.add(insuranceCertificateVehicleA)
        }

        //InsuranceCertificates vehicle B
        val vehicleBInsuranceCertificates = mutableListOf<InsuranceCertificate>()

        if (statementData?.vehicleBMotorAbsent == false) {
            val insuranceCompanyVehicleB =
                InsuranceCompany(null, statementData.vehicleBInsuranceCompanyName)

            val insuranceAgencyVehicleB = InsuranceAgency(
                null,
                statementData.vehicleBInsuranceAgencyName,
                statementData.vehicleBInsuranceAgencyAddress,
                statementData.vehicleBInsuranceAgencyCountry,
                statementData.vehicleBInsuranceAgencyPhoneNumber,
                statementData.vehicleBInsuranceAgencyEmail
            )

            val insuranceCertificateVehicleB = InsuranceCertificate(
                null,
                statementData.vehicleBInsuranceCompanyPolicyNumber,
                statementData.vehicleBInsuranceCompanyGreenCardNumber,
                statementData.vehicleBInsuranceCertificateAvailabilityDate?.toIsoString(),
                statementData.vehicleBInsuranceCertificateExpirationDate?.toIsoString(),
                insuranceAgencyVehicleB,
                insuranceCompanyVehicleB,
                motors.find { motorDTO ->
                    motorDTO.licensePlate == statementData.vehicleBMotorLicensePlate
                            && motorDTO.markType == statementData.vehicleBMotorMarkType
                            && motorDTO.countryOfRegistration == statementData.vehicleBMotorCountryOfRegistration
                }
            )

            vehicleBInsuranceCertificates.add(insuranceCertificateVehicleB)
        }

        if (statementData?.vehicleBTrailerPresent == true && statementData.vehicleBTrailerHasRegistration) {
            val insuranceCompanyVehicleB =
                InsuranceCompany(null, statementData.vehicleBTrailerInsuranceCompanyName)

            val insuranceAgencyVehicleB = InsuranceAgency(
                null,
                statementData.vehicleBTrailerInsuranceAgencyName,
                statementData.vehicleBTrailerInsuranceAgencyAddress,
                statementData.vehicleBTrailerInsuranceAgencyCountry,
                statementData.vehicleBTrailerInsuranceAgencyPhoneNumber,
                statementData.vehicleBTrailerInsuranceAgencyEmail
            )

            val insuranceCertificateVehicleB = InsuranceCertificate(
                null,
                statementData.vehicleBTrailerInsuranceCompanyPolicyNumber,
                statementData.vehicleBTrailerInsuranceCompanyGreenCardNumber,
                statementData.vehicleBTrailerInsuranceCertificateAvailabilityDate?.toIsoString(),
                statementData.vehicleBTrailerInsuranceCertificateExpirationDate?.toIsoString(),
                insuranceAgencyVehicleB,
                insuranceCompanyVehicleB,
                trailers.find { trailerDTO ->
                    trailerDTO.licensePlate == statementData.vehicleBTrailerLicensePlate
                            && trailerDTO.countryOfRegistration == statementData.vehicleBTrailerCountryOfRegistration
                })

            Log.i("trailer insurance", insuranceCertificateVehicleB.toString())

            vehicleBInsuranceCertificates.add(insuranceCertificateVehicleB)
        }

        val policyHolderVehicleA = PolicyHolderDTO(
            statementData?.policyHolderAFirstName,
            statementData?.policyHolderALastName,
            statementData?.policyHolderAEmail,
            statementData?.policyHolderAPhoneNumber,
            statementData?.policyHolderAAddress,
            statementData?.policyHolderAPostalCode,
            vehicleAInsuranceCertificates
        )

        val policyHolderVehicleB = PolicyHolderDTO(
            statementData?.policyHolderBFirstName,
            statementData?.policyHolderBLastName,
            statementData?.policyHolderBEmail,
            statementData?.policyHolderBPhoneNumber,
            statementData?.policyHolderBAddress,
            statementData?.policyHolderBPostalCode,
            vehicleBInsuranceCertificates
        )

        val policyHolders = listOf(policyHolderVehicleA, policyHolderVehicleB)

        val vehicleAAccidentPhotos: MutableList<AccidentImageDTO>? =
            if (!statementData?.vehicleAAccidentPhotos.isNullOrEmpty()) {
                val tempList = mutableListOf<AccidentImageDTO>()
                for (image: Bitmap in statementData?.vehicleAAccidentPhotos!!) {
                    val imageByte = image.toByteArray()
                    tempList.add(AccidentImageDTO(imageByte))
                }
                tempList
            } else null

        val vehicleBAccidentPhotos: MutableList<AccidentImageDTO>? =
            if (!statementData?.vehicleBAccidentPhotos.isNullOrEmpty()) {
                val tempList = mutableListOf<AccidentImageDTO>()
                for (image: Bitmap in statementData?.vehicleBAccidentPhotos!!) {
                    val imageByte = image.toByteArray()
                    tempList.add(AccidentImageDTO(imageByte))
                }
                tempList
            } else null


        val vehicleACircumstances = model.vehicleACircumstances.value?.size ?: 0
        val vehicleBCircumstances = model.vehicleBCircumstances.value?.size ?: 0
        val amountOfCircumstances = vehicleACircumstances + vehicleBCircumstances

        return AccidentStatementData(
            statementData?.dateOfAccident?.toIsoString(),
            statementData?.accidentLocation,
            statementData?.injured,
            statementData?.materialDamageToOtherVehicles,
            statementData?.materialDamageToObjects,
            amountOfCircumstances,
            statementData?.accidentSketch?.toByteArray(),
            drivers,
            witness,
            policyHolders,
            unregisteredTrailers,
            model.vehicleACircumstances.value?.map
            { it.text.toString() },
            statementData?.vehicleAPointOfImpactSketch?.toByteArray(),
            statementData?.vehicleADamageDescription,
            vehicleAAccidentPhotos,
            statementData?.vehicleARemarks,
            statementData?.driverASignature?.toByteArray(),
            model.vehicleBCircumstances.value?.map
            { it.text.toString() },
            statementData?.vehicleBPointOfImpactSketch?.toByteArray(),
            statementData?.vehicleBDamageDescription,
            vehicleBAccidentPhotos,
            statementData?.vehicleBRemarks,
            statementData?.driverBSignature?.toByteArray()
        )
    }

    private fun handleAccidentStatementResponse(
        response: Response<RequestResponse>
    ) {
        Log.i("Request", "Request code: ${response.code()}")
    }


}