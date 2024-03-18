package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleANewStatementBinding
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataErrors
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.printBackStack
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class VehicleANewStatementFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleANewStatementBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService
    private val securedPreferences = CrashKitApp.securedPreferences

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
        _binding = FragmentVehicleANewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private fun handlePolicyHolderProfileResponse(response: Response<PolicyHolderResponse>) {
        Log.i("Request", "Request code: ${response.code()}")
        if (response.isSuccessful) {
            val personalInformationResponse = response.body()
            if (personalInformationResponse != null) {
                bindPolicyHolderInformationToUI(binding, personalInformationResponse)
                importInsuranceInformation(model, personalInformationResponse)
            }
            Toast.makeText(
                requireContext(),
                "Import successful",
                Toast.LENGTH_LONG
            )
                .show()
        } else {

            val errorMessage = "Error while fetching insurance information"
            requireContext().createSimpleDialog(getString(R.string.error), errorMessage)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statementDataErrors = model.statementDataErrors.value!!

        setupValidation(statementDataErrors, fields, validationRules, formHelper)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        //Disable button if user is not logged in
        if (!securedPreferences.isGuest()) {
            binding.btnStatementVehicleAImportInsuranceInformation.isEnabled = true
        }

        binding.btnStatementVehicleAImportInsuranceInformation.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiService.getPolicyHolderProfileInformation()
                withContext(Dispatchers.Main) {
                    handlePolicyHolderProfileResponse(response)
                }
            }
        }

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            findNavController().apply {
                navigate(R.id.newStatementFragment)
            }
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                fragmentNavigationHelper.navigateToFragment(
                    R.id.fragmentContainerView,
                    VehicleAInsuranceFragment(),
                    "vehicle_a_insurance_fragment"
                )
            }
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementPolicyHolderName.setText(statementData.policyHolderALastName)
            binding.etStatementPolicyHolderFirstName.setText(statementData.policyHolderAFirstName)
            binding.etStatementPolicyHolderAddress.setText(statementData.policyHolderAAddress)
            binding.etStatementPolicyHolderPostalCode.setText(statementData.policyHolderAPostalCode)
            binding.etStatementPolicyHolderPhoneNumber.setText(statementData.policyHolderAPhoneNumber)
            binding.etStatementPolicyHolderEmail.setText(statementData.policyHolderAEmail)
            binding.etStatementVehicleAMarkType.setText(statementData.vehicleAMarkType)
            binding.etStatementVehicleARegistrationNumber.setText(statementData.vehicleARegistrationNumber)
            binding.etStatementVehicleACountry.setText(statementData.vehicleACountryOfRegistration)
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.policyHolderALastName = binding.etStatementPolicyHolderName.text.toString()
            this.policyHolderAFirstName =
                binding.etStatementPolicyHolderFirstName.text.toString()
            this.policyHolderAAddress = binding.etStatementPolicyHolderAddress.text.toString()
            this.policyHolderAPostalCode = binding.etStatementPolicyHolderPostalCode.text.toString()
            this.policyHolderAPhoneNumber =
                binding.etStatementPolicyHolderPhoneNumber.text.toString()
            this.policyHolderAEmail = binding.etStatementPolicyHolderEmail.text.toString()
            this.vehicleAMarkType = binding.etStatementVehicleAMarkType.text.toString()
            this.vehicleARegistrationNumber =
                binding.etStatementVehicleARegistrationNumber.text.toString()
            this.vehicleACountryOfRegistration =
                binding.etStatementVehicleACountry.text.toString()
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
            binding.etStatementVehicleAMarkType,
            binding.etStatementVehicleARegistrationNumber,
            binding.etStatementVehicleACountry
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
                binding.etStatementVehicleAMarkType,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleACountry,
                { value -> value.isNullOrEmpty() },
                statementDataErrors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleACountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                statementDataErrors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleARegistrationNumber,
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

    private fun bindPolicyHolderInformationToUI(
        binding: FragmentVehicleANewStatementBinding,
        response: PolicyHolderResponse
    ) {
        binding.etStatementPolicyHolderName.setText(response.lastName)
        binding.etStatementPolicyHolderFirstName.setText(response.firstName)
        binding.etStatementPolicyHolderAddress.setText(response.address)
        binding.etStatementPolicyHolderPostalCode.setText(response.postalCode)
        binding.etStatementPolicyHolderPhoneNumber.setText(response.phoneNumber)
        binding.etStatementPolicyHolderEmail.setText(response.email)
    }

    private fun importInsuranceInformation(
        model: NewStatementViewModel,
        response: PolicyHolderResponse
    ) {
        model.statementData.value?.apply {
            this.vehicleAInsuranceCompanyName =
                response.insuranceCertificate?.insuranceCompany?.name.toString()
            this.vehicleAInsuranceCompanyPolicyNumber =
                response.insuranceCertificate?.policyNumber.toString()
            this.vehicleAInsuranceCompanyGreenCardNumber =
                response.insuranceCertificate?.greenCardNumber.toString()
            this.vehicleAInsuranceCertificateAvailabilityDate =
                response.insuranceCertificate?.availabilityDate?.toLocalDate()
            this.vehicleAInsuranceCertificateExpirationDate =
                response.insuranceCertificate?.expirationDate?.toLocalDate()
            this.vehicleAInsuranceAgencyName =
                response.insuranceCertificate?.insuranceAgency?.name.toString()
            this.vehicleAInsuranceAgencyAddress =
                response.insuranceCertificate?.insuranceAgency?.address.toString()
            this.vehicleAInsuranceAgencyCountry =
                response.insuranceCertificate?.insuranceAgency?.country.toString()
            this.vehicleAInsuranceAgencyPhoneNumber =
                response.insuranceCertificate?.insuranceAgency?.phoneNumber.toString()
            this.vehicleAInsuranceAgencyEmail =
                response.insuranceCertificate?.insuranceAgency?.email.toString()
        }
    }
}
