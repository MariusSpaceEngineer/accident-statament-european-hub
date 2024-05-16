package com.inetum.realdolmen.crashkit.fragments.statement

import android.annotation.SuppressLint
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lock the screen orientation to portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccidentStatementOverviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        updateUIFromViewModel(model)

        setupButtonClickListeners()

    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner) { statementData ->
            updateGeneralInformationCardFromViewModel(statementData)
            updateVehicleACardFromViewModel(statementData)
            updateVehicleBCardFromViewModel(statementData)
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        TODO()
    }

    private fun setupButtonClickListeners() {
        setupVehicleACardButtons()
        setupVehicleBCardButtons()
        setupNavigationButtons()
    }

    private fun setupNavigationButtons() {
        binding.btnStatementAccidentPrevious.setOnClickListener {
            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            navController.navigate(R.id.accidentStatementSignatureFragment)
        }
    }

    private fun setupVehicleACardButtons() {
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
            navController.navigate(R.id.vehicleAMotorInsuranceFragment)
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
    }

    private fun setupVehicleBCardButtons() {
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
    /**
     * This function sets the text of a TextView with a label and appended text.
     * The label is displayed in bold, and the appended text (if present) is displayed in normal style.
     *
     * @param view The TextView to which the text is to be set.
     * @param label The label text that will be displayed in bold.
     * @param appendedText The text that will be appended to the label and displayed in normal style. If null, only a colon is appended to the label.
     */
    private fun setTextViewWithBoldLabelAndNormalText(view: TextView, label: String, appendedText: String?) {
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
        updateGeneralInformation(statementData)
        updateMaterialDamageInformation(statementData)
        updateWitnessInformation(statementData)
        updateAccidentSketch(statementData)
    }

    private fun updateGeneralInformation(statementData: StatementData) {
        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementOverviewGeneralCardLabelDateOfAccident,
            binding.tvStatementOverviewGeneralCardLabelDateOfAccident.text.toString(),
            statementData.dateOfAccident?.to24Format()
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementOverviewGeneralCardLabelLocation,
            binding.tvStatementOverviewGeneralCardLabelLocation.text.toString(),
            statementData.accidentLocation
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementOverviewGeneralCardLabelInjured,
            binding.tvStatementOverviewGeneralCardLabelInjured.text.toString(),
            if (statementData.injured) requireContext().getString(R.string.yes) else requireContext().getString(
                R.string.no
            )
        )
    }

    private fun updateMaterialDamageInformation(statementData: StatementData) {
        if (statementData.materialDamageToOtherVehicles || statementData.materialDamageToObjects) {
            binding.tvStatementOverviewGeneralCardLabelMaterialDamage.visibility = View.VISIBLE
            if (statementData.materialDamageToOtherVehicles)
                binding.tvStatementOverviewGeneralCardMaterialDamageOtherCars.visibility =
                    View.VISIBLE
            if (statementData.materialDamageToObjects)
                binding.tvStatementOverviewGeneralCardMaterialDamageOtherObjects.visibility =
                    View.VISIBLE
        }
    }

    private fun updateWitnessInformation(statementData: StatementData) {
        if (statementData.witnessIsPresent) {

            if (statementData.witnessName.isNotEmpty()) {
                binding.tvStatementOverviewGeneralCardWitnessName.visibility = View.VISIBLE
                setTextViewWithBoldLabelAndNormalText(
                    binding.tvStatementOverviewGeneralCardWitnessName,
                    binding.tvStatementOverviewGeneralCardWitnessName.text.toString(),
                    statementData.witnessName
                )
            }

            if (statementData.witnessAddress.isNotEmpty()) {
                binding.tvStatementOverviewGeneralCardWitnessAddress.visibility = View.VISIBLE
                setTextViewWithBoldLabelAndNormalText(
                    binding.tvStatementOverviewGeneralCardWitnessAddress,
                    binding.tvStatementOverviewGeneralCardWitnessAddress.text.toString(),
                    statementData.witnessAddress
                )
            }

            if (statementData.witnessPhoneNumber.isNotEmpty()) {
                binding.tvStatementOverviewGeneralCardWitnessPhoneNumber.visibility = View.VISIBLE
                setTextViewWithBoldLabelAndNormalText(
                    binding.tvStatementOverviewGeneralCardWitnessPhoneNumber,
                    binding.tvStatementOverviewGeneralCardWitnessPhoneNumber.text.toString(),
                    statementData.witnessPhoneNumber
                )
            }

        } else {
            binding.tvStatementOverviewGeneralCardWitnessNoWitness.visibility = View.VISIBLE
        }
    }

    private fun updateAccidentSketch(statementData: StatementData) {
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
        updateVehicleANewStatementFragmentFields(statementData)

        //Vehicle A motor insurance page
        updateVehicleAMotorInsuranceFragmentFields(statementData)

        //Vehicle A trailer insurance page
        updateVehicleATrailerInsuranceFragmentFields(statementData)

        //Vehicle A driver page
        updateVehicleADriverFragmentFields(statementData)

        //Vehicle A page four
        updateVehicleACircumstancesFragmentFields()

        //Vehicle A page five
        updateVehicleAMiscellaneousFragmentFields(statementData)
    }

    private fun updateVehicleBCardFromViewModel(statementData: StatementData) {

        //Vehicle B page one
        updateVehicleBNewStatementFragmentFields(statementData)

        //Vehicle B motor insurance page
        updateVehicleBMotorInsuranceFragmentFields(statementData)

        //Vehicle B trailer insurance page
        updateVehicleBTrailerInsuranceFragmentFields(statementData)

        //Vehicle B driver page
        updateVehicleBDriverFragmentFields(statementData)

        //Vehicle B page four
        updateVehicleBInsuranceFragmentFields()

        //Vehicle B page five
        updateVehicleBMiscellaneousFragmentFields(statementData)
    }

    private fun updateVehicleAMiscellaneousFragmentFields(statementData: StatementData) {
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

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleARemarks,
            binding.tvStatementVehicleARemarks.text.toString(),
            statementData.vehicleARemarks
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleADamageDescription,
            binding.tvStatementVehicleADamageDescription.text.toString(),
            statementData.vehicleADamageDescription
        )
    }

    private fun updateVehicleACircumstancesFragmentFields() {
        if (!model.vehicleACircumstances.value.isNullOrEmpty()) {
            binding.tvStatementVehicleAAmountOfCircumstances.visibility = View.VISIBLE

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleAAmountOfCircumstances,
                binding.tvStatementVehicleAAmountOfCircumstances.text.toString(),
                model.vehicleACircumstances.value!!.size.toString()
            )
        }
    }

    private fun updateVehicleADriverFragmentFields(statementData: StatementData) {
        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverLastName,
            binding.tvStatementVehicleACardDriverLastName.text.toString(),
            statementData.vehicleADriverLastName
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverFirstName,
            binding.tvStatementVehicleACardDriverFirstName.text.toString(),
            statementData.vehicleADriverFirstName
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverDateOfBirth,
            binding.tvStatementVehicleACardDriverDateOfBirth.text.toString(),
            statementData.vehicleADriverDateOfBirth?.to24Format()
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverAddress,
            binding.tvStatementVehicleACardDriverAddress.text.toString(),
            statementData.vehicleADriverAddress
        )


        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverCountry,
            binding.tvStatementVehicleACardDriverCountry.text.toString(),
            statementData.vehicleADriverCountry
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverPhoneNumber,
            binding.tvStatementVehicleACardDriverPhoneNumber.text.toString(),
            statementData.vehicleADriverPhoneNumber
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverCategory,
            binding.tvStatementVehicleACardDriverCategory.text.toString(),
            statementData.vehicleADriverDrivingLicenseCategory
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverEmail,
            binding.tvStatementVehicleACardDriverEmail.text.toString(),
            statementData.vehicleADriverEmail
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverDrivingLicenseNumber,
            binding.tvStatementVehicleACardDriverDrivingLicenseNumber.text.toString(),
            statementData.vehicleADriverDrivingLicenseNr
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardDriverDrivingLicenseExpirationDate,
            binding.tvStatementVehicleACardDriverDrivingLicenseExpirationDate.text.toString(),
            statementData.vehicleADriverDrivingLicenseExpirationDate?.to24Format()
        )
    }

    private fun updateVehicleATrailerInsuranceFragmentFields(statementData: StatementData) {
        if (statementData.vehicleATrailerHasRegistration) {
            binding.llVehicleATrailerInsurance.visibility = View.VISIBLE

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceCompanyName,
                binding.tvStatementVehicleACardTrailerInsuranceCompanyName.text.toString(),
                statementData.vehicleATrailerInsuranceCompanyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsurancePolicyNumber,
                binding.tvStatementVehicleACardTrailerInsurancePolicyNumber.text.toString(),
                statementData.vehicleATrailerInsuranceCompanyPolicyNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceGreenCardNumber,
                binding.tvStatementVehicleACardTrailerInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleATrailerInsuranceCompanyGreenCardNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleACardTrailerInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleATrailerInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleACardTrailerInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleATrailerInsuranceCertificateExpirationDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyName,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyName.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyAddress,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyAddress.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyAddress
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyCountry,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyCountry.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyCountry
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyPhoneNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceAgencyEmail,
                binding.tvStatementVehicleACardTrailerInsuranceAgencyEmail.text.toString(),
                statementData.vehicleATrailerInsuranceAgencyEmail
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardTrailerInsuranceDamageCovered,
                binding.tvStatementVehicleACardTrailerInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleATrailerMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleATrailerInsurance.visibility = View.GONE
        }
    }

    private fun updateVehicleAMotorInsuranceFragmentFields(statementData: StatementData) {
        if (!statementData.vehicleAMotorAbsent) {
            binding.llVehicleAMotorInsurance.visibility = View.VISIBLE

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceCompanyName,
                binding.tvStatementVehicleACardMotorInsuranceCompanyName.text.toString(),
                statementData.vehicleAInsuranceCompanyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsurancePolicyNumber,
                binding.tvStatementVehicleACardMotorInsurancePolicyNumber.text.toString(),
                statementData.vehicleAInsuranceCompanyPolicyNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceGreenCardNumber,
                binding.tvStatementVehicleACardMotorInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleAInsuranceCompanyGreenCardNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleACardMotorInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleAInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleACardMotorInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleAInsuranceCertificateExpirationDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyName,
                binding.tvStatementVehicleACardMotorInsuranceAgencyName.text.toString(),
                statementData.vehicleAInsuranceAgencyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyAddress,
                binding.tvStatementVehicleACardMotorInsuranceAgencyAddress.text.toString(),
                statementData.vehicleAInsuranceAgencyAddress
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyCountry,
                binding.tvStatementVehicleACardMotorInsuranceAgencyCountry.text.toString(),
                statementData.vehicleAInsuranceAgencyCountry
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleACardMotorInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleAInsuranceAgencyPhoneNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceAgencyEmail,
                binding.tvStatementVehicleACardMotorInsuranceAgencyEmail.text.toString(),
                statementData.vehicleAInsuranceAgencyEmail
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorInsuranceDamageCovered,
                binding.tvStatementVehicleACardMotorInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleAMotorMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleAMotorInsurance.visibility = View.GONE
        }
    }

    private fun updateVehicleANewStatementFragmentFields(statementData: StatementData) {
        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderLastName,
            binding.tvStatementVehicleACardPolicyHolderLastName.text.toString(),
            statementData.policyHolderALastName
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderFirstName,
            binding.tvStatementVehicleACardPolicyHolderFirstName.text.toString(),
            statementData.policyHolderAFirstName
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderAddress,
            binding.tvStatementVehicleACardPolicyHolderAddress.text.toString(),
            statementData.policyHolderAAddress
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderPostalCode,
            binding.tvStatementVehicleACardPolicyHolderPostalCode.text.toString(),
            statementData.policyHolderAPostalCode
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderPhoneNumber,
            binding.tvStatementVehicleACardPolicyHolderPhoneNumber.text.toString(),
            statementData.policyHolderAPhoneNumber
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleACardPolicyHolderEmail,
            binding.tvStatementVehicleACardPolicyHolderEmail.text.toString(),
            statementData.policyHolderAEmail
        )

        if (!statementData.vehicleAMotorAbsent) {
            binding.llVehicleAMotorFields.visibility = View.VISIBLE
            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorMarkType,
                binding.tvStatementVehicleACardMotorMarkType.text.toString(),
                statementData.vehicleAMotorMarkType
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleACardMotorRegistrationNumber,
                binding.tvStatementVehicleACardMotorRegistrationNumber.text.toString(),
                statementData.vehicleAMotorLicensePlate
            )

            setTextViewWithBoldLabelAndNormalText(
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

                setTextViewWithBoldLabelAndNormalText(
                    binding.tvStatementVehicleACardTrailerRegistrationNumber,
                    binding.tvStatementVehicleACardTrailerRegistrationNumber.text.toString(),
                    statementData.vehicleATrailerLicensePlate
                )

                setTextViewWithBoldLabelAndNormalText(
                    binding.tvStatementVehicleACardTrailerCountry,
                    binding.tvStatementVehicleACardTrailerCountry.text.toString(),
                    statementData.vehicleATrailerCountryOfRegistration
                )
            }

        } else {
            binding.llVehicleATrailerFields.visibility = View.GONE
        }
    }


    private fun updateVehicleBMiscellaneousFragmentFields(statementData: StatementData) {
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

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBRemarks,
            binding.tvStatementVehicleBRemarks.text.toString(),
            statementData.vehicleBRemarks
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBDamageDescription,
            binding.tvStatementVehicleBDamageDescription.text.toString(),
            statementData.vehicleBDamageDescription
        )
    }

    private fun updateVehicleBInsuranceFragmentFields() {
        if (!model.vehicleBCircumstances.value.isNullOrEmpty()) {
            binding.tvStatementVehicleBAmountOfCircumstances.visibility = View.VISIBLE

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBAmountOfCircumstances,
                binding.tvStatementVehicleBAmountOfCircumstances.text.toString(),
                model.vehicleBCircumstances.value!!.size.toString()
            )
        }
    }

    private fun updateVehicleBDriverFragmentFields(statementData: StatementData) {
        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverLastName,
            binding.tvStatementVehicleBCardDriverLastName.text.toString(),
            statementData.vehicleBDriverLastName
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverFirstName,
            binding.tvStatementVehicleBCardDriverFirstName.text.toString(),
            statementData.vehicleBDriverFirstName
        )


        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverDateOfBirth,
            binding.tvStatementVehicleBCardDriverDateOfBirth.text.toString(),
            statementData.vehicleBDriverDateOfBirth?.to24Format()
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverAddress,
            binding.tvStatementVehicleBCardDriverAddress.text.toString(),
            statementData.vehicleBDriverAddress
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverCountry,
            binding.tvStatementVehicleBCardDriverCountry.text.toString(),
            statementData.vehicleBDriverCountry
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverPhoneNumber,
            binding.tvStatementVehicleBCardDriverPhoneNumber.text.toString(),
            statementData.vehicleBDriverPhoneNumber
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverEmail,
            binding.tvStatementVehicleBCardDriverEmail.text.toString(),
            statementData.vehicleBDriverEmail
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverCategory,
            binding.tvStatementVehicleBCardDriverCategory.text.toString(),
            statementData.vehicleBDriverDrivingLicenseCategory
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverDrivingLicenseNumber,
            binding.tvStatementVehicleBCardDriverDrivingLicenseNumber.text.toString(),
            statementData.vehicleBDriverDrivingLicenseNr
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardDriverDrivingLicenseExpirationDate,
            binding.tvStatementVehicleBCardDriverDrivingLicenseExpirationDate.text.toString(),
            statementData.vehicleBDriverDrivingLicenseExpirationDate?.to24Format()
        )
    }

    private fun updateVehicleBTrailerInsuranceFragmentFields(statementData: StatementData) {
        if (statementData.vehicleBTrailerHasRegistration) {
            binding.llVehicleBTrailerInsurance.visibility = View.VISIBLE

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceCompanyName,
                binding.tvStatementVehicleBCardTrailerInsuranceCompanyName.text.toString(),
                statementData.vehicleBTrailerInsuranceCompanyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsurancePolicyNumber,
                binding.tvStatementVehicleBCardTrailerInsurancePolicyNumber.text.toString(),
                statementData.vehicleBTrailerInsuranceCompanyPolicyNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceGreenCardNumber,
                binding.tvStatementVehicleBCardTrailerInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleBTrailerInsuranceCompanyGreenCardNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleBTrailerInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleBCardTrailerInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleBTrailerInsuranceCertificateExpirationDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyName,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyName.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyAddress,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyAddress.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyAddress
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyCountry,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyCountry.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyCountry
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyPhoneNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyEmail,
                binding.tvStatementVehicleBCardTrailerInsuranceAgencyEmail.text.toString(),
                statementData.vehicleBTrailerInsuranceAgencyEmail
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardTrailerInsuranceDamageCovered,
                binding.tvStatementVehicleBCardTrailerInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleBTrailerMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleBTrailerInsurance.visibility = View.GONE
        }
    }

    private fun updateVehicleBMotorInsuranceFragmentFields(statementData: StatementData) {
        if (!statementData.vehicleBMotorAbsent) {
            binding.llVehicleBMotorInsurance.visibility = View.VISIBLE

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceCompanyName,
                binding.tvStatementVehicleBCardMotorInsuranceCompanyName.text.toString(),
                statementData.vehicleBInsuranceCompanyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsurancePolicyNumber,
                binding.tvStatementVehicleBCardMotorInsurancePolicyNumber.text.toString(),
                statementData.vehicleBInsuranceCompanyPolicyNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceGreenCardNumber,
                binding.tvStatementVehicleBCardMotorInsuranceGreenCardNumber.text.toString(),
                statementData.vehicleBInsuranceCompanyGreenCardNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceCertificateAvailabilityDate,
                binding.tvStatementVehicleBCardMotorInsuranceCertificateAvailabilityDate.text.toString(),
                statementData.vehicleBInsuranceCertificateAvailabilityDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceCertificateExpirationDate,
                binding.tvStatementVehicleBCardMotorInsuranceCertificateExpirationDate.text.toString(),
                statementData.vehicleBInsuranceCertificateExpirationDate?.to24Format()
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyName,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyName.text.toString(),
                statementData.vehicleBInsuranceAgencyName
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyAddress,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyAddress.text.toString(),
                statementData.vehicleBInsuranceAgencyAddress
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyCountry,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyCountry.text.toString(),
                statementData.vehicleBInsuranceAgencyCountry
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyPhoneNumber,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyPhoneNumber.text.toString(),
                statementData.vehicleBInsuranceAgencyPhoneNumber
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceAgencyEmail,
                binding.tvStatementVehicleBCardMotorInsuranceAgencyEmail.text.toString(),
                statementData.vehicleBInsuranceAgencyEmail
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorInsuranceDamageCovered,
                binding.tvStatementVehicleBCardMotorInsuranceDamageCovered.text.toString(),
                if (statementData.vehicleBMaterialDamageCovered) requireContext().getString(R.string.yes) else requireContext().getString(
                    R.string.no
                )
            )

        } else {
            binding.llVehicleBMotorInsurance.visibility = View.GONE
        }
    }

    private fun updateVehicleBNewStatementFragmentFields(statementData: StatementData) {
        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderLastName,
            binding.tvStatementVehicleBCardPolicyHolderLastName.text.toString(),
            statementData.policyHolderBLastName
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderFirstName,
            binding.tvStatementVehicleBCardPolicyHolderFirstName.text.toString(),
            statementData.policyHolderBFirstName
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderAddress,
            binding.tvStatementVehicleBCardPolicyHolderAddress.text.toString(),
            statementData.policyHolderBAddress
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderPostalCode,
            binding.tvStatementVehicleBCardPolicyHolderPostalCode.text.toString(),
            statementData.policyHolderBPostalCode
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderPhoneNumber,
            binding.tvStatementVehicleBCardPolicyHolderPhoneNumber.text.toString(),
            statementData.policyHolderBPhoneNumber
        )

        setTextViewWithBoldLabelAndNormalText(
            binding.tvStatementVehicleBCardPolicyHolderEmail,
            binding.tvStatementVehicleBCardPolicyHolderEmail.text.toString(),
            statementData.policyHolderBEmail
        )

        if (!statementData.vehicleBMotorAbsent) {
            binding.llVehicleBMotorFields.visibility = View.VISIBLE
            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorMarkType,
                binding.tvStatementVehicleBCardMotorMarkType.text.toString(),
                statementData.vehicleBMotorMarkType
            )

            setTextViewWithBoldLabelAndNormalText(
                binding.tvStatementVehicleBCardMotorRegistrationNumber,
                binding.tvStatementVehicleBCardMotorRegistrationNumber.text.toString(),
                statementData.vehicleBMotorLicensePlate
            )

            setTextViewWithBoldLabelAndNormalText(
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

                setTextViewWithBoldLabelAndNormalText(
                    binding.tvStatementVehicleBCardTrailerRegistrationNumber,
                    binding.tvStatementVehicleBCardTrailerRegistrationNumber.text.toString(),
                    statementData.vehicleBTrailerLicensePlate
                )

                setTextViewWithBoldLabelAndNormalText(
                    binding.tvStatementVehicleBCardTrailerCountry,
                    binding.tvStatementVehicleBCardTrailerCountry.text.toString(),
                    statementData.vehicleBTrailerCountryOfRegistration
                )
            }
        } else {
            binding.llVehicleBTrailerFields.visibility = View.GONE
        }
    }
}