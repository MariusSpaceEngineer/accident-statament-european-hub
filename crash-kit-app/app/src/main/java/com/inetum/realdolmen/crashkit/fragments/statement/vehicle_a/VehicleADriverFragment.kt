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
import com.google.android.material.datepicker.MaterialDatePicker
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleADriverBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.StatementDataErrors
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

class VehicleADriverFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleADriverBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()
    private var formHelper: FormHelper = FormHelper(fields)

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
        _binding = FragmentVehicleADriverBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statementDataErrors = model.statementDataErrors.value!!

        setupValidation(statementDataErrors, fields, validationRules, formHelper)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_a_driver_fragment")

        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                // If no errors, navigate to the next fragment
                fragmentNavigationHelper.navigateToFragment(
                    R.id.fragmentContainerView,
                    VehicleACircumstancesFragment(),
                    "vehicle_a_circumstances_fragment"
                )
            }
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate =
                Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
            when (currentPicker) {
                "driving_license_date_picker" -> {
                    drivingLicenseExpirationDate = selectedDate
                    binding.etStatementVehicleADriverDrivingLicenseExpirationDate.setText(
                        (drivingLicenseExpirationDate?.to24Format() ?: "")
                    )
                    binding.etStatementVehicleADriverDrivingLicenseExpirationDate.error = null
                }

                "date_of_birth_date_picker" -> {
                    driverDateOfBirth = selectedDate
                    binding.etStatementVehicleADriverDateOfBirth.setText(
                        (driverDateOfBirth?.to24Format() ?: "")
                    )
                    binding.etStatementVehicleADriverDateOfBirth.error = null
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
        })
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
            this.vehicleADriverDrivingLicenseNr =
                binding.etStatementVehicleADriverDrivingLicenseNumber.text.toString()
            this.vehicleADriverDrivingLicenseExpirationDate = drivingLicenseExpirationDate
        }
    }

    private fun addDateChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    override fun setupValidation(
        statementDataErrors: StatementDataErrors,
        fields: List<TextView>,
        validationRules: List<Triple<TextView, (String?) -> Boolean, String>>,
        formHelper: FormHelper
    ) {
        this.fields = listOf(
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


        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementVehicleADriverName,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                statementDataErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleADriverFirstName,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                statementDataErrors.noDigitsAllowed
            ),

            Triple(
                binding.etStatementVehicleADriverDateOfBirth,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverDateOfBirth, { value ->
                    value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                }, statementDataErrors.futureDate
            ),
            Triple(
                binding.etStatementVehicleADriverAddress,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverCountry,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                statementDataErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleADriverPhoneNumber,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverEmail,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                statementDataErrors.invalidEmail
            ),
            Triple(
                binding.etStatementVehicleADriverDrivingLicenseNumber,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverDrivingLicenseExpirationDate,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleADriverDrivingLicenseExpirationDate, { value ->
                    value?.toLocalDate()?.isBefore(LocalDate.now()) ?: false
                }, statementDataErrors.pastDate
            ),
        )
    }
}