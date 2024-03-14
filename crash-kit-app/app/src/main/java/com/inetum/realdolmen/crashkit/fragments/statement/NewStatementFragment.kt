package com.inetum.realdolmen.crashkit.fragments.statement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentNewStatementBinding
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleANewStatementFragment
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.DateTimePicker
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataErrors
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.printBackStack
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDateTime
import java.time.LocalDateTime

class NewStatementFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentNewStatementBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()
    private var formHelper: FormHelper = FormHelper(fields)

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }

    private val dateTimePicker by lazy {
        DateTimePicker(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statementDataErrors = model.statementDataErrors.value!!

        setupValidation(statementDataErrors, fields, validationRules, formHelper)

        updateUIFromViewModel(model)

        requireActivity().supportFragmentManager.printBackStack()

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            fields.forEach { field ->
                if (field.error != null) {
                    Log.e("FieldError", "Error in field: ${field}, error: ${field.error}")
                }
            }
            if (fields.none { it.error != null }) {
                // If no errors, navigate to the next fragment
                fragmentNavigationHelper.navigateToFragment(
                    R.id.fragmentContainerView,
                    VehicleANewStatementFragment(),
                    "vehicle_a_new_statement_fragment"
                )
            }
        }

        binding.btnDateTimePicker.setOnClickListener {
            dateTimePicker.pickDateTime()
        }

        dateTimePicker.addDateChangeListener {
            binding.etStatementAccidentDate.setText(
                (dateTimePicker.dateTime?.to24Format() ?: "")
            )
            binding.etStatementAccidentDate.error= null
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementAccidentDate.setText(
                (statementData.dateOfAccident?.to24Format() ?: "")
            )
            binding.etStatementAccidentLocation.setText(statementData.accidentLocation)
            binding.cbStatementAccidentInjured.isChecked = statementData.injured
            binding.cbStatementAccidentMaterialDamageOtherVehicles.isChecked =
                statementData.materialDamageToOtherVehicles
            binding.cbStatementAccidentMaterialDamageOtherObjects.isChecked =
                statementData.materialDamageToObjects
            binding.etStatementWitnessName.setText(statementData.witnessName)
            binding.etStatementWitnessAddress.setText(statementData.witnessAddress)
            binding.etStatementWitnessPhone.setText(statementData.witnessPhoneNumber)
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.dateOfAccident = dateTimePicker.dateTime
            this.accidentLocation = binding.etStatementAccidentLocation.text.toString()
            this.injured = binding.cbStatementAccidentInjured.isChecked
            this.materialDamageToOtherVehicles =
                binding.cbStatementAccidentMaterialDamageOtherVehicles.isChecked
            this.materialDamageToObjects =
                binding.cbStatementAccidentMaterialDamageOtherObjects.isChecked
            this.witnessName = binding.etStatementWitnessName.text.toString()
            this.witnessAddress = binding.etStatementWitnessAddress.text.toString()
            this.witnessPhoneNumber = binding.etStatementWitnessPhone.text.toString()
        }
    }

    override fun setupValidation(
        errors: StatementDataErrors,
        fields: List<TextView>,
        validationRules: List<Triple<TextView, (String?) -> Boolean, String>>,
        formHelper: FormHelper
    ) {
        this.fields = listOf(
            binding.etStatementAccidentDate,
            binding.etStatementAccidentLocation,
            binding.cbStatementAccidentMaterialDamageOtherVehicles,
            binding.cbStatementAccidentMaterialDamageOtherObjects,
            binding.etStatementWitnessName,
            binding.etStatementWitnessAddress,
            binding.etStatementWitnessPhone
        )

        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementAccidentDate,
                { value -> value.isNullOrEmpty() },
                errors.fieldRequired
            ),
            Triple(
                binding.etStatementAccidentDate, { value ->
                    value?.toLocalDateTime()?.isAfter(LocalDateTime.now()) ?: false
                }, errors.futureDate
            ),
            Triple(
                binding.etStatementAccidentLocation,
                { value -> value.isNullOrEmpty() },
                errors.fieldRequired
            ),
            Triple(
                binding.etStatementWitnessName,
                { value -> value.isNullOrEmpty() },
                errors.fieldRequired
            ),
            Triple(
                binding.etStatementWitnessName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementWitnessAddress,
                { value -> value.isNullOrEmpty() },
                errors.fieldRequired
            ),
            Triple(
                binding.etStatementWitnessPhone,
                { value -> value.isNullOrEmpty() },
                errors.fieldRequired
            ),
        )
    }

}