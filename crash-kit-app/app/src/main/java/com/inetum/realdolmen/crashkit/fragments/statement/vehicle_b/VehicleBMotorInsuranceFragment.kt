package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBMotorInsuranceBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.IValidationConfigure
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import java.beans.PropertyChangeSupport
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class VehicleBMotorInsuranceFragment : Fragment(), StatementDataHandler, IValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController
    private lateinit var insuranceCertificateDateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    private var _binding: FragmentVehicleBMotorInsuranceBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()
    private lateinit var formHelper: FormHelper

    private val changeSupport = PropertyChangeSupport(this)

    private var insuranceCertificateAvailabilityDate: LocalDate? = null
        set(newValue) {
            notifyPropertyChange("insuranceCertificateAvailabilityDate", field, newValue)
            field = newValue
        }

    private var insuranceCertificateExpirationDate: LocalDate? = null
        set(newValue) {
            notifyPropertyChange("insuranceCertificateExpirationDate", field, newValue)
            field = newValue
        }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lock the screen orientation to portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentVehicleBMotorInsuranceBinding.inflate(inflater, container, false)

        insuranceCertificateDateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(requireContext().getString(R.string.date_range_picker_title))
            .build()

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

        formHelper = FormHelper(requireContext(), fields)

        setupValidation()

        updateUIFromViewModel(model)

        setupClickListeners()
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner) { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementVehicleBInsuranceCompanyName.setText(statementData.vehicleBInsuranceCompanyName)
            binding.etStatementVehicleBInsuranceCompanyPolicyNumber.setText(statementData.vehicleBInsuranceCompanyPolicyNumber)
            binding.etStatementVehicleBInsuranceCompanyGreenCardNumber.setText(statementData.vehicleBInsuranceCompanyGreenCardNumber)
            binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate.setText(
                statementData.vehicleBInsuranceCertificateAvailabilityDate?.to24Format() ?: ""
            )
            binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate.setText(
                statementData.vehicleBInsuranceCertificateExpirationDate?.to24Format() ?: ""
            )
            binding.etStatementVehicleBInsuranceAgencyName.setText(statementData.vehicleBInsuranceAgencyName)
            binding.etStatementVehicleBInsuranceAgencyAddress.setText(statementData.vehicleBInsuranceAgencyAddress)
            binding.etStatementVehicleBInsuranceAgencyCountry.setText(statementData.vehicleBInsuranceAgencyCountry)
            binding.etStatementVehicleBInsuranceAgencyPhoneNumber.setText(statementData.vehicleBInsuranceAgencyPhoneNumber)
            binding.etStatementVehicleBInsuranceAgencyEmail.setText(statementData.vehicleBInsuranceAgencyEmail)
            binding.cbStatementDamagedCovered.isChecked =
                statementData.vehicleBMaterialDamageCovered
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBInsuranceCompanyName =
                binding.etStatementVehicleBInsuranceCompanyName.text.toString()
            this.vehicleBInsuranceCompanyPolicyNumber =
                binding.etStatementVehicleBInsuranceCompanyPolicyNumber.text.toString()
            this.vehicleBInsuranceCompanyGreenCardNumber =
                binding.etStatementVehicleBInsuranceCompanyGreenCardNumber.text.toString()
            this.vehicleBInsuranceCertificateAvailabilityDate =
                binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate.text.toString()
                    .toLocalDate()
            this.vehicleBInsuranceCertificateExpirationDate =
                binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate.text.toString()
                    .toLocalDate()
            this.vehicleBInsuranceAgencyName =
                binding.etStatementVehicleBInsuranceAgencyName.text.toString()
            this.vehicleBInsuranceAgencyAddress =
                binding.etStatementVehicleBInsuranceAgencyAddress.text.toString()
            this.vehicleBInsuranceAgencyCountry =
                binding.etStatementVehicleBInsuranceAgencyCountry.text.toString()
            this.vehicleBInsuranceAgencyPhoneNumber =
                binding.etStatementVehicleBInsuranceAgencyPhoneNumber.text.toString()
            this.vehicleBInsuranceAgencyEmail =
                binding.etStatementVehicleBInsuranceAgencyEmail.text.toString()
            this.vehicleBMaterialDamageCovered = binding.cbStatementDamagedCovered.isChecked
        }
    }

    override fun setupValidation(
    ) {
        this.fields = listOf(
            binding.etStatementVehicleBInsuranceCompanyName,
            binding.etStatementVehicleBInsuranceCompanyPolicyNumber,
            binding.etStatementVehicleBInsuranceCompanyGreenCardNumber,
            binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate,
            binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate,
            binding.etStatementVehicleBInsuranceAgencyName,
            binding.etStatementVehicleBInsuranceAgencyAddress,
            binding.etStatementVehicleBInsuranceAgencyCountry,
            binding.etStatementVehicleBInsuranceAgencyPhoneNumber,
            binding.etStatementVehicleBInsuranceAgencyEmail,
            binding.cbStatementDamagedCovered
        )

        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementVehicleBInsuranceCompanyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyPolicyNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyGreenCardNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate, { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                }, formHelper.errors.futureDate
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyCountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyPhoneNumber,
                { value -> !value.isNullOrEmpty() && value.any { it.isLetter() } },
                formHelper.errors.noLettersAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            )
        )
    }

    private fun setupClickListeners() {
        binding.btnStatementAccidentPrevious.setOnClickListener {
            handlePreviousButtonClick()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            handleNextButtonClick()
        }

        binding.btnDateTimePickerInsuranceCertificateAvailabilityDate.setOnClickListener {
            handleDatePickerButtonClick()
        }
    }

    private fun handleDatePickerButtonClick() {
        insuranceCertificateDateRangePicker.show(
            parentFragmentManager,
            "insurance_certificate_date_picker"
        )
        insuranceCertificateDateRangePicker.addOnPositiveButtonClickListener { selection ->
            insuranceCertificateAvailabilityDate = Instant.ofEpochMilli(selection.first).atZone(
                ZoneId.systemDefault()
            ).toLocalDate()
            binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate.setText(
                insuranceCertificateAvailabilityDate?.to24Format() ?: ""
            )
            insuranceCertificateExpirationDate = Instant.ofEpochMilli(selection.second).atZone(
                ZoneId.systemDefault()
            ).toLocalDate()
            binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate.setText(
                insuranceCertificateExpirationDate?.to24Format() ?: ""
            )
        }
    }

    private fun notifyPropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        changeSupport.firePropertyChange(
            propertyName,
            oldValue,
            newValue
        )
    }

    private fun handleNextButtonClick() {
        formHelper.clearErrors()

        updateViewModelFromUI(model)

        formHelper.validateFields(validationRules)

        if (fields.none { it.error != null }) {
            // If no errors, navigate to the next fragment
            navigateToNextFragment()
        }
    }

    private fun navigateToNextFragment() {
        if (model.statementData.value?.vehicleBTrailerHasRegistration == true) {
            navController.navigate(R.id.vehicleBTrailerInsuranceFragment)
        } else {
            navController.navigate(R.id.vehicleBDriverFragment)
        }
    }

    private fun handlePreviousButtonClick() {
        updateViewModelFromUI(model)

        navController.popBackStack()
    }
}