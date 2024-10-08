package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleADriverBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.AccidentStatementLists
import com.inetum.realdolmen.crashkit.utils.IValidationConfigure
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.showToast
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import java.beans.PropertyChangeSupport
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class VehicleADriverFragment : Fragment(), StatementDataHandler, IValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController
    private lateinit var formHelper: FormHelper
    private lateinit var datePicker: MaterialDatePicker<Long>

    private var _binding: FragmentVehicleADriverBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> =
        mutableListOf()

    private val changeSupport = PropertyChangeSupport(this)

    private var drivingLicenseExpirationDate: LocalDate? = null
        set(newValue) {
            notifyPropertyChange("drivingLicenseExpirationDate", field, newValue)
            field = newValue
        }

    private var driverDateOfBirth: LocalDate? = null
        set(newValue) {
            notifyPropertyChange("driverDateOfBirth", field, newValue)
            field = newValue
        }

    private var currentPicker: String? = null

    private val drivingLicenseCategories = AccidentStatementLists.drivingLicenseCategories

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
        _binding = FragmentVehicleADriverBinding.inflate(inflater, container, false)

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(requireContext().getString(R.string.date_picker_title))
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
        setupDrivingLicenseCategorySpinner()
        setupValidation()

        updateUIFromViewModel(model)

        setupButtonClickListeners()
        setupCheckboxListener()
    }

    private fun setupCheckboxListener() {
        binding.cbStatementVehicleADriverIsPolicyHolder.setOnCheckedChangeListener { _, isChecked ->
            updateDriverFields(isChecked)
            removeDriverFieldsErrors()
        }
    }

    private fun setupDrivingLicenseCategorySpinner() {
        val spinner: Spinner = binding.spinStatementVehicleADriverCategory
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            drivingLicenseCategories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupButtonClickListeners() {
        setupNavigationButtons()
        setupDatePicker()
        setupDatePickerButtons()
    }

    private fun setupNavigationButtons() {
        binding.btnStatementAccidentPrevious.setOnClickListener {
            handlePreviousButtonClick()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            handleNextButtonClick()
        }
    }

    private fun setupDatePicker() {
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate =
                Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
            when (currentPicker) {
                "driving_license_date_picker" -> updateDrivingLicenseExpirationDate(selectedDate)
                "date_of_birth_date_picker" -> updateDateOfBirth(selectedDate)
            }
        }
    }

    private fun updateDrivingLicenseExpirationDate(selectedDate: LocalDate) {
        drivingLicenseExpirationDate = selectedDate
        binding.etStatementVehicleADriverDrivingLicenseExpirationDate.setText(
            (drivingLicenseExpirationDate?.to24Format() ?: "")
        )
        binding.etStatementVehicleADriverDrivingLicenseExpirationDate.error = null
    }

    private fun updateDateOfBirth(selectedDate: LocalDate) {
        driverDateOfBirth = selectedDate
        binding.etStatementVehicleADriverDateOfBirth.setText(
            (driverDateOfBirth?.to24Format() ?: "")
        )
        binding.etStatementVehicleADriverDateOfBirth.error = null
    }

    private fun setupDatePickerButtons() {
        binding.btnDrivingLicenseExpirationDatePicker.setOnClickListener {
            currentPicker = "driving_license_date_picker"
            datePicker.show(parentFragmentManager, currentPicker)
        }

        binding.btnDateOfBirthDatePicker.setOnClickListener {
            currentPicker = "date_of_birth_date_picker"
            datePicker.show(parentFragmentManager, currentPicker)
        }
    }

    private fun handleNextButtonClick() {
        formHelper.clearErrors()
        formHelper.validateFields(validationRules)

        if (fields.none { it.error != null } && validateSpinner(binding.spinStatementVehicleADriverCategory)) {
            updateViewModelFromUI(model)
            // If no errors, navigate to the next fragment
            navController.navigate(R.id.vehicleACircumstancesFragment)
        } else if (!validateSpinner(binding.spinStatementVehicleADriverCategory)) {
            requireContext().showToast(getString(R.string.no_category_selected))
        }
    }

    private fun handlePreviousButtonClick() {
        updateViewModelFromUI(model)

        navController.popBackStack()
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner) { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementVehicleADriverName.setText(statementData.vehicleADriverLastName)
            binding.etStatementVehicleADriverFirstName.setText(statementData.vehicleADriverFirstName)
            binding.etStatementVehicleADriverDateOfBirth.setText(
                statementData.vehicleADriverDateOfBirth?.to24Format() ?: ""
            )
            binding.etStatementVehicleADriverAddress.setText(statementData.vehicleADriverAddress)
            binding.etStatementVehicleADriverCountry.setText(statementData.vehicleADriverCountry)
            binding.etStatementVehicleADriverPhoneNumber.setText(statementData.vehicleADriverPhoneNumber)
            binding.etStatementVehicleADriverEmail.setText(statementData.vehicleADriverEmail)
            binding.etStatementVehicleADriverDrivingLicenseNumber.setText(statementData.vehicleADriverDrivingLicenseNr)
            binding.etStatementVehicleADriverDrivingLicenseExpirationDate.setText(
                statementData.vehicleADriverDrivingLicenseExpirationDate?.to24Format()
                    ?: ""
            )
            binding.cbStatementVehicleADriverIsPolicyHolder.isChecked =
                statementData.vehicleADriverIsPolicyHolder

            if (statementData.vehicleADriverDrivingLicenseCategory.isNotBlank()) {
                val position =
                    drivingLicenseCategories.indexOf(statementData.vehicleADriverDrivingLicenseCategory)
                binding.spinStatementVehicleADriverCategory.setSelection(position)
            }
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleADriverLastName = binding.etStatementVehicleADriverName.text.toString()
            this.vehicleADriverFirstName =
                binding.etStatementVehicleADriverFirstName.text.toString()
            this.vehicleADriverDateOfBirth =
                driverDateOfBirth
            this.vehicleADriverAddress =
                binding.etStatementVehicleADriverAddress.text.toString()
            this.vehicleADriverCountry =
                binding.etStatementVehicleADriverCountry.text.toString()
            this.vehicleADriverPhoneNumber =
                binding.etStatementVehicleADriverPhoneNumber.text.toString()
            this.vehicleADriverEmail = binding.etStatementVehicleADriverEmail.text.toString()
            this.vehicleADriverDrivingLicenseCategory =
                binding.spinStatementVehicleADriverCategory.selectedItem.toString()
            this.vehicleADriverDrivingLicenseNr =
                binding.etStatementVehicleADriverDrivingLicenseNumber.text.toString()
            this.vehicleADriverDrivingLicenseExpirationDate = drivingLicenseExpirationDate
            this.vehicleADriverIsPolicyHolder =
                binding.cbStatementVehicleADriverIsPolicyHolder.isChecked
        }
    }

    override fun setupValidation(
    ) {
        this.fields = mutableListOf(
            binding.etStatementVehicleADriverName,
            binding.etStatementVehicleADriverFirstName,
            binding.etStatementVehicleADriverDateOfBirth,
            binding.etStatementVehicleADriverAddress,
            binding.etStatementVehicleADriverCountry,
            binding.etStatementVehicleADriverPhoneNumber,
            binding.etStatementVehicleADriverEmail,
            binding.etStatementVehicleADriverDrivingLicenseNumber,
            binding.etStatementVehicleADriverDrivingLicenseExpirationDate
        )

        this.validationRules = mutableListOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementVehicleADriverName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleADriverFirstName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),

            Triple(
                binding.etStatementVehicleADriverDateOfBirth,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverDateOfBirth, { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                }, formHelper.errors.futureDate
            ),
            Triple(
                binding.etStatementVehicleADriverAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverCountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleADriverPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            ),
            Triple(
                binding.etStatementVehicleADriverDrivingLicenseNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverDrivingLicenseExpirationDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverDrivingLicenseExpirationDate, { value ->
                    value?.toLocalDate()?.isBefore(LocalDate.now()) ?: false
                }, formHelper.errors.pastDate
            ),
        )
    }

    /**
     * This method validates the selected item of a given spinner.
     *
     * @param spinner The Spinner object that needs to be validated.
     * @return Boolean Returns true if the spinner has a selected item and the selected item is not blank. Otherwise, it returns false.
     */
    private fun validateSpinner(spinner: Spinner): Boolean {
        return spinner.selectedItem != null && spinner.selectedItem.toString().isNotBlank()
    }

    private fun updateDriverFields(checked: Boolean) {
        if (checked) {
            model.statementData.observe(viewLifecycleOwner) { statementData ->
                // Update the UI here based on the new statementData
                binding.etStatementVehicleADriverName.setText(statementData.policyHolderALastName)
                binding.etStatementVehicleADriverFirstName.setText(statementData.policyHolderAFirstName)
                binding.etStatementVehicleADriverAddress.setText(statementData.policyHolderAAddress)
                binding.etStatementVehicleADriverPhoneNumber.setText(statementData.policyHolderAPhoneNumber)
                binding.etStatementVehicleADriverEmail.setText(statementData.policyHolderAEmail)
            }

        } else {
            binding.etStatementVehicleADriverName.text = null
            binding.etStatementVehicleADriverFirstName.text = null
            binding.etStatementVehicleADriverAddress.text = null
            binding.etStatementVehicleADriverPhoneNumber.text = null
            binding.etStatementVehicleADriverEmail.text = null
        }
    }

    private fun removeDriverFieldsErrors() {
        // Remove error messages from fields
        (fields as MutableList<TextView>).forEach { field ->
            if (field == binding.etStatementVehicleADriverName || field == binding.etStatementVehicleADriverFirstName
                || field == binding.etStatementVehicleADriverAddress || field == binding.etStatementVehicleADriverPhoneNumber
                || field == binding.etStatementVehicleADriverEmail
            ) {
                (field as EditText).error = null
            }
        }
    }

    private fun notifyPropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        changeSupport.firePropertyChange(
            propertyName,
            oldValue,
            newValue
        )
    }
}