package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

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
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleAMotorInsuranceBinding
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

class VehicleAMotorInsuranceFragment : Fragment(), StatementDataHandler, IValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController
    private lateinit var insuranceCertificateDateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    private var _binding: FragmentVehicleAMotorInsuranceBinding? = null
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
        _binding = FragmentVehicleAMotorInsuranceBinding.inflate(inflater, container, false)

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
            binding.etStatementVehicleAInsuranceCompanyName.setText(statementData.vehicleAInsuranceCompanyName)
            binding.etStatementVehicleAInsuranceCompanyPolicyNumber.setText(statementData.vehicleAInsuranceCompanyPolicyNumber)
            binding.etStatementVehicleAInsuranceCompanyGreenCardNumber.setText(statementData.vehicleAInsuranceCompanyGreenCardNumber)
            binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate.setText(
                statementData.vehicleAInsuranceCertificateAvailabilityDate?.to24Format() ?: ""
            )
            binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate.setText(
                statementData.vehicleAInsuranceCertificateExpirationDate?.to24Format() ?: ""
            )
            binding.etStatementVehicleAInsuranceAgencyName.setText(statementData.vehicleAInsuranceAgencyName)
            binding.etStatementVehicleAInsuranceAgencyAddress.setText(statementData.vehicleAInsuranceAgencyAddress)
            binding.etStatementVehicleAInsuranceAgencyCountry.setText(statementData.vehicleAInsuranceAgencyCountry)
            binding.etStatementVehicleAInsuranceAgencyPhoneNumber.setText(statementData.vehicleAInsuranceAgencyPhoneNumber)
            binding.etStatementVehicleAInsuranceAgencyEmail.setText(statementData.vehicleAInsuranceAgencyEmail)
            binding.cbStatementDamagedCovered.isChecked =
                statementData.vehicleAMotorMaterialDamageCovered
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleAInsuranceCompanyName =
                binding.etStatementVehicleAInsuranceCompanyName.text.toString()
            this.vehicleAInsuranceCompanyPolicyNumber =
                binding.etStatementVehicleAInsuranceCompanyPolicyNumber.text.toString()
            this.vehicleAInsuranceCompanyGreenCardNumber =
                binding.etStatementVehicleAInsuranceCompanyGreenCardNumber.text.toString()
            this.vehicleAInsuranceCertificateAvailabilityDate =
                binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate.text.toString()
                    .toLocalDate()
            this.vehicleAInsuranceCertificateExpirationDate =
                binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate.text.toString()
                    .toLocalDate()
            this.vehicleAInsuranceAgencyName =
                binding.etStatementVehicleAInsuranceAgencyName.text.toString()
            this.vehicleAInsuranceAgencyAddress =
                binding.etStatementVehicleAInsuranceAgencyAddress.text.toString()
            this.vehicleAInsuranceAgencyCountry =
                binding.etStatementVehicleAInsuranceAgencyCountry.text.toString()
            this.vehicleAInsuranceAgencyPhoneNumber =
                binding.etStatementVehicleAInsuranceAgencyPhoneNumber.text.toString()
            this.vehicleAInsuranceAgencyEmail =
                binding.etStatementVehicleAInsuranceAgencyEmail.text.toString()
            this.vehicleAMotorMaterialDamageCovered = binding.cbStatementDamagedCovered.isChecked
        }
    }

    override fun setupValidation(
    ) {
        this.fields = listOf(
            binding.etStatementVehicleAInsuranceCompanyName,
            binding.etStatementVehicleAInsuranceCompanyPolicyNumber,
            binding.etStatementVehicleAInsuranceCompanyGreenCardNumber,
            binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate,
            binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate,
            binding.etStatementVehicleAInsuranceAgencyName,
            binding.etStatementVehicleAInsuranceAgencyAddress,
            binding.etStatementVehicleAInsuranceAgencyCountry,
            binding.etStatementVehicleAInsuranceAgencyPhoneNumber,
            binding.etStatementVehicleAInsuranceAgencyEmail,
            binding.cbStatementDamagedCovered
        )

        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementVehicleAInsuranceCompanyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceCompanyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleAInsuranceCompanyPolicyNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceCompanyGreenCardNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate, { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                }, formHelper.errors.futureDate
            ),
            Triple(
                binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyCountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyPhoneNumber,
                { value -> !value.isNullOrEmpty() && value.any { it.isLetter() } },
                formHelper.errors.noLettersAllowed
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAInsuranceAgencyEmail,
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

    private fun handlePreviousButtonClick() {
        updateViewModelFromUI(model)
        navController.popBackStack()
    }

    private fun handleNextButtonClick() {
        formHelper.clearErrors()
        updateViewModelFromUI(model)
        formHelper.validateFields(validationRules)

        if (fields.none { it.error != null }) {
            navigateToNextFragment()
        }
    }

    private fun navigateToNextFragment() {
        if (model.statementData.value?.vehicleATrailerHasRegistration == true) {
            navController.navigate(R.id.vehicleATrailerInsuranceFragment)
        } else {
            navController.navigate(R.id.vehicleADriverFragment)
        }
    }

    private fun handleDatePickerButtonClick() {
        insuranceCertificateDateRangePicker.show(
            parentFragmentManager,
            "insurance_certificate_date_picker"
        )
        insuranceCertificateDateRangePicker.addOnPositiveButtonClickListener { selection ->
            handleDateSelection(selection)
        }
    }

    private fun handleDateSelection(selection: Pair<Long, Long>) {
        insuranceCertificateAvailabilityDate = Instant.ofEpochMilli(selection.first).atZone(
            ZoneId.systemDefault()
        ).toLocalDate()
        binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate.setText(
            insuranceCertificateAvailabilityDate?.to24Format() ?: ""
        )
        insuranceCertificateExpirationDate = Instant.ofEpochMilli(selection.second).atZone(
            ZoneId.systemDefault()
        ).toLocalDate()
        binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate.setText(
            insuranceCertificateExpirationDate?.to24Format() ?: ""
        )
    }

    private fun notifyPropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        changeSupport.firePropertyChange(
            propertyName,
            oldValue,
            newValue
        )
    }
}