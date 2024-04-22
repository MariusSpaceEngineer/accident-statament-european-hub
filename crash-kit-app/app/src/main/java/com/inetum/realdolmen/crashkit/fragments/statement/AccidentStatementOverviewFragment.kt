package com.inetum.realdolmen.crashkit.fragments.statement

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.transition.ChangeTransform
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.adapters.ImageAdapter
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
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementData
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
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
    private lateinit var navController: NavController

    private var _binding: FragmentAccidentStatementOverviewBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        // Reset orientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()

        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }
        // Inflate the layout for this fragment
        _binding = FragmentAccidentStatementOverviewBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUIFromViewModel(model)

        binding.tvStatementOverviewGeneralCardEdit.setOnClickListener {
            navController.navigate(R.id.newStatementFragment)
        }

        binding.tvStatementOverviewGeneralCardSketchEdit.setOnClickListener {
            navController.navigate(R.id.accidentSketchFragment)
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
            navController.navigate(R.id.vehicleANewStatementFragment)
        }

        binding.tvStatementVehicleAPageTwoEdit.setOnClickListener {
            navController.navigate(R.id.vehicleAInsuranceFragment)
        }

        binding.tvStatementVehicleAPageThreeEdit.setOnClickListener {
            navController.navigate(R.id.vehicleADriverFragment)
        }

        binding.tvStatementVehicleAPageFourEdit.setOnClickListener {
            navController.navigate(R.id.vehicleACircumstancesFragment)
        }

        binding.tvStatementVehicleAPageFiveEdit.setOnClickListener {
            navController.navigate(R.id.vehicleAMiscellaneousFragment)
        }


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
            navController.navigate(R.id.vehicleBNewStatementFragment)
        }

        binding.tvStatementVehicleBPageTwoEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBInsuranceFragment)
        }

        binding.tvStatementVehicleBPageThreeEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBDriverFragment)
        }

        binding.tvStatementVehicleBPageFourEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBCircumstancesFragment)
        }

        binding.tvStatementVehicleBPageFiveEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBMiscellaneousFragment)
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

        val drivers = listOf(driverA, driverB)

        val witness = WitnessDTO(
            statementData?.witnessName,
            statementData?.witnessAddress, statementData?.witnessPhoneNumber
        )

        val witnesses = listOf(witness)

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

        val motors = listOf(motorA, motorB)


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
            listOf(insuranceCertificateVehicleA)
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
            listOf(insuranceCertificateVehicleB)
        )

        val policyHolders = listOf(policyHolderVehicleA, policyHolderVehicleB)

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

    private fun setBoldAndNormalText(view: TextView, label: String, appendedText: String?) {
        val spannable = SpannableStringBuilder(label)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        appendedText?.let { text ->
            val start = spannable.length
            spannable.append(":\n$text")
            val end = spannable.length
            spannable.setSpan(
                StyleSpan(Typeface.NORMAL),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } ?: spannable.append(":")
        view.text = spannable
    }

    private fun updateGeneralInformationCardFromViewModel(statementData: StatementData) {
        //General information
        setBoldAndNormalText(
            binding.tvStatementOverviewGeneralCardLabelDateOfAccident,
            binding.tvStatementOverviewGeneralCardLabelDateOfAccident.text.toString(),
            statementData.dateOfAccident?.to24Format()
        )

        setBoldAndNormalText(
            binding.tvStatementOverviewGeneralCardLabelLocation,
            binding.tvStatementOverviewGeneralCardLabelLocation.text.toString(),
            statementData.accidentLocation
        )

        setBoldAndNormalText(
            binding.tvStatementOverviewGeneralCardLabelInjured,
            binding.tvStatementOverviewGeneralCardLabelInjured.text.toString(),
            if (statementData.injured) requireContext().getString(R.string.yes) else requireContext().getString(
                R.string.no
            )
        )

        if (statementData.materialDamageToOtherVehicles || statementData.materialDamageToObjects) {
            binding.tvStatementOverviewGeneralCardLabelMaterialDamage.visibility = View.VISIBLE
            if (statementData.materialDamageToOtherVehicles)
                binding.tvStatementOverviewGeneralCardMaterialDamageOtherCars.visibility =
                    View.VISIBLE
            if (statementData.materialDamageToObjects)
                binding.tvStatementOverviewGeneralCardMaterialDamageOtherObjects.visibility =
                    View.VISIBLE
        }

        if (statementData.witnessIsPresent) {

            if (statementData.witnessName.isNotEmpty()) {
                binding.tvStatementOverviewGeneralCardWitnessName.visibility = View.VISIBLE
                setBoldAndNormalText(
                    binding.tvStatementOverviewGeneralCardWitnessName,
                    binding.tvStatementOverviewGeneralCardWitnessName.text.toString(),
                    statementData.witnessName
                )
            }

            if (statementData.witnessAddress.isNotEmpty()) {
                binding.tvStatementOverviewGeneralCardWitnessAddress.visibility = View.VISIBLE
                setBoldAndNormalText(
                    binding.tvStatementOverviewGeneralCardWitnessAddress,
                    binding.tvStatementOverviewGeneralCardWitnessAddress.text.toString(),
                    statementData.witnessAddress
                )
            }

            if (statementData.witnessPhoneNumber.isNotEmpty()) {
                binding.tvStatementOverviewGeneralCardWitnessPhoneNumber.visibility = View.VISIBLE
                setBoldAndNormalText(
                    binding.tvStatementOverviewGeneralCardWitnessPhoneNumber,
                    binding.tvStatementOverviewGeneralCardWitnessPhoneNumber.text.toString(),
                    statementData.witnessPhoneNumber
                )
            }

        } else {
            binding.tvStatementOverviewGeneralCardWitnessNoWitness.visibility = View.VISIBLE
        }

        if (statementData.accidentSketch != null) {
            binding.tvStatementOverviewSketchLabel.visibility = View.VISIBLE
            binding.ivStatementOverviewSketch.setImageBitmap(statementData.accidentSketch)
            binding.ivStatementOverviewSketch.visibility = View.VISIBLE
            binding.tvStatementOverviewGeneralCardSketchEdit.visibility = View.VISIBLE
        }
    }

    private fun updateVehicleACardFromViewModel(
        statementData: StatementData
    ) {

        //Vehicle A page one
        setBoldAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderLastName,
            binding.tvStatementVehicleACardPolicyHolderLastName.text.toString(),
            statementData.policyHolderALastName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderFirstName,
            binding.tvStatementVehicleACardPolicyHolderFirstName.text.toString(),
            statementData.policyHolderAFirstName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderAddress,
            binding.tvStatementVehicleACardPolicyHolderAddress.text.toString(),
            statementData.policyHolderAAddress
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderPostalCode,
            binding.tvStatementVehicleACardPolicyHolderPostalCode.text.toString(),
            statementData.policyHolderAPostalCode
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderPhoneNumber,
            binding.tvStatementVehicleACardPolicyHolderPhoneNumber.text.toString(),
            statementData.policyHolderAPhoneNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderEmail,
            binding.tvStatementVehicleACardPolicyHolderEmail.text.toString(),
            statementData.policyHolderAEmail
        )

        if (statementData.vehicleAMarkType.isNotEmpty()) {
            binding.tvStatementVehicleACardMotorTitle.visibility = View.VISIBLE
            binding.tvStatementVehicleACardMotorMarkType.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorMarkType,
                binding.tvStatementVehicleACardMotorMarkType.text.toString(),
                statementData.vehicleAMarkType
            )
        }

        if (statementData.vehicleARegistrationNumber.isNotEmpty()) {
            binding.tvStatementVehicleACardMotorRegistrationNumber.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorRegistrationNumber,
                binding.tvStatementVehicleACardMotorRegistrationNumber.text.toString(),
                statementData.vehicleARegistrationNumber
            )
        }

        if (statementData.vehicleACountryOfRegistration.isNotEmpty()) {
            binding.tvStatementVehicleACardMotorCountry.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorCountry,
                binding.tvStatementVehicleACardMotorCountry.text.toString(),
                statementData.vehicleACountryOfRegistration
            )
        }

        if (statementData.vehicleATrailerPresent) {
            binding.tvStatementVehicleACardTrailerTitle.visibility = View.VISIBLE

            if (statementData.vehicleATrailerRegistrationNumber.isNotEmpty()) {
                binding.tvStatementVehicleACardTrailerRegistrationNumber.visibility = View.VISIBLE
                setBoldAndNormalText(
                    binding.tvStatementVehicleACardTrailerRegistrationNumber,
                    binding.tvStatementVehicleACardTrailerRegistrationNumber.text.toString(),
                    statementData.vehicleATrailerRegistrationNumber
                )
            }

            if (statementData.vehicleATrailerCountryOfRegistration.isNotEmpty()) {
                binding.tvStatementVehicleACardTrailerCountry.visibility = View.VISIBLE
                setBoldAndNormalText(
                    binding.tvStatementVehicleACardTrailerCountry,
                    binding.tvStatementVehicleACardTrailerCountry.text.toString(),
                    statementData.vehicleATrailerCountryOfRegistration
                )
            }

            if (statementData.vehicleATrailerRegistrationNumber.isEmpty() && statementData.vehicleATrailerCountryOfRegistration.isEmpty()) {
                binding.tvStatementVehicleACardTrailerNoRegistration.visibility = View.VISIBLE
            }

        }

        //Vehicle A page two
        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceCompanyName,
            binding.tvStatementVehicleACardInsuranceCompanyName.text.toString(),
            statementData.vehicleAInsuranceCompanyName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsurancePolicyNumber,
            binding.tvStatementVehicleACardInsurancePolicyNumber.text.toString(),
            statementData.vehicleAInsuranceCompanyPolicyNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceGreenCardNumber,
            binding.tvStatementVehicleACardInsuranceGreenCardNumber.text.toString(),
            statementData.vehicleAInsuranceCompanyGreenCardNumber
        )

        //Vehicle A page three
        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceCertificateAvailabilityDate,
            binding.tvStatementVehicleACardInsuranceCertificateAvailabilityDate.text.toString(),
            statementData.vehicleAInsuranceCertificateAvailabilityDate?.to24Format()
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceCertificateExpirationDate,
            binding.tvStatementVehicleACardInsuranceCertificateExpirationDate.text.toString(),
            statementData.vehicleAInsuranceCertificateExpirationDate?.to24Format()
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceAgencyName,
            binding.tvStatementVehicleACardInsuranceAgencyName.text.toString(),
            statementData.vehicleAInsuranceAgencyName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceAgencyAddress,
            binding.tvStatementVehicleACardInsuranceAgencyAddress.text.toString(),
            statementData.vehicleAInsuranceAgencyAddress
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceAgencyCountry,
            binding.tvStatementVehicleACardInsuranceAgencyCountry.text.toString(),
            statementData.vehicleAInsuranceAgencyCountry
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceAgencyPhoneNumber,
            binding.tvStatementVehicleACardInsuranceAgencyPhoneNumber.text.toString(),
            statementData.vehicleAInsuranceAgencyPhoneNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceAgencyEmail,
            binding.tvStatementVehicleACardInsuranceAgencyEmail.text.toString(),
            statementData.vehicleAInsuranceAgencyEmail
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardInsuranceDamageCovered,
            binding.tvStatementVehicleACardInsuranceDamageCovered.text.toString(),
            if (statementData.vehicleAMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                R.string.no
            )
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverLastName,
            binding.tvStatementVehicleACardDriverLastName.text.toString(),
            statementData.vehicleADriverLastName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverFirstName,
            binding.tvStatementVehicleACardDriverFirstName.text.toString(),
            statementData.vehicleADriverFirstName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverDateOfBirth,
            binding.tvStatementVehicleACardDriverDateOfBirth.text.toString(),
            statementData.vehicleADriverDateOfBirth?.to24Format()
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverAddress,
            binding.tvStatementVehicleACardDriverAddress.text.toString(),
            statementData.vehicleADriverAddress
        )


        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverCountry,
            binding.tvStatementVehicleACardDriverCountry.text.toString(),
            statementData.vehicleADriverCountry
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverPhoneNumber,
            binding.tvStatementVehicleACardDriverPhoneNumber.text.toString(),
            statementData.vehicleADriverPhoneNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverEmail,
            binding.tvStatementVehicleACardDriverEmail.text.toString(),
            statementData.vehicleADriverEmail
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverDrivingLicenseNumber,
            binding.tvStatementVehicleACardDriverDrivingLicenseNumber.text.toString(),
            statementData.vehicleADriverDrivingLicenseNr
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleACardDriverDrivingLicenseExpirationDate,
            binding.tvStatementVehicleACardDriverDrivingLicenseExpirationDate.text.toString(),
            statementData.vehicleADriverDrivingLicenseExpirationDate?.to24Format()
        )

        //Vehicle A page five
        if (statementData.vehicleAPointOfImpactSketch != null){
            binding.tvStatementVehicleAPointOfImpactTitle.visibility = View.VISIBLE
            binding.ivStatementVehicleAPointOfImpactSketch.setImageBitmap(statementData.vehicleAPointOfImpactSketch)
            binding.ivStatementVehicleAPointOfImpactSketch.visibility= View.VISIBLE

        }

        if (!statementData.vehicleAAccidentPhotos.isNullOrEmpty()) {
            val accidentImages = statementData.vehicleAAccidentPhotos!!
            val viewPager = binding.vpStatementOverviewVehicleAAccidentPhotos
            viewPager.visibility = View.VISIBLE
            viewPager.adapter = ImageAdapter(accidentImages, this.requireContext())
        }

        setBoldAndNormalText(
            binding.tvStatementVehicleARemarks,
            binding.tvStatementVehicleARemarks.text.toString(),
            statementData.vehicleARemarks
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleADamageDescription,
            binding.tvStatementVehicleADamageDescription.text.toString(),
            statementData.vehicleADamageDescription
        )

    }

    private fun updateVehicleBCardFromViewModel(statementData: StatementData) {

        //Vehicle B page one
        setBoldAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderLastName,
            binding.tvStatementVehicleBCardPolicyHolderLastName.text.toString(),
            statementData.policyHolderBLastName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderFirstName,
            binding.tvStatementVehicleBCardPolicyHolderFirstName.text.toString(),
            statementData.policyHolderBFirstName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderAddress,
            binding.tvStatementVehicleBCardPolicyHolderAddress.text.toString(),
            statementData.policyHolderBAddress
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderPostalCode,
            binding.tvStatementVehicleBCardPolicyHolderPostalCode.text.toString(),
            statementData.policyHolderBPostalCode
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderPhoneNumber,
            binding.tvStatementVehicleBCardPolicyHolderPhoneNumber.text.toString(),
            statementData.policyHolderBPhoneNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderEmail,
            binding.tvStatementVehicleBCardPolicyHolderEmail.text.toString(),
            statementData.policyHolderBEmail
        )

        if (statementData.vehicleBMarkType.isNotEmpty()) {
            binding.tvStatementVehicleBCardMotorTitle.visibility = View.VISIBLE
            binding.tvStatementVehicleBCardMotorMarkType.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorMarkType,
                binding.tvStatementVehicleBCardMotorMarkType.text.toString(),
                statementData.vehicleBMarkType
            )
        }

        if (statementData.vehicleBRegistrationNumber.isNotEmpty()) {
            binding.tvStatementVehicleBCardMotorRegistrationNumber.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorRegistrationNumber,
                binding.tvStatementVehicleBCardMotorRegistrationNumber.text.toString(),
                statementData.vehicleBRegistrationNumber
            )
        }

        if (statementData.vehicleBCountryOfRegistration.isNotEmpty()) {
            binding.tvStatementVehicleBCardMotorCountry.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorCountry,
                binding.tvStatementVehicleBCardMotorCountry.text.toString(),
                statementData.vehicleBCountryOfRegistration
            )
        }

        if (statementData.vehicleBTrailerPresent) {
            binding.tvStatementVehicleBCardTrailerTitle.visibility = View.VISIBLE

            if (statementData.vehicleBTrailerRegistrationNumber.isNotEmpty()) {
                binding.tvStatementVehicleBCardTrailerRegistrationNumber.visibility = View.VISIBLE
                setBoldAndNormalText(
                    binding.tvStatementVehicleBCardTrailerRegistrationNumber,
                    binding.tvStatementVehicleBCardTrailerRegistrationNumber.text.toString(),
                    statementData.vehicleBTrailerRegistrationNumber
                )
            }

            if (statementData.vehicleBTrailerCountryOfRegistration.isNotEmpty()) {
                binding.tvStatementVehicleBCardTrailerCountry.visibility = View.VISIBLE
                setBoldAndNormalText(
                    binding.tvStatementVehicleBCardTrailerCountry,
                    binding.tvStatementVehicleBCardTrailerCountry.text.toString(),
                    statementData.vehicleBTrailerCountryOfRegistration
                )
            }

            if (statementData.vehicleBTrailerRegistrationNumber.isEmpty() && statementData.vehicleBTrailerCountryOfRegistration.isEmpty()) {
                binding.tvStatementVehicleBCardTrailerNoRegistration.visibility = View.VISIBLE
            }

        }

        //Vehicle B page two
        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceCompanyName,
            binding.tvStatementVehicleBCardInsuranceCompanyName.text.toString(),
            statementData.vehicleBInsuranceCompanyName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsurancePolicyNumber,
            binding.tvStatementVehicleBCardInsurancePolicyNumber.text.toString(),
            statementData.vehicleBInsuranceCompanyPolicyNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceGreenCardNumber,
            binding.tvStatementVehicleBCardInsuranceGreenCardNumber.text.toString(),
            statementData.vehicleBInsuranceCompanyGreenCardNumber
        )


        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceCertificateAvailabilityDate,
            binding.tvStatementVehicleBCardInsuranceCertificateAvailabilityDate.text.toString(),
            statementData.vehicleBInsuranceCertificateAvailabilityDate?.to24Format()
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceCertificateExpirationDate,
            binding.tvStatementVehicleBCardInsuranceCertificateExpirationDate.text.toString(),
            statementData.vehicleBInsuranceCertificateExpirationDate?.to24Format()
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceAgencyName,
            binding.tvStatementVehicleBCardInsuranceAgencyName.text.toString(),
            statementData.vehicleBInsuranceAgencyName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceAgencyAddress,
            binding.tvStatementVehicleBCardInsuranceAgencyAddress.text.toString(),
            statementData.vehicleBInsuranceAgencyAddress
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceAgencyCountry,
            binding.tvStatementVehicleBCardInsuranceAgencyCountry.text.toString(),
            statementData.vehicleBInsuranceAgencyCountry
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceAgencyPhoneNumber,
            binding.tvStatementVehicleBCardInsuranceAgencyPhoneNumber.text.toString(),
            statementData.vehicleBInsuranceAgencyPhoneNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceAgencyEmail,
            binding.tvStatementVehicleBCardInsuranceAgencyEmail.text.toString(),
            statementData.vehicleBInsuranceAgencyEmail
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardInsuranceDamageCovered,
            binding.tvStatementVehicleBCardInsuranceDamageCovered.text.toString(),
            if (statementData.vehicleBMaterialDamageCovered)
                requireContext().getString(R.string.yes) else requireContext().getString(
                R.string.no
            )
        )

        //Vehicle B page three
        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverLastName,
            binding.tvStatementVehicleBCardDriverLastName.text.toString(),
            statementData.vehicleBDriverLastName
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverFirstName,
            binding.tvStatementVehicleBCardDriverFirstName.text.toString(),
            statementData.vehicleBDriverFirstName
        )


        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverDateOfBirth,
            binding.tvStatementVehicleBCardDriverDateOfBirth.text.toString(),
            statementData.vehicleBDriverDateOfBirth?.to24Format()
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverAddress,
            binding.tvStatementVehicleBCardDriverAddress.text.toString(),
            statementData.vehicleBDriverAddress
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverCountry,
            binding.tvStatementVehicleBCardDriverCountry.text.toString(),
            statementData.vehicleBDriverCountry
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverPhoneNumber,
            binding.tvStatementVehicleBCardDriverPhoneNumber.text.toString(),
            statementData.vehicleBDriverPhoneNumber
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverEmail,
            binding.tvStatementVehicleBCardDriverEmail.text.toString(),
            statementData.vehicleBDriverEmail
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverDrivingLicenseNumber,
            binding.tvStatementVehicleBCardDriverDrivingLicenseNumber.text.toString(),
            statementData.vehicleBDriverDrivingLicenseNr
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBCardDriverDrivingLicenseExpirationDate,
            binding.tvStatementVehicleBCardDriverDrivingLicenseExpirationDate.text.toString(),
            statementData.vehicleBDriverDrivingLicenseExpirationDate?.to24Format()
        )

        //Vehicle B page five
        if (statementData.vehicleBPointOfImpactSketch != null){
            binding.tvStatementVehicleBPointOfImpactTitle.visibility = View.VISIBLE
            binding.ivStatementVehicleBPointOfImpactSketch.setImageBitmap(statementData.vehicleBPointOfImpactSketch)
            binding.ivStatementVehicleBPointOfImpactSketch.visibility= View.VISIBLE

        }
        if (!statementData.vehicleBAccidentPhotos.isNullOrEmpty()) {

            val accidentImages = statementData.vehicleBAccidentPhotos!!
            val viewPager = binding.vpStatementOverviewVehicleBAccidentPhotos
            viewPager.visibility = View.VISIBLE
            viewPager.adapter = ImageAdapter(accidentImages, this.requireContext())
        }

        setBoldAndNormalText(
            binding.tvStatementVehicleBRemarks,
            binding.tvStatementVehicleBRemarks.text.toString(),
            statementData.vehicleBRemarks
        )

        setBoldAndNormalText(
            binding.tvStatementVehicleBDamageDescription,
            binding.tvStatementVehicleBDamageDescription.text.toString(),
            statementData.vehicleBDamageDescription
        )
    }
}