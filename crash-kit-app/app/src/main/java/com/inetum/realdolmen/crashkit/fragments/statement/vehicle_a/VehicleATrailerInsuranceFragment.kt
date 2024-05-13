package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleATrailerInsuranceBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.IValidationConfigure
import com.inetum.realdolmen.crashkit.utils.printBackStack
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class VehicleATrailerInsuranceFragment : Fragment(), StatementDataHandler, IValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentVehicleATrailerInsuranceBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()
    private lateinit var formHelper: FormHelper

    private val changeSupport = PropertyChangeSupport(this)

    private var insuranceCertificateAvailabilityDate: LocalDate? = null
        set(newValue) {
            val oldValue = field
            field = newValue

            // Notify listeners about the change
            changeSupport.firePropertyChange(
                "insuranceCertificateAvailabilityDate",
                oldValue,
                newValue
            )
        }

    private var insuranceCertificateExpirationDate: LocalDate? = null
        set(newValue) {
            val oldValue = field
            field = newValue

            // Notify listeners about the change
            changeSupport.firePropertyChange(
                "insuranceCertificateExpirationDate",
                oldValue,
                newValue
            )
        }

    private val insuranceCertificateDateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
        .setTitleText("Select dates")
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleATrailerInsuranceBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
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

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                // If no errors, navigate to the next fragment
                navController.navigate(R.id.vehicleADriverFragment)
            }

        }

        binding.btnDateTimePickerTrailerInsuranceCertificateAvailabilityDate.setOnClickListener {
            insuranceCertificateDateRangePicker.show(
                parentFragmentManager,
                "insurance_certificate_date_picker"
            )
            insuranceCertificateDateRangePicker.addOnPositiveButtonClickListener { selection ->
                insuranceCertificateAvailabilityDate = Instant.ofEpochMilli(selection.first).atZone(
                    ZoneId.systemDefault()
                ).toLocalDate()
                insuranceCertificateExpirationDate = Instant.ofEpochMilli(selection.second).atZone(
                    ZoneId.systemDefault()
                ).toLocalDate()
            }
        }

        addDateChangeListener {

            binding.etStatementVehicleATrailerInsuranceCompanyCertificateAvailabilityDate.setText(
                (insuranceCertificateAvailabilityDate?.to24Format() ?: "")
            )
            binding.etStatementVehicleATrailerInsuranceCompanyCertificateAvailabilityDate.error =
                null
        }

        addDateChangeListener {
            binding.etStatementVehicleATrailerInsuranceCompanyCertificateExpirationDate.setText(
                (insuranceCertificateExpirationDate?.to24Format() ?: "")

            )
            binding.etStatementVehicleATrailerInsuranceCompanyCertificateExpirationDate.error = null
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementVehicleATrailerInsuranceCompanyName.setText(statementData.vehicleATrailerInsuranceCompanyName)
            binding.etStatementVehicleATrailerInsuranceCompanyPolicyNumber.setText(statementData.vehicleATrailerInsuranceCompanyPolicyNumber)
            binding.etStatementVehicleATrailerInsuranceCompanyGreenCardNumber.setText(statementData.vehicleATrailerInsuranceCompanyGreenCardNumber)
            binding.etStatementVehicleATrailerInsuranceCompanyCertificateAvailabilityDate.setText(
                statementData.vehicleATrailerInsuranceCertificateAvailabilityDate?.to24Format() ?: ""
            )
            binding.etStatementVehicleATrailerInsuranceCompanyCertificateExpirationDate.setText(
                statementData.vehicleATrailerInsuranceCertificateExpirationDate?.to24Format() ?: ""
            )
            binding.etStatementVehicleATrailerInsuranceAgencyName.setText(statementData.vehicleATrailerInsuranceAgencyName)
            binding.etStatementVehicleATrailerInsuranceAgencyAddress.setText(statementData.vehicleATrailerInsuranceAgencyAddress)
            binding.etStatementVehicleATrailerInsuranceAgencyCountry.setText(statementData.vehicleATrailerInsuranceAgencyCountry)
            binding.etStatementVehicleATrailerInsuranceAgencyPhoneNumber.setText(statementData.vehicleATrailerInsuranceAgencyPhoneNumber)
            binding.etStatementVehicleATrailerInsuranceAgencyEmail.setText(statementData.vehicleATrailerInsuranceAgencyEmail)
            binding.cbStatementDamagedCovered.isChecked =
                statementData.vehicleATrailerMaterialDamageCovered
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleATrailerInsuranceCompanyName =
                binding.etStatementVehicleATrailerInsuranceCompanyName.text.toString()
            this.vehicleATrailerInsuranceCompanyPolicyNumber =
                binding.etStatementVehicleATrailerInsuranceCompanyPolicyNumber.text.toString()
            this.vehicleATrailerInsuranceCompanyGreenCardNumber =
                binding.etStatementVehicleATrailerInsuranceCompanyGreenCardNumber.text.toString()
            this.vehicleATrailerInsuranceCertificateAvailabilityDate =
                binding.etStatementVehicleATrailerInsuranceCompanyCertificateAvailabilityDate.text.toString()
                    .toLocalDate()
            this.vehicleATrailerInsuranceCertificateExpirationDate =
                binding.etStatementVehicleATrailerInsuranceCompanyCertificateExpirationDate.text.toString()
                    .toLocalDate()
            this.vehicleATrailerInsuranceAgencyName =
                binding.etStatementVehicleATrailerInsuranceAgencyName.text.toString()
            this.vehicleATrailerInsuranceAgencyAddress =
                binding.etStatementVehicleATrailerInsuranceAgencyAddress.text.toString()
            this.vehicleATrailerInsuranceAgencyCountry =
                binding.etStatementVehicleATrailerInsuranceAgencyCountry.text.toString()
            this.vehicleATrailerInsuranceAgencyPhoneNumber =
                binding.etStatementVehicleATrailerInsuranceAgencyPhoneNumber.text.toString()
            this.vehicleATrailerInsuranceAgencyEmail =
                binding.etStatementVehicleATrailerInsuranceAgencyEmail.text.toString()
            this.vehicleATrailerMaterialDamageCovered = binding.cbStatementDamagedCovered.isChecked
        }
    }

    private fun addDateChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    override fun setupValidation(
    ) {
        this.fields = listOf(
            binding.etStatementVehicleATrailerInsuranceCompanyName,
            binding.etStatementVehicleATrailerInsuranceCompanyPolicyNumber,
            binding.etStatementVehicleATrailerInsuranceCompanyGreenCardNumber,
            binding.etStatementVehicleATrailerInsuranceCompanyCertificateAvailabilityDate,
            binding.etStatementVehicleATrailerInsuranceCompanyCertificateExpirationDate,
            binding.etStatementVehicleATrailerInsuranceAgencyName,
            binding.etStatementVehicleATrailerInsuranceAgencyAddress,
            binding.etStatementVehicleATrailerInsuranceAgencyCountry,
            binding.etStatementVehicleATrailerInsuranceAgencyPhoneNumber,
            binding.etStatementVehicleATrailerInsuranceAgencyEmail,
            binding.cbStatementDamagedCovered
        )

        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementVehicleATrailerInsuranceCompanyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceCompanyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceCompanyPolicyNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceCompanyGreenCardNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceCompanyCertificateAvailabilityDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceCompanyCertificateAvailabilityDate,
                { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                },
                formHelper.errors.futureDate
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceCompanyCertificateExpirationDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyCountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyPhoneNumber,
                { value -> !value.isNullOrEmpty() && value.any { it.isLetter() } },
                formHelper.errors.noLettersAllowed
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleATrailerInsuranceAgencyEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            )
        )
    }
}