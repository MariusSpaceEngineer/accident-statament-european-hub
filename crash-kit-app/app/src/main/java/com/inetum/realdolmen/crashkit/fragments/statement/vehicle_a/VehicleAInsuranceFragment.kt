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
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleAInsuranceBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.printBackStack
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class VehicleAInsuranceFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentVehicleAInsuranceBinding? = null
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
        navController = findNavController()
        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }

        // Inflate the layout for this fragment
        _binding = FragmentVehicleAInsuranceBinding.inflate(inflater, container, false)
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

        binding.btnDateTimePickerInsuranceCertificateAvailabilityDate.setOnClickListener {
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

            binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate.setText(
                (insuranceCertificateAvailabilityDate?.to24Format() ?: "")
            )
            binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate.error = null
        }

        addDateChangeListener {
            binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate.setText(
                (insuranceCertificateExpirationDate?.to24Format() ?: "")

            )
            binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate.error = null
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
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
                statementData.vehicleAMaterialDamageCovered
        })
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
            this.vehicleAMaterialDamageCovered = binding.cbStatementDamagedCovered.isChecked
        }
    }

    private fun addDateChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
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
}