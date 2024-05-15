package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBDriverBinding
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

class VehicleBDriverFragment : Fragment(), StatementDataHandler, IValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController
    private lateinit var formHelper: FormHelper
    private lateinit var datePicker: MaterialDatePicker<Long>

    private var _binding: FragmentVehicleBDriverBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()

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
        _binding = FragmentVehicleBDriverBinding.inflate(inflater, container, false)

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

    private fun setupCheckboxListener() {
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
        binding.etStatementVehicleBDrivingLicenseExpirationDate.setText(
            (drivingLicenseExpirationDate?.to24Format() ?: "")
        )
        binding.etStatementVehicleBDrivingLicenseExpirationDate.error = null
    }

    private fun updateDateOfBirth(selectedDate: LocalDate) {
        driverDateOfBirth = selectedDate
        binding.etStatementVehicleBDriverDateOfBirth.setText(
            (driverDateOfBirth?.to24Format() ?: "")
        )
        binding.etStatementVehicleBDriverDateOfBirth.error = null
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

        if (fields.none { it.error != null } && validateSpinner(binding.spinStatementVehicleBDriverCategory)) {
            updateViewModelFromUI(model)
            // If no errors, navigate to the next fragment
            navController.navigate(R.id.vehicleBCircumstancesFragment)
        } else if (!validateSpinner(binding.spinStatementVehicleBDriverCategory)) {
            requireContext().showToast(getString(R.string.no_category_selected))
        }
    }

    private fun handlePreviousButtonClick() {
        updateViewModelFromUI(model)

        navController.popBackStack()
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
    /**
     * This method validates the selected item of a given spinner.
     *
     * @param spinner The Spinner object that needs to be validated.
     * @return Boolean Returns true if the spinner has a selected item and the selected item is not blank. Otherwise, it returns false.
     */
    private fun validateSpinner(spinner: Spinner): Boolean {
        return spinner.selectedItem != null && spinner.selectedItem.toString().isNotBlank()
    }

    private fun notifyPropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        changeSupport.firePropertyChange(
            propertyName,
            oldValue,
            newValue
        )
    }
}