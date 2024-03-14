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
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBNewStatementBinding
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataErrors
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleBNewStatementFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleBNewStatementBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()
    private var formHelper: FormHelper = FormHelper(fields)

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }

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
            FragmentVehicleBNewStatementBinding.inflate(inflater, container, false)
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

            fragmentNavigationHelper.popBackStackInclusive("vehicle_b_new_statement_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                fragmentNavigationHelper.navigateToFragment(
                    R.id.fragmentContainerView,
                    VehicleBInsuranceFragment(),
                    "vehicle_b_insurance_fragment"
                )
            }
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementPolicyHolderName.setText(statementData.policyHolderBLastName)
            binding.etStatementPolicyHolderFirstName.setText(statementData.policyHolderBFirstName)
            binding.etStatementPolicyHolderAddress.setText(statementData.policyHolderBAddress)
            binding.etStatementPolicyHolderPostalCode.setText(statementData.policyHolderBPostalCode)
            binding.etStatementPolicyHolderPhoneNumber.setText(statementData.policyHolderBPhoneNumber)
            binding.etStatementPolicyHolderEmail.setText(statementData.policyHolderBEmail)
            binding.etStatementVehicleBMarkType.setText(statementData.vehicleBMarkType)
            binding.etStatementVehicleBRegistrationNumber.setText(statementData.vehicleBRegistrationNumber)
            binding.etStatementVehicleBCountry.setText(statementData.vehicleBCountryOfRegistration)
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.policyHolderBLastName = binding.etStatementPolicyHolderName.text.toString()
            this.policyHolderBFirstName =
                binding.etStatementPolicyHolderFirstName.text.toString()
            this.policyHolderBAddress = binding.etStatementPolicyHolderAddress.text.toString()
            this.policyHolderBPostalCode = binding.etStatementPolicyHolderPostalCode.text.toString()
            this.policyHolderBPhoneNumber =
                binding.etStatementPolicyHolderPhoneNumber.text.toString()
            this.policyHolderBEmail = binding.etStatementPolicyHolderEmail.text.toString()
            this.vehicleBMarkType = binding.etStatementVehicleBMarkType.text.toString()
            this.vehicleBRegistrationNumber =
                binding.etStatementVehicleBRegistrationNumber.text.toString()
            this.vehicleBCountryOfRegistration =
                binding.etStatementVehicleBCountry.text.toString()
        }
    }

    override fun setupValidation(
        statementDataErrors: StatementDataErrors,
        fields: List<TextView>,
        validationRules: List<Triple<TextView, (String?) -> Boolean, String>>,
        formHelper: FormHelper
    ) {
        this.fields = listOf(
            binding.etStatementPolicyHolderName,
            binding.etStatementPolicyHolderFirstName,
            binding.etStatementPolicyHolderAddress,
            binding.etStatementPolicyHolderPostalCode,
            binding.etStatementPolicyHolderPhoneNumber,
            binding.etStatementPolicyHolderEmail,
            binding.etStatementVehicleBMarkType,
            binding.etStatementVehicleBRegistrationNumber,
            binding.etStatementVehicleBCountry
        )


        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementPolicyHolderName,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                statementDataErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementPolicyHolderFirstName,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                statementDataErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementPolicyHolderAddress,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderPostalCode,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderPhoneNumber,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBMarkType,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBCountry,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                statementDataErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBRegistrationNumber,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderEmail,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                statementDataErrors.invalidEmail
            )
        )
    }

}