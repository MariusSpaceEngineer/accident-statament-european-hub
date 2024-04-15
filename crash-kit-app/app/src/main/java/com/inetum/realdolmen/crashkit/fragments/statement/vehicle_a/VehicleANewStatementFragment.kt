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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleANewStatementBinding
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class VehicleANewStatementFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentVehicleANewStatementBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService
    private val securedPreferences = CrashKitApp.securedPreferences

    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> =
        mutableListOf()
    private lateinit var formHelper: FormHelper

    private var hasTrailer = false

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

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::navController.isInitialized) {
            // Save the NavController's state
            outState.putBundle("nav_state", navController.saveState())
        }
        super.onSaveInstanceState(outState)
    }

    private fun handlePolicyHolderProfileResponse(response: Response<PolicyHolderResponse>) {
        Log.i("Request", "Request code: ${response.code()}")
        if (response.isSuccessful) {
            val personalInformationResponse = response.body()
            if (personalInformationResponse != null) {
                importInsuranceInformation(personalInformationResponse.insuranceCertificates)
                bindPolicyHolderInformationToUI(binding, personalInformationResponse)
            }
        } else {

            val errorMessage = "Error while fetching insurance information"
            requireContext().createSimpleDialog(getString(R.string.error), errorMessage)
        }
    }

    private fun updateTrailerButton(hasTrailer: Boolean): Boolean {
        var value = hasTrailer
        value = !hasTrailer

        if (!value) {
            binding.btnStatementVehicleAAddTrailer.text =
                requireContext().getString(R.string.add_trailer_button)
            binding.tvStatementVehicleATrailerTitle.visibility = View.GONE
            binding.cbStatementTrailerNeedsRegistration.visibility = View.GONE
        } else {
            binding.btnStatementVehicleAAddTrailer.text = requireContext().getString(R.string.remove_trailer_button)
            binding.cbStatementTrailerNeedsRegistration.isChecked = false
            binding.tvStatementVehicleATrailerTitle.visibility = View.VISIBLE
            binding.cbStatementTrailerNeedsRegistration.visibility = View.VISIBLE
        }
        return value
    }

    private fun removeTrailerFieldsFromValidation() {
        // Remove trailer fields from validationRules
        (validationRules as MutableList<Triple<EditText, (String?) -> Boolean, String>>).removeAll { rule ->
            rule.first == binding.etStatementTrailerARegistrationNumber || rule.first == binding.etStatementTrailerACountry
        }

        // Remove trailer fields from fields
        (fields as MutableList<TextView>).removeAll { field ->
            if (field == binding.etStatementTrailerARegistrationNumber || field == binding.etStatementTrailerACountry) {
                (field as EditText).error = null
                true
            } else {
                false
            }
        }
    }


    private fun setTrailerFieldsToValidation() {
        (fields as MutableList).apply {
            add(binding.etStatementTrailerARegistrationNumber)
            add(binding.etStatementTrailerACountry)
        }

        (validationRules as MutableList).apply {
            add(
                Triple(
                    binding.etStatementTrailerARegistrationNumber,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementTrailerACountry,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementTrailerACountry,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    formHelper.errors.noDigitsAllowed
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        formHelper = FormHelper(requireContext(), fields)

        setupValidation()

        updateUIFromViewModel(model)

        //Disable button if user is not logged in
        binding.btnStatementVehicleAImportInsuranceInformation.isEnabled =
            !securedPreferences.isGuest()

        binding.btnStatementVehicleAImportInsuranceInformation.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiService.getPolicyHolderProfileInformation()
                withContext(Dispatchers.Main) {
                    handlePolicyHolderProfileResponse(response)
                }
            }
        }

        binding.btnStatementVehicleAAddTrailer.setOnClickListener {
            hasTrailer = updateTrailerButton(hasTrailer)
        }

        binding.cbStatementTrailerNeedsRegistration.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.llStatementTrailerAFields.visibility = View.VISIBLE
                setTrailerFieldsToValidation()
            } else {
                binding.llStatementTrailerAFields.visibility = View.GONE
                removeTrailerFieldsFromValidation()
                binding.etStatementTrailerACountry.text = null
                binding.etStatementTrailerARegistrationNumber.text = null
            }
        }

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            Log.i("fields to validate", validationRules.toString())

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                updateViewModelFromUI(model)
                navController.navigate(R.id.vehicleAInsuranceFragment)
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
            if (statementData.vehicleATrailerRegistrationNumber.isNotEmpty() && statementData.vehicleATrailerCountryOfRegistration.isNotEmpty()) {
                hasTrailer = false
                hasTrailer = updateTrailerButton(hasTrailer)
                Log.i("hasTrailer", hasTrailer.toString())
                binding.cbStatementTrailerNeedsRegistration.isChecked = true
                binding.etStatementTrailerARegistrationNumber.setText(statementData.vehicleATrailerRegistrationNumber)
                binding.etStatementTrailerACountry.setText(statementData.vehicleATrailerCountryOfRegistration)
            }
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
            this.vehicleATrailerRegistrationNumber =
                binding.etStatementTrailerARegistrationNumber.text.toString()
            this.vehicleATrailerCountryOfRegistration =
                binding.etStatementTrailerACountry.text.toString()
        }
        Log.i("model", model.statementData.value?.vehicleATrailerRegistrationNumber ?: "empty ")
        Log.i("model", model.statementData.value?.vehicleATrailerCountryOfRegistration ?: "empty ")

    }

    override fun setupValidation(
    ) {
        this.fields = mutableListOf(
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


        this.validationRules = mutableListOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementPolicyHolderName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementPolicyHolderFirstName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementPolicyHolderAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderPostalCode,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAMarkType,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleACountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleACountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleARegistrationNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
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
        insuranceCertificates: List<InsuranceCertificate>?
    ) {
        if (insuranceCertificates != null) {
            val insuranceCertificateStrings =
                insuranceCertificates.map { "Company name: ${it.insuranceCompany?.name}\nAgency name: ${it.insuranceAgency?.name}\nPolicy Number: ${it.policyNumber}" }
                    .toTypedArray()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select an Insurance Certificate")
                .setSingleChoiceItems(
                    insuranceCertificateStrings,
                    -1
                ) { dialog, which ->
                    val selectedInsurance = insuranceCertificates.get(which)

                    model.statementData.value?.apply {
                        this.vehicleAInsuranceCompanyName =
                            selectedInsurance.insuranceCompany?.name ?: ""
                        this.vehicleAInsuranceCompanyPolicyNumber =
                            selectedInsurance.policyNumber.toString()
                        this.vehicleAInsuranceCompanyGreenCardNumber =
                            selectedInsurance.greenCardNumber.toString()
                        this.vehicleAInsuranceCertificateAvailabilityDate =
                            selectedInsurance.availabilityDate?.toLocalDate()
                        this.vehicleAInsuranceCertificateExpirationDate =
                            selectedInsurance.expirationDate?.toLocalDate()
                        this.vehicleAInsuranceAgencyName =
                            selectedInsurance.insuranceAgency?.name.toString()
                        this.vehicleAInsuranceAgencyAddress =
                            selectedInsurance.insuranceAgency?.address.toString()
                        this.vehicleAInsuranceAgencyCountry =
                            selectedInsurance.insuranceAgency?.country.toString()
                        this.vehicleAInsuranceAgencyPhoneNumber =
                            selectedInsurance.insuranceAgency?.phoneNumber.toString()
                        this.vehicleAInsuranceAgencyEmail =
                            selectedInsurance.insuranceAgency?.email.toString()
                    }

                    dialog.dismiss()

                    Toast.makeText(
                        requireContext(),
                        "Import successful",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                .show()

        }
    }
}
