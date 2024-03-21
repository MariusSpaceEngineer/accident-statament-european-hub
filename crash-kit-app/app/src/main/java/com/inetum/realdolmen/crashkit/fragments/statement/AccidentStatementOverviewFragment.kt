package com.inetum.realdolmen.crashkit.fragments.statement

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.ChangeTransform
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentStatementOverviewBinding
import com.inetum.realdolmen.crashkit.dto.AccidentImageDTO
import com.inetum.realdolmen.crashkit.dto.AccidentStatementData
import com.inetum.realdolmen.crashkit.dto.DriverDTO
import com.inetum.realdolmen.crashkit.dto.InsuranceAgency
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.InsuranceCompany
import com.inetum.realdolmen.crashkit.dto.MotorDTO
import com.inetum.realdolmen.crashkit.dto.PolicyHolderDTO
import com.inetum.realdolmen.crashkit.dto.RequestResponse
import com.inetum.realdolmen.crashkit.dto.WitnessDTO
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleACircumstancesFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleADriverFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleAInsuranceFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleAMiscellaneousFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleANewStatementFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b.VehicleBCircumstancesFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b.VehicleBDriverFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b.VehicleBInsuranceFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b.VehicleBMiscellaneousFragment
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b.VehicleBNewStatementFragment
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementData
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toByteArray
import com.inetum.realdolmen.crashkit.utils.toIsoString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AccidentStatementOverviewFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentAccidentStatementOverviewBinding? = null
    private val binding get() = _binding!!

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }
    private val apiService = CrashKitApp.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccidentStatementOverviewBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.tvStatementOverviewGeneralCardEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                NewStatementFragment(),
                "new_statement_fragment"
            )
        }

        binding.ibStatementVehicleACardExpandButton.setOnClickListener {
            toggleCardFields(
                binding.clStatementVehicleACard,
                binding.llVehicleA,
                binding.ibStatementVehicleACardExpandButton,
                R.drawable.arrow_drop_up,
                R.drawable.arrow_drop_down
            )
        }

        binding.tvStatementVehicleAPageOneEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleANewStatementFragment(),
                "vehicle_a_new_statement_fragment"
            )
        }

        binding.tvStatementVehicleAPageTwoEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleAInsuranceFragment(),
                "vehicle_a_insurance_fragment"
            )
        }

        binding.tvStatementVehicleAPageThreeEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleADriverFragment(),
                "vehicle_a_driver_fragment"
            )
        }

        binding.tvStatementVehicleAPageFourEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleACircumstancesFragment(),
                "vehicle_a_circumstances_fragment"
            )
        }

        binding.tvStatementVehicleAPageFiveEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleAMiscellaneousFragment(),
                "vehicle_a_miscellaneous_fragment"
            )
        }

        //
        binding.ibStatementVehicleBCardExpandButton.setOnClickListener {
            toggleCardFields(
                binding.clStatementVehicleBCard,
                binding.llVehicleB,
                binding.ibStatementVehicleBCardExpandButton,
                R.drawable.arrow_drop_up,
                R.drawable.arrow_drop_down
            )
        }

        binding.tvStatementVehicleBPageOneEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleBNewStatementFragment(),
                "vehicle_b_new_statement_fragment"
            )
        }

        binding.tvStatementVehicleBPageTwoEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleBInsuranceFragment(),
                "vehicle_b_insurance_fragment"
            )
        }

        binding.tvStatementVehicleBPageThreeEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleBDriverFragment(),
                "vehicle_b_driver_fragment"
            )
        }

        binding.tvStatementVehicleBPageFourEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleBCircumstancesFragment(),
                "vehicle_b_circumstances_fragment"
            )
        }

        binding.tvStatementVehicleBPageFiveEdit.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleBMiscellaneousFragment(),
                "vehicle_b_miscellaneous_fragment"
            )
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            lifecycleScope.launch {
                submitAccidentStatementData(model)
            }
        }

    }

    private suspend fun submitAccidentStatementData(model: NewStatementViewModel) {
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
            null,
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
            null,
            statementData?.vehicleBDriverDrivingLicenseExpirationDate?.toIsoString()
        )

        val drivers = listOf<DriverDTO>(driverA, driverB)

        val witness = WitnessDTO(
            statementData?.witnessName,
            statementData?.witnessAddress, statementData?.witnessPhoneNumber
        )

        val witnesses = listOf<WitnessDTO>(witness)

        val motorA = MotorDTO(
            statementData?.vehicleAMarkType,
            null,
            statementData?.vehicleARegistrationNumber,
            statementData?.vehicleACountryOfRegistration
        )

        val motorB = MotorDTO(
            statementData?.vehicleBMarkType,
            null,
            statementData?.vehicleBRegistrationNumber,
            statementData?.vehicleBCountryOfRegistration
        )

        val motors = listOf<MotorDTO>(motorA, motorB)


        val insuranceCompanyVehicleA =
            InsuranceCompany(null, statementData?.vehicleAInsuranceCompanyName)

        val insuranceAgencyVehicleA = InsuranceAgency(
            null,
            statementData?.vehicleAInsuranceAgencyName,
            statementData?.vehicleAInsuranceAgencyAddress,
            statementData?.vehicleAInsuranceAgencyCountry,
            statementData?.vehicleAInsuranceAgencyPhoneNumber,
            statementData?.vehicleAInsuranceAgencyEmail
        )

        val insuranceCertificateVehicleA = InsuranceCertificate(
            null,
            statementData?.vehicleAInsuranceCompanyPolicyNumber,
            statementData?.vehicleAInsuranceCompanyGreenCardNumber,
            statementData?.vehicleAInsuranceCertificateAvailabilityDate?.toIsoString(),
            statementData?.vehicleAInsuranceCertificateExpirationDate?.toIsoString(),
            insuranceAgencyVehicleA,
            insuranceCompanyVehicleA
        )

        val policyHolderVehicleA = PolicyHolderDTO(
            statementData?.policyHolderAFirstName,
            statementData?.policyHolderALastName,
            statementData?.policyHolderAEmail,
            statementData?.policyHolderAPhoneNumber,
            statementData?.policyHolderAAddress,
            statementData?.policyHolderAPostalCode,
            listOf<InsuranceCertificate>(insuranceCertificateVehicleA)
        )

        val insuranceCompanyVehicleB =
            InsuranceCompany(null, statementData?.vehicleBInsuranceCompanyName)

        val insuranceAgencyVehicleB = InsuranceAgency(
            null,
            statementData?.vehicleBInsuranceAgencyName,
            statementData?.vehicleBInsuranceAgencyAddress,
            statementData?.vehicleBInsuranceAgencyCountry,
            statementData?.vehicleBInsuranceAgencyPhoneNumber,
            statementData?.vehicleBInsuranceAgencyEmail
        )

        val insuranceCertificateVehicleB = InsuranceCertificate(
            null,
            statementData?.vehicleBInsuranceCompanyPolicyNumber,
            statementData?.vehicleBInsuranceCompanyGreenCardNumber,
            statementData?.vehicleBInsuranceCertificateAvailabilityDate?.toIsoString(),
            statementData?.vehicleBDriverDrivingLicenseExpirationDate?.toIsoString(),
            insuranceAgencyVehicleB,
            insuranceCompanyVehicleB
        )

        val policyHolderVehicleB = PolicyHolderDTO(
            statementData?.policyHolderBFirstName,
            statementData?.policyHolderBLastName,
            statementData?.policyHolderBEmail,
            statementData?.policyHolderBPhoneNumber,
            statementData?.policyHolderBAddress,
            statementData?.policyHolderBPostalCode,
            listOf<InsuranceCertificate>(insuranceCertificateVehicleB)
        )

        val policyHolders = listOf<PolicyHolderDTO>(policyHolderVehicleA, policyHolderVehicleB)

        val vehicleAAccidentPhotos = mutableListOf<AccidentImageDTO>()

        if (!statementData?.vehicleAAccidentPhotos.isNullOrEmpty()) {

            for (image: Bitmap in statementData?.vehicleAAccidentPhotos!!) {
                val imageByte = image.toByteArray()
                vehicleAAccidentPhotos.add(AccidentImageDTO(imageByte))
            }
        }


        val vehicleBAccidentPhotos = mutableListOf<AccidentImageDTO>()

        if (!statementData?.vehicleBAccidentPhotos.isNullOrEmpty()) {

            for (image: Bitmap in statementData?.vehicleBAccidentPhotos!!) {
                val imageByte = image.toByteArray()
                vehicleBAccidentPhotos.add(AccidentImageDTO(imageByte))
            }
        }


        val accidentStatement = AccidentStatementData(
            statementData?.dateOfAccident?.toIsoString(),
            statementData?.accidentLocation,
            statementData?.injured,
            statementData?.materialDamageToOtherVehicles,
            statementData?.materialDamageToObjects,
            null,
            null,
            null,
            null,
            vehicleAAccidentPhotos,
            statementData?.vehicleARemarks,
            vehicleBAccidentPhotos,
            statementData?.vehicleBRemarks,
            statementData?.vehicleADamageDescription,
            statementData?.vehicleBDamageDescription,
            null,
            null,
            drivers,
            witnesses,
            policyHolders,
            motors,
            null
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.createAccidentStatement(accidentStatement)
            withContext(Dispatchers.Main) {
                handleAccidentStatementResponse(response)
            }
        }
    }

    private fun handleAccidentStatementResponse(
        response: Response<RequestResponse>
    ) {
        Log.i("Request", "Request code: ${response.code()}")
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            updateGeneralInformationCardFromViewModel(statementData)
            updateVehicleACardFromViewModel(statementData)
            updateVehicleBCardFromViewModel(statementData)
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        TODO("Not yet implemented")
    }

    private fun toggleCardFields(
        cardLayout: ConstraintLayout,
        cardFieldLayout: LinearLayout,
        toggleButton: ImageButton,
        expandedImage: Int,
        collapsedImage: Int
    ) {
        if (cardFieldLayout.visibility == View.GONE) {
            cardFieldLayout.visibility = View.VISIBLE
            toggleButton.setImageResource(expandedImage)
        } else {
            cardFieldLayout.visibility = View.GONE
            toggleButton.setImageResource(collapsedImage)
        }

        TransitionManager.beginDelayedTransition(
            cardLayout,
            ChangeTransform()
        )
    }

    private fun updateGeneralInformationCardFromViewModel(statementData: StatementData) {
        //General information
        binding.tvStatementOverviewGeneralCardLabelDateOfAccident.text = buildString {
            append(binding.tvStatementOverviewGeneralCardLabelDateOfAccident.text.toString())
            statementData.dateOfAccident?.let { dateTime ->
                val formattedDateTime = dateTime.to24Format()
                if (formattedDateTime.isNotEmpty()) {
                    append(":\n$formattedDateTime")
                }
            } ?: append(":")
        }

        binding.tvStatementOverviewGeneralCardLabelLocation.text = buildString {
            append(binding.tvStatementOverviewGeneralCardLabelLocation.text.toString())
            append(if (statementData.accidentLocation.isNotEmpty()) ":\n" else ":")
            append(statementData.accidentLocation.ifEmpty { "" })
        }

        binding.tvStatementOverviewGeneralCardLabelInjured.text = buildString {
            append(binding.tvStatementOverviewGeneralCardLabelInjured.text.toString())
            append(": ")
            append(if (statementData.injured) "Yes" else "No")
        }

        if (statementData.materialDamageToOtherVehicles) {
            binding.tvStatementOverviewGeneralCardMaterialDamageOtherCars.visibility =
                View.VISIBLE
        }
        if (statementData.materialDamageToObjects) {
            binding.tvStatementOverviewGeneralCardMaterialDamageOtherObjects.visibility =
                View.VISIBLE
        }
        binding.tvStatementOverviewGeneralCardWitnessName.text = buildString {
            append(binding.tvStatementOverviewGeneralCardWitnessName.text.toString())
            append(": ")
            append(statementData.witnessName.ifEmpty { "" })
        }
        binding.tvStatementOverviewGeneralCardWitnessAddress.text = buildString {
            append(binding.tvStatementOverviewGeneralCardWitnessAddress.text.toString())
            append(": ")
            append(statementData.witnessAddress.ifEmpty { "" })
        }
        binding.tvStatementOverviewGeneralCardWitnessPhoneNumber.text = buildString {
            append(binding.tvStatementOverviewGeneralCardWitnessPhoneNumber.text.toString())
            append(": ")
            append(statementData.witnessPhoneNumber.ifEmpty { "" })
        }
    }

    private fun updateVehicleACardFromViewModel(
        statementData: StatementData
    ) {


        //Vehicle A page one
        binding.tvStatementVehicleACardPolicyHolderLastName.text = buildString {
            append(binding.tvStatementVehicleACardPolicyHolderLastName.text.toString())
            append(": ")
            append(statementData.policyHolderALastName)
        }

        binding.tvStatementVehicleACardPolicyHolderFirstName.text = buildString {
            append(binding.tvStatementVehicleACardPolicyHolderFirstName.text.toString())
            append(": ")
            append(statementData.policyHolderAFirstName)
        }

        binding.tvStatementVehicleACardPolicyHolderAddress.text = buildString {
            append(binding.tvStatementVehicleACardPolicyHolderAddress.text.toString())
            append(": ")
            append(statementData.policyHolderAAddress)
        }

        binding.tvStatementVehicleACardPolicyHolderPostalCode.text = buildString {
            append(binding.tvStatementVehicleACardPolicyHolderPostalCode.text.toString())
            append(": ")
            append(statementData.policyHolderAPostalCode)
        }

        binding.tvStatementVehicleACardPolicyHolderPhoneNumber.text = buildString {
            append(binding.tvStatementVehicleACardPolicyHolderPhoneNumber.text.toString())
            append(": ")
            append(statementData.policyHolderAPhoneNumber)
        }

        binding.tvStatementVehicleACardPolicyHolderEmail.text = buildString {
            append(binding.tvStatementVehicleACardPolicyHolderEmail.text.toString())
            append(": ")
            append(statementData.policyHolderAEmail)
        }

        binding.tvStatementVehicleACardMotorMarkType.text = buildString {
            append(binding.tvStatementVehicleACardMotorMarkType.text.toString())
            append(": ")
            append(statementData.vehicleAMarkType)
        }

        binding.tvStatementVehicleACardMotorRegistrationNumber.text = buildString {
            append(binding.tvStatementVehicleACardMotorRegistrationNumber.text.toString())
            append(if (statementData.vehicleARegistrationNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleARegistrationNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardMotorCountry.text = buildString {
            append(binding.tvStatementVehicleACardMotorCountry.text.toString())
            append(if (statementData.vehicleACountryOfRegistration.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleACountryOfRegistration.ifEmpty { "" })
        }

        //Vehicle A page two
        binding.tvStatementVehicleACardInsuranceCompanyName.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceCompanyName.text.toString())
            append(": ")
            append(statementData.vehicleAInsuranceCompanyName)
        }

        binding.tvStatementVehicleACardInsurancePolicyNumber.text = buildString {
            append(binding.tvStatementVehicleACardInsurancePolicyNumber.text.toString())
            append(if (statementData.vehicleAInsuranceCompanyPolicyNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleAInsuranceCompanyPolicyNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardInsuranceGreenCardNumber.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceGreenCardNumber.text.toString())
            append(if (statementData.vehicleAInsuranceCompanyGreenCardNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleAInsuranceCompanyGreenCardNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardInsuranceCertificateAvailabilityDate.text =
            buildString {
                append(binding.tvStatementVehicleACardInsuranceCertificateAvailabilityDate.text.toString())
                statementData.vehicleAInsuranceCertificateAvailabilityDate?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }
        binding.tvStatementVehicleACardInsuranceCertificateExpirationDate.text =
            buildString {
                append(binding.tvStatementVehicleACardInsuranceCertificateExpirationDate.text.toString())
                statementData.vehicleAInsuranceCertificateExpirationDate?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }

        binding.tvStatementVehicleACardInsuranceAgencyName.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceAgencyName.text.toString())
            append(": ")
            append(statementData.vehicleAInsuranceAgencyName)
        }

        binding.tvStatementVehicleACardInsuranceAgencyAddress.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceAgencyAddress.text.toString())
            append(if (statementData.vehicleAInsuranceAgencyAddress.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleAInsuranceAgencyAddress.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardInsuranceAgencyCountry.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceAgencyCountry.text.toString())
            append(": ")
            append(statementData.vehicleAInsuranceAgencyCountry)
        }

        binding.tvStatementVehicleACardInsuranceAgencyPhoneNumber.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceAgencyPhoneNumber.text.toString())
            append(if (statementData.vehicleAInsuranceAgencyPhoneNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleAInsuranceAgencyPhoneNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardInsuranceAgencyEmail.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceAgencyEmail.text.toString())
            append(if (statementData.vehicleAInsuranceAgencyEmail.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleAInsuranceAgencyEmail.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardInsuranceDamageCovered.text = buildString {
            append(binding.tvStatementVehicleACardInsuranceDamageCovered.text.toString())
            append(": ")
            append(if (statementData.vehicleAMaterialDamageCovered) "Yes" else "No")
        }

        //Vehicle A page three
        binding.tvStatementVehicleACardDriverLastName.text = buildString {
            append(binding.tvStatementVehicleACardDriverLastName.text.toString())
            append(": ")
            append(statementData.vehicleADriverLastName)
        }

        binding.tvStatementVehicleACardDriverFirstName.text = buildString {
            append(binding.tvStatementVehicleACardDriverFirstName.text.toString())
            append(": ")
            append(statementData.vehicleADriverFirstName)
        }

        binding.tvStatementVehicleACardDriverDateOfBirth.text =
            buildString {
                append(binding.tvStatementVehicleACardDriverDateOfBirth.text.toString())
                statementData.vehicleADriverDateOfBirth?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }

        binding.tvStatementVehicleACardDriverAddress.text = buildString {
            append(binding.tvStatementVehicleACardDriverAddress.text.toString())
            append(if (statementData.vehicleADriverAddress.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleADriverAddress.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardDriverCountry.text = buildString {
            append(binding.tvStatementVehicleACardDriverCountry.text.toString())
            append(if (statementData.vehicleADriverCountry.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleADriverCountry.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardDriverPhoneNumber.text = buildString {
            append(binding.tvStatementVehicleACardDriverPhoneNumber.text.toString())
            append(if (statementData.vehicleADriverPhoneNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleADriverPhoneNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardDriverEmail.text = buildString {
            append(binding.tvStatementVehicleACardDriverEmail.text.toString())
            append(if (statementData.vehicleADriverEmail.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleADriverEmail.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardDriverDrivingLicenseNumber.text = buildString {
            append(binding.tvStatementVehicleACardDriverDrivingLicenseNumber.text.toString())
            append(if (statementData.vehicleADriverDrivingLicenseNr.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleADriverDrivingLicenseNr.ifEmpty { "" })
        }

        binding.tvStatementVehicleACardDriverDrivingLicenseExpirationDate.text =
            buildString {
                append(binding.tvStatementVehicleACardDriverDrivingLicenseExpirationDate.text.toString())
                statementData.vehicleADriverDrivingLicenseExpirationDate?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }

        //Vehicle A page five
        binding.tvStatementVehicleARemarks.text = buildString {
            append(binding.tvStatementVehicleARemarks.text.toString())
            append(if (statementData.vehicleARemarks.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleARemarks.ifEmpty { "" })
        }

        binding.tvStatementVehicleADamageDescription.text = buildString {
            append(binding.tvStatementVehicleADamageDescription.text.toString())
            append(if (statementData.vehicleADamageDescription.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleADamageDescription.ifEmpty { "" })
        }

    }

    private fun updateVehicleBCardFromViewModel(statementData: StatementData) {

        //Vehicle B page one
        binding.tvStatementVehicleBCardPolicyHolderLastName.text = buildString {
            append(binding.tvStatementVehicleBCardPolicyHolderLastName.text.toString())
            append(": ")
            append(statementData.policyHolderBLastName)
        }

        binding.tvStatementVehicleBCardPolicyHolderFirstName.text = buildString {
            append(binding.tvStatementVehicleBCardPolicyHolderFirstName.text.toString())
            append(": ")
            append(statementData.policyHolderBFirstName)
        }

        binding.tvStatementVehicleBCardPolicyHolderAddress.text = buildString {
            append(binding.tvStatementVehicleBCardPolicyHolderAddress.text.toString())
            append(": ")
            append(statementData.policyHolderBAddress)
        }

        binding.tvStatementVehicleBCardPolicyHolderPostalCode.text = buildString {
            append(binding.tvStatementVehicleBCardPolicyHolderPostalCode.text.toString())
            append(": ")
            append(statementData.policyHolderBPostalCode)
        }

        binding.tvStatementVehicleBCardPolicyHolderPhoneNumber.text = buildString {
            append(binding.tvStatementVehicleBCardPolicyHolderPhoneNumber.text.toString())
            append(": ")
            append(statementData.policyHolderBPhoneNumber)
        }

        binding.tvStatementVehicleBCardPolicyHolderEmail.text = buildString {
            append(binding.tvStatementVehicleBCardPolicyHolderEmail.text.toString())
            append(": ")
            append(statementData.policyHolderBEmail)
        }

        binding.tvStatementVehicleBCardMotorMarkType.text = buildString {
            append(binding.tvStatementVehicleBCardMotorMarkType.text.toString())
            append(": ")
            append(statementData.vehicleBMarkType)
        }

        binding.tvStatementVehicleBCardMotorRegistrationNumber.text = buildString {
            append(binding.tvStatementVehicleBCardMotorRegistrationNumber.text.toString())
            append(if (statementData.vehicleBRegistrationNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBRegistrationNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardMotorCountry.text = buildString {
            append(binding.tvStatementVehicleBCardMotorCountry.text.toString())
            append(if (statementData.vehicleBCountryOfRegistration.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBCountryOfRegistration.ifEmpty { "" })
        }

        //Vehicle A page two
        binding.tvStatementVehicleBCardInsuranceCompanyName.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceCompanyName.text.toString())
            append(": ")
            append(statementData.vehicleBInsuranceCompanyName)
        }

        binding.tvStatementVehicleBCardInsurancePolicyNumber.text = buildString {
            append(binding.tvStatementVehicleBCardInsurancePolicyNumber.text.toString())
            append(if (statementData.vehicleBInsuranceCompanyPolicyNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBInsuranceCompanyPolicyNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardInsuranceGreenCardNumber.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceGreenCardNumber.text.toString())
            append(if (statementData.vehicleBInsuranceCompanyGreenCardNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBInsuranceCompanyGreenCardNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardInsuranceCertificateAvailabilityDate.text =
            buildString {
                append(binding.tvStatementVehicleBCardInsuranceCertificateAvailabilityDate.text.toString())
                statementData.vehicleBInsuranceCertificateAvailabilityDate?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }
        binding.tvStatementVehicleBCardInsuranceCertificateExpirationDate.text =
            buildString {
                append(binding.tvStatementVehicleBCardInsuranceCertificateExpirationDate.text.toString())
                statementData.vehicleBInsuranceCertificateExpirationDate?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }

        binding.tvStatementVehicleBCardInsuranceAgencyName.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceAgencyName.text.toString())
            append(": ")
            append(statementData.vehicleBInsuranceAgencyName)
        }

        binding.tvStatementVehicleBCardInsuranceAgencyAddress.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceAgencyAddress.text.toString())
            append(if (statementData.vehicleBInsuranceAgencyAddress.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBInsuranceAgencyAddress.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardInsuranceAgencyCountry.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceAgencyCountry.text.toString())
            append(": ")
            append(statementData.vehicleBInsuranceAgencyCountry)
        }

        binding.tvStatementVehicleBCardInsuranceAgencyPhoneNumber.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceAgencyPhoneNumber.text.toString())
            append(if (statementData.vehicleBInsuranceAgencyPhoneNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBInsuranceAgencyPhoneNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardInsuranceAgencyEmail.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceAgencyEmail.text.toString())
            append(if (statementData.vehicleBInsuranceAgencyEmail.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBInsuranceAgencyEmail.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardInsuranceDamageCovered.text = buildString {
            append(binding.tvStatementVehicleBCardInsuranceDamageCovered.text.toString())
            append(": ")
            append(if (statementData.vehicleBMaterialDamageCovered) "Yes" else "No")
        }

        //Vehicle A page three
        binding.tvStatementVehicleBCardDriverLastName.text = buildString {
            append(binding.tvStatementVehicleBCardDriverLastName.text.toString())
            append(": ")
            append(statementData.vehicleBDriverLastName)
        }

        binding.tvStatementVehicleBCardDriverFirstName.text = buildString {
            append(binding.tvStatementVehicleBCardDriverFirstName.text.toString())
            append(": ")
            append(statementData.vehicleBDriverFirstName)
        }

        binding.tvStatementVehicleBCardDriverDateOfBirth.text =
            buildString {
                append(binding.tvStatementVehicleBCardDriverDateOfBirth.text.toString())
                statementData.vehicleBDriverDateOfBirth?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }

        binding.tvStatementVehicleBCardDriverAddress.text = buildString {
            append(binding.tvStatementVehicleBCardDriverAddress.text.toString())
            append(if (statementData.vehicleBDriverAddress.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBDriverAddress.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardDriverCountry.text = buildString {
            append(binding.tvStatementVehicleBCardDriverCountry.text.toString())
            append(if (statementData.vehicleBDriverCountry.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBDriverCountry.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardDriverPhoneNumber.text = buildString {
            append(binding.tvStatementVehicleBCardDriverPhoneNumber.text.toString())
            append(if (statementData.vehicleBDriverPhoneNumber.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBDriverPhoneNumber.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardDriverEmail.text = buildString {
            append(binding.tvStatementVehicleBCardDriverEmail.text.toString())
            append(if (statementData.vehicleBDriverEmail.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBDriverEmail.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardDriverDrivingLicenseNumber.text = buildString {
            append(binding.tvStatementVehicleBCardDriverDrivingLicenseNumber.text.toString())
            append(if (statementData.vehicleBDriverDrivingLicenseNr.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBDriverDrivingLicenseNr.ifEmpty { "" })
        }

        binding.tvStatementVehicleBCardDriverDrivingLicenseExpirationDate.text =
            buildString {
                append(binding.tvStatementVehicleBCardDriverDrivingLicenseExpirationDate.text.toString())
                statementData.vehicleBDriverDrivingLicenseExpirationDate?.let { date ->
                    val formattedDate = date.to24Format()
                    if (formattedDate.isNotEmpty()) {
                        append("\n $formattedDate")
                    }
                } ?: append(":")
            }

        //Vehicle A page five
        binding.tvStatementVehicleBRemarks.text = buildString {
            append(binding.tvStatementVehicleBRemarks.text.toString())
            append(if (statementData.vehicleBRemarks.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBRemarks.ifEmpty { "" })
        }

        binding.tvStatementVehicleBDamageDescription.text = buildString {
            append(binding.tvStatementVehicleBDamageDescription.text.toString())
            append(if (statementData.vehicleBDamageDescription.isNotEmpty()) ":\n" else ":")
            append(statementData.vehicleBDamageDescription.ifEmpty { "" })
        }
    }
}