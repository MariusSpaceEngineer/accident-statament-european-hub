package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBDriverBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.AccidentStatementLists
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.showToast
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import java.beans.PropertyChangeSupport
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class VehicleBDriverFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController
    private lateinit var formHelper: FormHelper

    private var _binding: FragmentVehicleBDriverBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()

    private val changeSupport = PropertyChangeSupport(this)

    private var drivingLicenseExpirationDate: LocalDate? = null
        set(newValue) {
            val oldValue = field
            field = newValue

            // Notify listeners about the change
            changeSupport.firePropertyChange(
                "drivingLicenseExpirationDate",
                oldValue,
                newValue
            )
        }

    private var driverDateOfBirth: LocalDate? = null
        set(newValue) {
            val oldValue = field
            field = newValue

            // Notify listeners about the change
            changeSupport.firePropertyChange(
                "driverDateOfBirth",
                oldValue,
                newValue
            )
        }

    private val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select date")
        .build()

    private var currentPicker: String? = null

    private val drivingLicenseCategories = AccidentStatementLists.drivingLicenseCategories

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
            FragmentVehicleBDriverBinding.inflate(inflater, container, false)
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

        setupDrivingLicenseCategorySpinner()

        setupValidation()

        updateUIFromViewModel(model)

        setupButtonClickListeners()

        binding.cbStatementVehicleBDriverIsPolicyHolder.setOnCheckedChangeListener { _, isChecked ->
            updateDriverFields(isChecked)
            removeDriverFieldsErrors()
        }
    }

    private fun setupDrivingLicenseCategorySpinner() {
        val spinner: Spinner = binding.spinStatementVehicleBDriverCategory
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            drivingLicenseCategories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }


    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementVehicleBDriverName.setText(statementData.vehicleBDriverLastName)
            binding.etStatementVehicleBDriverFirstName.setText(statementData.vehicleBDriverFirstName)
            binding.etStatementVehicleBDriverDateOfBirth.setText(
                statementData.vehicleBDriverDateOfBirth?.to24Format() ?: ""
            )
            binding.etStatementVehicleBDriverAddress.setText(statementData.vehicleBDriverAddress)
            binding.etStatementVehicleBDriverCountry.setText(statementData.vehicleBDriverCountry)
            binding.etStatementVehicleBDriverPhoneNumber.setText(statementData.vehicleBDriverPhoneNumber)
            binding.etStatementVehicleBDriverEmail.setText(statementData.vehicleBDriverEmail)
            binding.etStatementVehicleBDriverDrivingLicenseNumber.setText(statementData.vehicleBDriverDrivingLicenseNr)
            binding.etStatementVehicleBDrivingLicenseExpirationDate.setText(
                statementData.vehicleBDriverDrivingLicenseExpirationDate?.to24Format()
                    ?: ""
            )
            binding.cbStatementVehicleBDriverIsPolicyHolder.isChecked =
                statementData.vehicleBDriverIsPolicyHolder

            if (statementData.vehicleBDriverDrivingLicenseCategory.isNotBlank()) {
                val position =
                    drivingLicenseCategories.indexOf(statementData.vehicleBDriverDrivingLicenseCategory)
                binding.spinStatementVehicleBDriverCategory.setSelection(position)
            }
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBDriverLastName = binding.etStatementVehicleBDriverName.text.toString()
            this.vehicleBDriverFirstName =
                binding.etStatementVehicleBDriverFirstName.text.toString()
            this.vehicleBDriverDateOfBirth =
                driverDateOfBirth
            this.vehicleBDriverAddress =
                binding.etStatementVehicleBDriverAddress.text.toString()
            this.vehicleBDriverCountry =
                binding.etStatementVehicleBDriverCountry.text.toString()
            this.vehicleBDriverPhoneNumber =
                binding.etStatementVehicleBDriverPhoneNumber.text.toString()
            this.vehicleBDriverDrivingLicenseCategory =
                binding.spinStatementVehicleBDriverCategory.selectedItem.toString()
            this.vehicleBDriverEmail = binding.etStatementVehicleBDriverEmail.text.toString()
            this.vehicleBDriverDrivingLicenseNr =
                binding.etStatementVehicleBDriverDrivingLicenseNumber.text.toString()
            this.vehicleBDriverDrivingLicenseExpirationDate = drivingLicenseExpirationDate
            this.vehicleBDriverIsPolicyHolder =
                binding.cbStatementVehicleBDriverIsPolicyHolder.isChecked
        }
    }

    override fun setupValidation(
    ) {
        this.fields = listOf(
            binding.etStatementVehicleBDriverName,
            binding.etStatementVehicleBDriverFirstName,
            binding.etStatementVehicleBDriverDateOfBirth,
            binding.etStatementVehicleBDriverAddress,
            binding.etStatementVehicleBDriverCountry,
            binding.etStatementVehicleBDriverPhoneNumber,
            binding.etStatementVehicleBDriverEmail,
            binding.etStatementVehicleBDriverDrivingLicenseNumber,
            binding.etStatementVehicleBDrivingLicenseExpirationDate
        )


        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementVehicleBDriverName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBDriverFirstName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),

            Triple(
                binding.etStatementVehicleBDriverDateOfBirth,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverDateOfBirth, { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                }, formHelper.errors.futureDate
            ),
            Triple(
                binding.etStatementVehicleBDriverAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverCountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBDriverPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            ),
            Triple(
                binding.etStatementVehicleBDriverDrivingLicenseNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDrivingLicenseExpirationDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDrivingLicenseExpirationDate, { value ->
                    value?.toLocalDate()?.isBefore(LocalDate.now()) ?: false
                }, formHelper.errors.pastDate
            ),
        )
    }
    private fun updateDriverFields(checked: Boolean) {
        if (checked) {
            model.statementData.observe(viewLifecycleOwner) { statementData ->
                // Update the UI here based on the new statementData
                binding.etStatementVehicleBDriverName.setText(statementData.policyHolderBLastName)
                binding.etStatementVehicleBDriverFirstName.setText(statementData.policyHolderBFirstName)
                binding.etStatementVehicleBDriverAddress.setText(statementData.policyHolderBAddress)
                binding.etStatementVehicleBDriverPhoneNumber.setText(statementData.policyHolderBPhoneNumber)
                binding.etStatementVehicleBDriverEmail.setText(statementData.policyHolderBEmail)
            }

        } else {
            binding.etStatementVehicleBDriverName.text = null
            binding.etStatementVehicleBDriverFirstName.text = null
            binding.etStatementVehicleBDriverAddress.text = null
            binding.etStatementVehicleBDriverPhoneNumber.text = null
            binding.etStatementVehicleBDriverEmail.text = null
        }
    }

    private fun removeDriverFieldsErrors() {
        // Remove error messages from fields
        (fields as MutableList<TextView>).forEach { field ->
            if (field == binding.etStatementVehicleBDriverName || field == binding.etStatementVehicleBDriverFirstName
                || field == binding.etStatementVehicleBDriverAddress || field == binding.etStatementVehicleBDriverPhoneNumber
                || field == binding.etStatementVehicleBDriverEmail
            ) {
                (field as EditText).error = null
            }
        }
    }

    private fun setupButtonClickListeners() {
        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            formHelper.validateFields(validationRules)
            if (fields.none { it.error != null } && validateSpinner(binding.spinStatementVehicleBDriverCategory)) {
                updateViewModelFromUI(model)
                navController.navigate(R.id.vehicleBCircumstancesFragment)
            } else if (!validateSpinner(binding.spinStatementVehicleBDriverCategory)) {
                requireContext().showToast("Please select a valid category.")
            }
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate =
                Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
            when (currentPicker) {
                "driving_license_date_picker" -> {
                    drivingLicenseExpirationDate = selectedDate
                    binding.etStatementVehicleBDrivingLicenseExpirationDate.setText(
                        (drivingLicenseExpirationDate?.to24Format() ?: "")
                    )
                    binding.etStatementVehicleBDrivingLicenseExpirationDate.error = null
                }

                "date_of_birth_date_picker" -> {
                    driverDateOfBirth = selectedDate
                    binding.etStatementVehicleBDriverDateOfBirth.setText(
                        (driverDateOfBirth?.to24Format() ?: "")
                    )
                    binding.etStatementVehicleBDriverDateOfBirth.error = null
                }
            }
        }

        binding.btnDrivingLicenseExpirationDatePicker.setOnClickListener {
            currentPicker = "driving_license_date_picker"
            datePicker.show(parentFragmentManager, currentPicker)
        }

        binding.btnDateOfBirthDatePicker.setOnClickListener {
            currentPicker = "date_of_birth_date_picker"
            datePicker.show(parentFragmentManager, currentPicker)
        }
    }

    private fun validateSpinner(spinner: Spinner): Boolean {
        return spinner.selectedItem != null && spinner.selectedItem.toString().isNotBlank()
    }

}