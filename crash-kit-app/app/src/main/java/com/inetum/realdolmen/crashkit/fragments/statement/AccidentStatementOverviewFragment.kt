package com.inetum.realdolmen.crashkit.fragments.statement

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.transition.ChangeTransform
import android.transition.TransitionManager
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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.adapters.ImageAdapter
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentStatementOverviewBinding
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementData
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.to24Format

class AccidentStatementOverviewFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentAccidentStatementOverviewBinding? = null
    private val binding get() = _binding!!

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

        binding.tvStatementVehicleAPageOneMotorEdit.setOnClickListener {
            navController.navigate(R.id.vehicleANewStatementFragment)
        }

        binding.tvStatementVehicleAPageOneTrailerEdit.setOnClickListener {
            navController.navigate(R.id.vehicleANewStatementFragment)
        }

        binding.tvStatementVehicleAMotorInsurancePageEdit.setOnClickListener {
            navController.navigate(R.id.vehicleAInsuranceFragment)
        }

        binding.tvStatementVehicleATrailerInsurancePageEdit.setOnClickListener {
            navController.navigate(R.id.vehicleATrailerInsuranceFragment)
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

        binding.tvStatementVehicleBPageOneMotorEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBNewStatementFragment)
        }

        binding.tvStatementVehicleBPageOneTrailerEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBNewStatementFragment)
        }

        binding.tvStatementVehicleBMotorInsurancePageEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBInsuranceFragment)
        }

        binding.tvStatementVehicleBTrailerInsurancePageEdit.setOnClickListener {
            navController.navigate(R.id.vehicleBTrailerInsuranceFragment)
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
            navController.navigate(R.id.accidentStatementSignatureFragment)
        }

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

        if (!statementData.vehicleAMotorAbsent) {
            binding.llVehicleAMotorFields.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorMarkType,
                binding.tvStatementVehicleACardMotorMarkType.text.toString(),
                statementData.vehicleAMotorMarkType
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorRegistrationNumber,
                binding.tvStatementVehicleACardMotorRegistrationNumber.text.toString(),
                statementData.vehicleAMotorLicensePlate
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorCountry,
                binding.tvStatementVehicleACardMotorCountry.text.toString(),
                statementData.vehicleAMotorCountryOfRegistration
            )
        } else {
            binding.llVehicleAMotorFields.visibility = View.GONE
        }

        if (statementData.vehicleATrailerPresent) {
            binding.llVehicleATrailerFields.visibility = View.VISIBLE

            if (statementData.vehicleATrailerLicensePlate.isEmpty() && statementData.vehicleATrailerCountryOfRegistration.isEmpty()) {
                binding.tvStatementVehicleACardTrailerRegistrationNumber.visibility = View.GONE
                binding.tvStatementVehicleACardTrailerCountry.visibility = View.GONE
                binding.tvStatementVehicleACardTrailerNoRegistration.visibility = View.VISIBLE
            } else {
                binding.tvStatementVehicleACardTrailerRegistrationNumber.visibility = View.VISIBLE
                binding.tvStatementVehicleACardTrailerCountry.visibility = View.VISIBLE

                setBoldAndNormalText(
                    binding.tvStatementVehicleACardTrailerRegistrationNumber,
                    binding.tvStatementVehicleACardTrailerRegistrationNumber.text.toString(),
                    statementData.vehicleATrailerLicensePlate
                )

                setBoldAndNormalText(
                    binding.tvStatementVehicleACardTrailerCountry,
                    binding.tvStatementVehicleACardTrailerCountry.text.toString(),
                    statementData.vehicleATrailerCountryOfRegistration
                )
            }

        } else {
            binding.llVehicleATrailerFields.visibility = View.GONE
        }

        //Vehicle A motor insurance page
        if (!statementData.vehicleAMotorAbsent) {
            binding.llVehicleAMotorInsurance.visibility = View.VISIBLE

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceCompanyName,
                binding.tvStatementVehicleACardMotorInsuranceCompanyName.text.toString(),
                statementData.vehicleAInsuranceCompanyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsurancePolicyNumber,
                binding.tvStatementVehicleACardMotorInsurancePolicyNumber.text.toString(),
                statementData.vehicleAInsuranceCompanyPolicyNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceGreenCardNumber,
                binding.tvStatementVehicleACardMotorInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleAInsuranceCompanyGreenCardNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleACardMotorInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleAInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleACardMotorInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleAInsuranceCertificateExpirationDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyName,
                binding.tvStatementVehicleACardMotorInsuranceAgencyName.text.toString(),
                statementData.vehicleAInsuranceAgencyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyAddress,
                binding.tvStatementVehicleACardMotorInsuranceAgencyAddress.text.toString(),
                statementData.vehicleAInsuranceAgencyAddress
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyCountry,
                binding.tvStatementVehicleACardMotorInsuranceAgencyCountry.text.toString(),
                statementData.vehicleAInsuranceAgencyCountry
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleACardMotorInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleAInsuranceAgencyPhoneNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyEmail,
                binding.tvStatementVehicleACardMotorInsuranceAgencyEmail.text.toString(),
                statementData.vehicleAInsuranceAgencyEmail
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceDamageCovered,
                binding.tvStatementVehicleACardMotorInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleAMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleAMotorInsurance.visibility = View.GONE
        }

        //Vehicle A motor insurance page
        if (statementData.vehicleATrailerHasRegistration) {
            binding.llVehicleATrailerInsurance.visibility = View.VISIBLE

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceCompanyName,
                binding.tvStatementVehicleACardTrailerInsuranceCompanyName.text.toString(),
                statementData.vehicleATrailerInsuranceCompanyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsurancePolicyNumber,
                binding.tvStatementVehicleACardTrailerInsurancePolicyNumber.text.toString(),
                statementData.vehicleATrailerInsuranceCompanyPolicyNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceGreenCardNumber,
                binding.tvStatementVehicleACardTrailerInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleATrailerInsuranceCompanyGreenCardNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleACardTrailerInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleATrailerInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleACardTrailerInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleATrailerInsuranceCertificateExpirationDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyName,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyName.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyAddress,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyAddress.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyAddress
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyCountry,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyCountry.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyCountry
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyPhoneNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyEmail,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyEmail.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyEmail
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceDamageCovered,
                binding.tvStatementVehicleACardTrailerInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleATrailerMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleATrailerInsurance.visibility = View.GONE
        }

        //Vehicle A driver page
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
            binding.tvStatementVehicleACardDriverCategory,
            binding.tvStatementVehicleACardDriverCategory.text.toString(),
            statementData.vehicleADriverDrivingLicenseCategory
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

        //Vehicle A page four
        if (!model.vehicleACircumstances.value.isNullOrEmpty()) {
            binding.tvStatementVehicleAAmountOfCircumstances.visibility = View.VISIBLE

            setBoldAndNormalText(
                binding.tvStatementVehicleAAmountOfCircumstances,
                binding.tvStatementVehicleAAmountOfCircumstances.text.toString(),
                model.vehicleACircumstances.value!!.size.toString()
            )
        }

        //Vehicle A page five
        if (statementData.vehicleAPointOfImpactSketch != null) {
            binding.tvStatementVehicleAPointOfImpactTitle.visibility = View.VISIBLE
            binding.ivStatementVehicleAPointOfImpactSketch.setImageBitmap(statementData.vehicleAPointOfImpactSketch)
            binding.ivStatementVehicleAPointOfImpactSketch.visibility = View.VISIBLE

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

        if (!statementData.vehicleBMotorAbsent) {
            binding.llVehicleBMotorFields.visibility = View.VISIBLE
            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorMarkType,
                binding.tvStatementVehicleBCardMotorMarkType.text.toString(),
                statementData.vehicleBMotorMarkType
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorRegistrationNumber,
                binding.tvStatementVehicleBCardMotorRegistrationNumber.text.toString(),
                statementData.vehicleBMotorLicensePlate
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorCountry,
                binding.tvStatementVehicleBCardMotorCountry.text.toString(),
                statementData.vehicleBMotorCountryOfRegistration
            )
        } else {
            binding.llVehicleBMotorFields.visibility = View.GONE
        }


        if (statementData.vehicleBTrailerPresent) {
            binding.llVehicleBTrailerFields.visibility = View.VISIBLE

            if (statementData.vehicleBTrailerLicensePlate.isEmpty() && statementData.vehicleBTrailerCountryOfRegistration.isEmpty()) {
                binding.tvStatementVehicleBCardTrailerRegistrationNumber.visibility = View.GONE
                binding.tvStatementVehicleBCardTrailerCountry.visibility = View.GONE
                binding.tvStatementVehicleBCardTrailerNoRegistration.visibility = View.VISIBLE
            } else {
                binding.tvStatementVehicleBCardTrailerRegistrationNumber.visibility = View.VISIBLE
                binding.tvStatementVehicleBCardTrailerCountry.visibility = View.VISIBLE

                setBoldAndNormalText(
                    binding.tvStatementVehicleBCardTrailerRegistrationNumber,
                    binding.tvStatementVehicleBCardTrailerRegistrationNumber.text.toString(),
                    statementData.vehicleBTrailerLicensePlate
                )

                setBoldAndNormalText(
                    binding.tvStatementVehicleBCardTrailerCountry,
                    binding.tvStatementVehicleBCardTrailerCountry.text.toString(),
                    statementData.vehicleBTrailerCountryOfRegistration
                )
            }
        } else {
            binding.llVehicleBTrailerFields.visibility = View.GONE
        }

        //Vehicle B motor insurance page
        if (!statementData.vehicleBMotorAbsent) {
            binding.llVehicleBMotorInsurance.visibility = View.VISIBLE

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceCompanyName,
                binding.tvStatementVehicleBCardMotorInsuranceCompanyName.text.toString(),
                statementData.vehicleBInsuranceCompanyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsurancePolicyNumber,
                binding.tvStatementVehicleBCardMotorInsurancePolicyNumber.text.toString(),
                statementData.vehicleBInsuranceCompanyPolicyNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceGreenCardNumber,
                binding.tvStatementVehicleBCardMotorInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleBInsuranceCompanyGreenCardNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleBCardMotorInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleBInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleBCardMotorInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleBInsuranceCertificateExpirationDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyName,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyName.text.toString(),
                statementData.vehicleBInsuranceAgencyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyAddress,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyAddress.text.toString(),
                statementData.vehicleBInsuranceAgencyAddress
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyCountry,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyCountry.text.toString(),
                statementData.vehicleBInsuranceAgencyCountry
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleBInsuranceAgencyPhoneNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyEmail,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyEmail.text.toString(),
                statementData.vehicleBInsuranceAgencyEmail
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceDamageCovered,
                binding.tvStatementVehicleBCardMotorInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleBMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleBMotorInsurance.visibility = View.GONE
        }

        //Vehicle A motor insurance page
        if (statementData.vehicleBTrailerHasRegistration) {
            binding.llVehicleBTrailerInsurance.visibility = View.VISIBLE

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceCompanyName,
                binding.tvStatementVehicleBCardTrailerInsuranceCompanyName.text.toString(),
                statementData.vehicleBTrailerInsuranceCompanyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsurancePolicyNumber,
                binding.tvStatementVehicleBCardTrailerInsurancePolicyNumber.text.toString(),
                statementData.vehicleBTrailerInsuranceCompanyPolicyNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceGreenCardNumber,
                binding.tvStatementVehicleBCardTrailerInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleBTrailerInsuranceCompanyGreenCardNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleBTrailerInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleBTrailerInsuranceCertificateExpirationDate?.to24Format()
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyName,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyName.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyName
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyAddress,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyAddress.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyAddress
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyCountry,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyCountry.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyCountry
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyPhoneNumber
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyEmail,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyEmail.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyEmail
            )

            setBoldAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceDamageCovered,
                binding.tvStatementVehicleBCardTrailerInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleBTrailerMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleBTrailerInsurance.visibility = View.GONE
        }

        //Vehicle B driver page
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
            binding.tvStatementVehicleBCardDriverCategory,
            binding.tvStatementVehicleBCardDriverCategory.text.toString(),
            statementData.vehicleBDriverDrivingLicenseCategory
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

        //Vehicle B page four
        if (!model.vehicleBCircumstances.value.isNullOrEmpty()) {
            binding.tvStatementVehicleBAmountOfCircumstances.visibility = View.VISIBLE

            setBoldAndNormalText(
                binding.tvStatementVehicleBAmountOfCircumstances,
                binding.tvStatementVehicleBAmountOfCircumstances.text.toString(),
                model.vehicleBCircumstances.value!!.size.toString()
            )
        }

        //Vehicle B page five
        if (statementData.vehicleBPointOfImpactSketch != null) {
            binding.tvStatementVehicleBPointOfImpactTitle.visibility = View.VISIBLE
            binding.ivStatementVehicleBPointOfImpactSketch.setImageBitmap(statementData.vehicleBPointOfImpactSketch)
            binding.ivStatementVehicleBPointOfImpactSketch.visibility = View.VISIBLE

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