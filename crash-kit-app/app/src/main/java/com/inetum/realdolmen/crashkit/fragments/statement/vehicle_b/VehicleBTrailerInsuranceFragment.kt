package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

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
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBTrailerInsuranceBinding
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


class VehicleBTrailerInsuranceFragment : Fragment(), StatementDataHandler, IValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentVehicleBTrailerInsuranceBinding? = null
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
        _binding = FragmentVehicleBTrailerInsuranceBinding.inflate(inflater, container, false)
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
                navController.navigate(R.id.vehicleBDriverFragment)
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

            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateAvailabilityDate.setText(
                (insuranceCertificateAvailabilityDate?.to24Format() ?: "")
            )
            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateAvailabilityDate.error =
                null
        }

        addDateChangeListener {
            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateExpirationDate.setText(
                (insuranceCertificateExpirationDate?.to24Format() ?: "")

            )
            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateExpirationDate.error = null
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementVehicleBTrailerInsuranceCompanyName.setText(statementData.vehicleBTrailerInsuranceCompanyName)
            binding.etStatementVehicleBTrailerInsuranceCompanyPolicyNumber.setText(statementData.vehicleBTrailerInsuranceCompanyPolicyNumber)
            binding.etStatementVehicleBTrailerInsuranceCompanyGreenCardNumber.setText(statementData.vehicleBTrailerInsuranceCompanyGreenCardNumber)
            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateAvailabilityDate.setText(
                statementData.vehicleATrailerInsuranceCertificateAvailabilityDate?.to24Format()
                    ?: ""
            )
            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateExpirationDate.setText(
                statementData.vehicleATrailerInsuranceCertificateExpirationDate?.to24Format() ?: ""
            )
            binding.etStatementVehicleBTrailerInsuranceAgencyName.setText(statementData.vehicleBTrailerInsuranceAgencyName)
            binding.etStatementVehicleBTrailerInsuranceAgencyAddress.setText(statementData.vehicleBTrailerInsuranceAgencyAddress)
            binding.etStatementVehicleBTrailerInsuranceAgencyCountry.setText(statementData.vehicleBTrailerInsuranceAgencyCountry)
            binding.etStatementVehicleBTrailerInsuranceAgencyPhoneNumber.setText(statementData.vehicleBTrailerInsuranceAgencyPhoneNumber)
            binding.etStatementVehicleBTrailerInsuranceAgencyEmail.setText(statementData.vehicleBTrailerInsuranceAgencyEmail)
            binding.cbStatementDamagedCovered.isChecked =
                statementData.vehicleATrailerMaterialDamageCovered
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBTrailerInsuranceCompanyName =
                binding.etStatementVehicleBTrailerInsuranceCompanyName.text.toString()
            this.vehicleBTrailerInsuranceCompanyPolicyNumber =
                binding.etStatementVehicleBTrailerInsuranceCompanyPolicyNumber.text.toString()
            this.vehicleBTrailerInsuranceCompanyGreenCardNumber =
                binding.etStatementVehicleBTrailerInsuranceCompanyGreenCardNumber.text.toString()
            this.vehicleBTrailerInsuranceCertificateAvailabilityDate =
                binding.etStatementVehicleBTrailerInsuranceCompanyCertificateAvailabilityDate.text.toString()
                    .toLocalDate()
            this.vehicleBTrailerInsuranceCertificateExpirationDate =
                binding.etStatementVehicleBTrailerInsuranceCompanyCertificateExpirationDate.text.toString()
                    .toLocalDate()
            this.vehicleBTrailerInsuranceAgencyName =
                binding.etStatementVehicleBTrailerInsuranceAgencyName.text.toString()
            this.vehicleBTrailerInsuranceAgencyAddress =
                binding.etStatementVehicleBTrailerInsuranceAgencyAddress.text.toString()
            this.vehicleBTrailerInsuranceAgencyCountry =
                binding.etStatementVehicleBTrailerInsuranceAgencyCountry.text.toString()
            this.vehicleBTrailerInsuranceAgencyPhoneNumber =
                binding.etStatementVehicleBTrailerInsuranceAgencyPhoneNumber.text.toString()
            this.vehicleBTrailerInsuranceAgencyEmail =
                binding.etStatementVehicleBTrailerInsuranceAgencyEmail.text.toString()
            this.vehicleBTrailerMaterialDamageCovered = binding.cbStatementDamagedCovered.isChecked
        }
    }

    private fun addDateChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    override fun setupValidation(
    ) {
        this.fields = listOf(
            binding.etStatementVehicleBTrailerInsuranceCompanyName,
            binding.etStatementVehicleBTrailerInsuranceCompanyPolicyNumber,
            binding.etStatementVehicleBTrailerInsuranceCompanyGreenCardNumber,
            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateAvailabilityDate,
            binding.etStatementVehicleBTrailerInsuranceCompanyCertificateExpirationDate,
            binding.etStatementVehicleBTrailerInsuranceAgencyName,
            binding.etStatementVehicleBTrailerInsuranceAgencyAddress,
            binding.etStatementVehicleBTrailerInsuranceAgencyCountry,
            binding.etStatementVehicleBTrailerInsuranceAgencyPhoneNumber,
            binding.etStatementVehicleBTrailerInsuranceAgencyEmail,
            binding.cbStatementDamagedCovered
        )

        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementVehicleBTrailerInsuranceCompanyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceCompanyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceCompanyPolicyNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceCompanyGreenCardNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceCompanyCertificateAvailabilityDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceCompanyCertificateAvailabilityDate,
                { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                },
                formHelper.errors.futureDate
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceCompanyCertificateExpirationDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyCountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyPhoneNumber,
                { value -> !value.isNullOrEmpty() && value.any { it.isLetter() } },
                formHelper.errors.noLettersAllowed
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBTrailerInsuranceAgencyEmail,
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