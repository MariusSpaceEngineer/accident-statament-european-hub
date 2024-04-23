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
    private lateinit var formHelper: FormHelper

    private var _binding: FragmentVehicleANewStatementBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService
    private val securedPreferences = CrashKitApp.securedPreferences

    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> =
        mutableListOf()

    private var hasTrailer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleANewStatementBinding.inflate(inflater, container, false)

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

        setupValidation()

        updateUIFromViewModel(model)

        //Disable button if user is not logged in
        binding.btnStatementVehicleAImportInsuranceInformation.isEnabled =
            !securedPreferences.isGuest()

        setupButtonClickListeners()
        setupCheckboxListeners()
    }

    private fun setupCheckboxListeners() {
        binding.cbStatementTrailerHasRegistration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.llStatementTrailerAFields.visibility = View.VISIBLE
                addTrailerFields()
                addTrailerFieldsForValidation()
            } else {
                binding.llStatementTrailerAFields.visibility = View.GONE
                binding.etStatementTrailerACountryOfRegistration.text = null
                binding.etStatementTrailerALicensePlate.text = null
                removeTrailerFields()
            }
        }

        binding.cbStatementVehicleAMotorAbsent.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.llStatementMotorAFields.visibility = View.GONE
                removeMotorFields()

            } else {
                binding.llStatementMotorAFields.visibility = View.VISIBLE
                addMotorFields()
                addMotorFieldsForValidation()
            }
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner) { statementData ->
            binding.etStatementPolicyHolderName.setText(statementData.policyHolderALastName)
            binding.etStatementPolicyHolderFirstName.setText(statementData.policyHolderAFirstName)
            binding.etStatementPolicyHolderAddress.setText(statementData.policyHolderAAddress)
            binding.etStatementPolicyHolderPostalCode.setText(statementData.policyHolderAPostalCode)
            binding.etStatementPolicyHolderPhoneNumber.setText(statementData.policyHolderAPhoneNumber)
            binding.etStatementPolicyHolderEmail.setText(statementData.policyHolderAEmail)
            binding.etStatementVehicleAMotorMarkType.setText(statementData.vehicleAMotorMarkType)
            binding.etStatementVehicleAMotorLicensePlate.setText(statementData.vehicleAMotorLicensePlate)
            binding.etStatementVehicleAMotorCountryOfRegistration.setText(statementData.vehicleAMotorCountryOfRegistration)
            binding.cbStatementVehicleAMotorAbsent.isChecked = statementData.vehicleAMotorAbsent
            hasTrailer = updateTrailerButtonState(statementData.vehicleATrailerPresent)
            if (statementData.vehicleATrailerLicensePlate.isNotEmpty() && statementData.vehicleATrailerCountryOfRegistration.isNotEmpty()) {
                binding.cbStatementTrailerHasRegistration.isChecked = true
                binding.etStatementTrailerALicensePlate.setText(statementData.vehicleATrailerLicensePlate)
                binding.etStatementTrailerACountryOfRegistration.setText(statementData.vehicleATrailerCountryOfRegistration)
            }
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            //Policy Holder
            this.policyHolderALastName = binding.etStatementPolicyHolderName.text.toString()
            this.policyHolderAFirstName =
                binding.etStatementPolicyHolderFirstName.text.toString()
            this.policyHolderAAddress = binding.etStatementPolicyHolderAddress.text.toString()
            this.policyHolderAPostalCode = binding.etStatementPolicyHolderPostalCode.text.toString()
            this.policyHolderAPhoneNumber =
                binding.etStatementPolicyHolderPhoneNumber.text.toString()
            this.policyHolderAEmail = binding.etStatementPolicyHolderEmail.text.toString()
            //Motor
            this.vehicleAMotorAbsent = binding.cbStatementVehicleAMotorAbsent.isChecked
            this.vehicleAMotorMarkType = binding.etStatementVehicleAMotorMarkType.text.toString()
            this.vehicleAMotorLicensePlate =
                binding.etStatementVehicleAMotorLicensePlate.text.toString()
            this.vehicleAMotorCountryOfRegistration =
                binding.etStatementVehicleAMotorCountryOfRegistration.text.toString()
            //Trailer
            this.vehicleATrailerPresent = !hasTrailer
            this.vehicleATrailerHasRegistration = binding.cbStatementTrailerHasRegistration.isChecked
            this.vehicleATrailerLicensePlate =
                binding.etStatementTrailerALicensePlate.text.toString()
            this.vehicleATrailerCountryOfRegistration =
                binding.etStatementTrailerACountryOfRegistration.text.toString()
        }
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
            binding.etStatementVehicleAMotorMarkType,
            binding.etStatementVehicleAMotorLicensePlate,
            binding.etStatementVehicleAMotorCountryOfRegistration
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
                binding.etStatementVehicleAMotorMarkType,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAMotorCountryOfRegistration,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleAMotorCountryOfRegistration,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleAMotorLicensePlate,
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

    private fun setupButtonClickListeners() {
        binding.btnStatementVehicleAImportInsuranceInformation.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiService.getPolicyHolderProfileInformation()
                withContext(Dispatchers.Main) {
                    handlePolicyHolderProfileResponse(response)
                }
            }
        }

        binding.btnStatementVehicleAAddTrailer.setOnClickListener {
            hasTrailer = updateTrailerButtonState(hasTrailer)
        }

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()
            binding.tvStatementNoMotorNoTrailerError.visibility = View.GONE

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null } && isVehicleAssigned()) {
                updateViewModelFromUI(model)
                navController.navigate(R.id.vehicleADriverFragment)
            } else if (!isVehicleAssigned()) {
                binding.tvStatementNoMotorNoTrailerError.visibility = View.VISIBLE
            }
        }
    }

    private fun isVehicleAssigned(): Boolean {
        //If both trailer and motor are not present it returns false
        return !hasTrailer || !binding.cbStatementVehicleAMotorAbsent.isChecked
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

    private fun updateTrailerButtonState(hasTrailer: Boolean): Boolean {
        val value: Boolean = !hasTrailer

        if (value) {
            binding.btnStatementVehicleAAddTrailer.text =
                requireContext().getString(R.string.add_trailer_button)
            binding.tvStatementVehicleATrailerTitle.visibility = View.GONE
            binding.cbStatementTrailerHasRegistration.visibility = View.GONE
        } else {
            binding.btnStatementVehicleAAddTrailer.text =
                requireContext().getString(R.string.remove_trailer_button)
            binding.cbStatementTrailerHasRegistration.isChecked = false
            binding.tvStatementVehicleATrailerTitle.visibility = View.VISIBLE
            binding.cbStatementTrailerHasRegistration.visibility = View.VISIBLE
        }
        return value
    }

    private fun removeTrailerFields() {
        (validationRules as MutableList<Triple<EditText, (String?) -> Boolean, String>>).removeAll { rule ->
            rule.first == binding.etStatementTrailerALicensePlate || rule.first == binding.etStatementTrailerACountryOfRegistration
        }

        (fields as MutableList<TextView>).removeAll { field ->
            if (field == binding.etStatementTrailerALicensePlate || field == binding.etStatementTrailerACountryOfRegistration) {
                (field as EditText).error = null
                true
            } else {
                false
            }
        }
    }

    private fun addTrailerFields() {
        (fields as MutableList).apply {
            add(binding.etStatementTrailerALicensePlate)
            add(binding.etStatementTrailerACountryOfRegistration)
        }
    }

    private fun addTrailerFieldsForValidation() {
        (validationRules as MutableList).apply {
            add(
                Triple(
                    binding.etStatementTrailerALicensePlate,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementTrailerACountryOfRegistration,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementTrailerACountryOfRegistration,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    formHelper.errors.noDigitsAllowed
                )
            )
        }
    }

    private fun addMotorFields() {
        (fields as MutableList).apply {
            add(binding.etStatementVehicleAMotorMarkType)
            add(binding.etStatementVehicleAMotorLicensePlate)
            add(binding.etStatementVehicleAMotorCountryOfRegistration)
        }
    }

    private fun addMotorFieldsForValidation() {
        (validationRules as MutableList).apply {
            add(
                Triple(
                    binding.etStatementVehicleAMotorMarkType,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementVehicleAMotorCountryOfRegistration,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementVehicleAMotorCountryOfRegistration,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    formHelper.errors.noDigitsAllowed
                )
            )
            add(
                Triple(
                    binding.etStatementVehicleAMotorLicensePlate,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
        }
    }

    private fun removeMotorFields() {
        (validationRules as MutableList<Triple<EditText, (String?) -> Boolean, String>>).removeAll { rule ->
            rule.first == binding.etStatementVehicleAMotorMarkType || rule.first == binding.etStatementVehicleAMotorLicensePlate
                    || rule.first == binding.etStatementVehicleAMotorCountryOfRegistration
        }

        (fields as MutableList<TextView>).removeAll { field ->
            if (field == binding.etStatementVehicleAMotorMarkType || field == binding.etStatementVehicleAMotorLicensePlate ||
                field == binding.etStatementVehicleAMotorCountryOfRegistration
            ) {
                (field as EditText).error = null
                true
            } else {
                false
            }
        }
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
                    val selectedInsurance = insuranceCertificates[which]

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
