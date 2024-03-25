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
import com.google.android.material.datepicker.MaterialDatePicker
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBInsuranceBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.helpers.InputFieldsErrors
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

class VehicleBInsuranceFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleBInsuranceBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()
    private var formHelper: FormHelper = FormHelper(fields)
    private val inputFieldsErrors = InputFieldsErrors()

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }

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
        _binding =
            FragmentVehicleBInsuranceBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupValidation()

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_b_insurance_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                fragmentNavigationHelper.navigateToFragment(
                    R.id.fragmentContainerView,
                    VehicleBDriverFragment(),
                    "vehicle_b_driver_fragment"
                )
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

            binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate.setText(
                (insuranceCertificateAvailabilityDate?.to24Format() ?: "")
            )
            binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate.error = null
        }

        addDateChangeListener {
            binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate.setText(
                (insuranceCertificateExpirationDate?.to24Format() ?: "")
            )
            binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate.error = null

        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
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
        })
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

    private fun addDateChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
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
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                this.inputFieldsErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyPolicyNumber,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyGreenCardNumber,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate, { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                }, this.inputFieldsErrors.futureDate
            ),
            Triple(
                binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyName,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                this.inputFieldsErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyAddress,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyCountry,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                this.inputFieldsErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyPhoneNumber,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyPhoneNumber,
                { value -> !value.isNullOrEmpty() && value.any { it.isLetter() } },
                this.inputFieldsErrors.noLettersAllowed
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyEmail,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBInsuranceAgencyEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                this.inputFieldsErrors.invalidEmail
            )
        )
    }

}