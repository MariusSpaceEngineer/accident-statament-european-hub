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
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBDriverBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.helpers.InputFieldsErrors
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.printBackStack
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import java.beans.PropertyChangeSupport
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class VehicleBDriverFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleBDriverBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()
    private var formHelper: FormHelper = FormHelper(fields)
    private val inputFieldsErrors = InputFieldsErrors()

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupValidation()

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_b_driver_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)
            if (fields.none { it.error != null }) {
                fragmentNavigationHelper.navigateToFragment(
                    R.id.fragmentContainerView,
                    VehicleBCircumstancesFragment(),
                    "vehicle_b_circumstances_fragment"
                )
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
            this.vehicleBDriverEmail = binding.etStatementVehicleBDriverEmail.text.toString()
            this.vehicleBDriverDrivingLicenseNr =
                binding.etStatementVehicleBDriverDrivingLicenseNumber.text.toString()
            this.vehicleBDriverDrivingLicenseExpirationDate = drivingLicenseExpirationDate

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
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                this.inputFieldsErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBDriverFirstName,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                this.inputFieldsErrors.noDigitsAllowed
            ),

            Triple(
                binding.etStatementVehicleBDriverDateOfBirth,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverDateOfBirth, { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                }, this.inputFieldsErrors.futureDate
            ),
            Triple(
                binding.etStatementVehicleBDriverAddress,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverCountry,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                this.inputFieldsErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBDriverPhoneNumber,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverEmail,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDriverEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                this.inputFieldsErrors.invalidEmail
            ),
            Triple(
                binding.etStatementVehicleBDriverDrivingLicenseNumber,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDrivingLicenseExpirationDate,
                { value -> value.isNullOrEmpty() },
                this.inputFieldsErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBDrivingLicenseExpirationDate, { value ->
                    value?.toLocalDate()?.isBefore(LocalDate.now()) ?: false
                }, this.inputFieldsErrors.pastDate
            ),
        )
    }
}