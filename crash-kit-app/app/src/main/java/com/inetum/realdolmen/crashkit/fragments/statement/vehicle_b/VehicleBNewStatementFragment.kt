package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.zxing.integration.android.IntentIntegrator
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBNewStatementBinding
import com.inetum.realdolmen.crashkit.dto.PolicyHolderVehicleBResponse
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import com.journeyapps.barcodescanner.CaptureActivity

class VehicleBNewStatementFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController
    private lateinit var formHelper: FormHelper

    private var _binding: FragmentVehicleBNewStatementBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = mutableListOf()

    private var hasTrailer: Boolean = false

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with QR code scanning
            qrCodeScanLauncher.launch(Intent(requireContext(), CaptureActivity::class.java))
        } else {
            // Permission denied, handle accordingly (e.g., show a message)
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_LONG).show()
        }
    }

    private val qrCodeScanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val scanResult = IntentIntegrator.parseActivityResult(
            result.resultCode,
            result.data
        )
        if (scanResult != null) {
            if (scanResult.contents == null) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Log.i("QR-code", scanResult.contents)
                val json = scanResult.contents
                val gson = Gson()
                try {
                    val policyHolderResponse =
                        gson.fromJson(json, PolicyHolderVehicleBResponse::class.java)

                    Log.i("QR-code", policyHolderResponse.toString())

                    bindPolicyHolderInformationToUI(binding, policyHolderResponse)
                    importInsuranceInformation(model, policyHolderResponse)
                } catch (e: JsonSyntaxException) {
                    Log.e("VehicleBNewStatementFragment", "Failed to parse JSON", e)
                }
            }
        }
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

        setupButtonClickListeners()
        setupCheckboxListeners()
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementPolicyHolderBName.setText(statementData.policyHolderBLastName)
            binding.etStatementPolicyHolderBFirstName.setText(statementData.policyHolderBFirstName)
            binding.etStatementPolicyHolderBAddress.setText(statementData.policyHolderBAddress)
            binding.etStatementPolicyHolderBPostalCode.setText(statementData.policyHolderBPostalCode)
            binding.etStatementPolicyHolderBPhoneNumber.setText(statementData.policyHolderBPhoneNumber)
            binding.etStatementPolicyHolderBEmail.setText(statementData.policyHolderBEmail)
            binding.etStatementVehicleBMarkType.setText(statementData.vehicleBMarkType)
            binding.etStatementVehicleBRegistrationNumber.setText(statementData.vehicleBRegistrationNumber)
            binding.etStatementVehicleBCountry.setText(statementData.vehicleBCountryOfRegistration)
            binding.cbStatementVehicleBMotorPresent.isChecked = statementData.vehicleBMotorPresent
            hasTrailer = updateTrailerButtonState(statementData.vehicleBTrailerPresent)
            if (statementData.vehicleBTrailerRegistrationNumber.isNotEmpty() && statementData.vehicleBTrailerCountryOfRegistration.isNotEmpty()) {
                binding.cbStatementTrailerBNeedsRegistration.isChecked = true
                binding.etStatementTrailerBRegistrationNumber.setText(statementData.vehicleBTrailerRegistrationNumber)
                binding.etStatementTrailerBCountry.setText(statementData.vehicleBTrailerCountryOfRegistration)
            }
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.policyHolderBLastName = binding.etStatementPolicyHolderBName.text.toString()
            this.policyHolderBFirstName =
                binding.etStatementPolicyHolderBFirstName.text.toString()
            this.policyHolderBAddress = binding.etStatementPolicyHolderBAddress.text.toString()
            this.policyHolderBPostalCode = binding.etStatementPolicyHolderBPostalCode.text.toString()
            this.policyHolderBPhoneNumber =
                binding.etStatementPolicyHolderBPhoneNumber.text.toString()
            this.policyHolderBEmail = binding.etStatementPolicyHolderBEmail.text.toString()
            this.vehicleBMarkType = binding.etStatementVehicleBMarkType.text.toString()
            this.vehicleBRegistrationNumber =
                binding.etStatementVehicleBRegistrationNumber.text.toString()
            this.vehicleBCountryOfRegistration =
                binding.etStatementVehicleBCountry.text.toString()
            this.vehicleBTrailerRegistrationNumber =
                binding.etStatementTrailerBRegistrationNumber.text.toString()
            this.vehicleBTrailerCountryOfRegistration =
                binding.etStatementTrailerBCountry.text.toString()
            this.vehicleBTrailerPresent = !hasTrailer
            this.vehicleBMotorPresent = binding.cbStatementVehicleBMotorPresent.isChecked
        }
    }

    override fun setupValidation(
    ) {
        this.fields = mutableListOf(
            binding.etStatementPolicyHolderBName,
            binding.etStatementPolicyHolderBFirstName,
            binding.etStatementPolicyHolderBAddress,
            binding.etStatementPolicyHolderBPostalCode,
            binding.etStatementPolicyHolderBPhoneNumber,
            binding.etStatementPolicyHolderBEmail,
            binding.etStatementVehicleBMarkType,
            binding.etStatementVehicleBRegistrationNumber,
            binding.etStatementVehicleBCountry
        )


        this.validationRules = mutableListOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementPolicyHolderBName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderBName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementPolicyHolderBFirstName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderBFirstName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementPolicyHolderBAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderBPostalCode,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderBPhoneNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBMarkType,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBCountry,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementVehicleBCountry,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementVehicleBRegistrationNumber,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderBEmail,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementPolicyHolderBEmail,
                { value ->
                    !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        value
                    ).matches()
                },
                formHelper.errors.invalidEmail
            )
        )
    }

    private fun setupCheckboxListeners() {
        binding.cbStatementTrailerBNeedsRegistration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.llStatementTrailerBFields.visibility = View.VISIBLE
                addTrailerFields()
                addTrailerFieldsForValidation()
            } else {
                binding.llStatementTrailerBFields.visibility = View.GONE
                binding.etStatementTrailerBCountry.text = null
                binding.etStatementTrailerBRegistrationNumber.text = null
                removeTrailerFields()
            }
        }

        binding.cbStatementVehicleBMotorPresent.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.llStatementMotorBFields.visibility = View.GONE
                removeMotorFields()

            } else {
                binding.llStatementMotorBFields.visibility = View.VISIBLE
                addMotorFields()
                addMotorFieldsForValidation()
            }
        }
    }

    private fun addTrailerFields() {
        (fields as MutableList).apply {
            add(binding.etStatementTrailerBRegistrationNumber)
            add(binding.etStatementTrailerBCountry)
        }
    }

    private fun addTrailerFieldsForValidation() {
        (validationRules as MutableList).apply {
            add(
                Triple(
                    binding.etStatementTrailerBRegistrationNumber,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementTrailerBCountry,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementTrailerBCountry,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    formHelper.errors.noDigitsAllowed
                )
            )
        }
    }

    private fun removeTrailerFields() {
        (validationRules as MutableList<Triple<EditText, (String?) -> Boolean, String>>).removeAll { rule ->
            rule.first == binding.etStatementTrailerBRegistrationNumber || rule.first == binding.etStatementTrailerBCountry
        }

        (fields as MutableList<TextView>).removeAll { field ->
            if (field == binding.etStatementTrailerBRegistrationNumber || field == binding.etStatementTrailerBCountry) {
                (field as EditText).error = null
                true
            } else {
                false
            }
        }
    }

    private fun removeMotorFields() {
        (validationRules as MutableList<Triple<EditText, (String?) -> Boolean, String>>).removeAll { rule ->
            rule.first == binding.etStatementVehicleBMarkType || rule.first == binding.etStatementVehicleBRegistrationNumber
                    || rule.first == binding.etStatementVehicleBCountry
        }

        (fields as MutableList<TextView>).removeAll { field ->
            if (field == binding.etStatementVehicleBMarkType || field == binding.etStatementVehicleBRegistrationNumber ||
                field == binding.etStatementVehicleBCountry
            ) {
                (field as EditText).error = null
                true
            } else {
                false
            }
        }
    }

    private fun addMotorFields() {
        (fields as MutableList).apply {
            add(binding.etStatementVehicleBMarkType)
            add(binding.etStatementVehicleBRegistrationNumber)
            add(binding.etStatementVehicleBCountry)
        }
    }

    private fun addMotorFieldsForValidation() {
        (validationRules as MutableList).apply {
            add(
                Triple(
                    binding.etStatementVehicleBMarkType,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementVehicleBCountry,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementVehicleBCountry,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    formHelper.errors.noDigitsAllowed
                )
            )
            add(
                Triple(
                    binding.etStatementVehicleBRegistrationNumber,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
        }
    }

    private fun setupButtonClickListeners() {
        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementVehicleBAddTrailer.setOnClickListener {
            hasTrailer = updateTrailerButtonState(hasTrailer)
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()
            binding.tvStatementNoMotorNoTrailerError.visibility = View.GONE

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null } && isVehicleAssigned()) {
                updateViewModelFromUI(model)

                navController.navigate(R.id.vehicleBInsuranceFragment)
            } else if (!isVehicleAssigned()) {
                binding.tvStatementNoMotorNoTrailerError.visibility = View.VISIBLE
            }
        }

        binding.btnStatementVehicleBImportInsuranceInformation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted, proceed with QR code scanning
                qrCodeScanLauncher.launch(Intent(requireContext(), CaptureActivity::class.java))
            } else {
                // Request camera permission
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun updateTrailerButtonState(hasTrailer: Boolean): Boolean {
        val value: Boolean = !hasTrailer

        if (value) {
            binding.btnStatementVehicleBAddTrailer.text =
                requireContext().getString(R.string.add_trailer_button)
            binding.tvStatementVehicleBTrailerTitle.visibility = View.GONE
            binding.cbStatementTrailerBNeedsRegistration.visibility = View.GONE
        } else {
            binding.btnStatementVehicleBAddTrailer.text =
                requireContext().getString(R.string.remove_trailer_button)
            binding.cbStatementTrailerBNeedsRegistration.isChecked = false
            binding.tvStatementVehicleBTrailerTitle.visibility = View.VISIBLE
            binding.cbStatementTrailerBNeedsRegistration.visibility = View.VISIBLE
        }
        return value
    }

    private fun isVehicleAssigned(): Boolean {
        //If both trailer and motor are not present it returns false
        return !hasTrailer || !binding.cbStatementVehicleBMotorPresent.isChecked
    }

    private fun bindPolicyHolderInformationToUI(
        binding: FragmentVehicleBNewStatementBinding,
        response: PolicyHolderVehicleBResponse
    ) {
        binding.etStatementPolicyHolderBName.setText(response.lastName)
        binding.etStatementPolicyHolderBName.error = null
        binding.etStatementPolicyHolderBFirstName.setText(response.firstName)
        binding.etStatementPolicyHolderBFirstName.error = null
        binding.etStatementPolicyHolderBAddress.setText(response.address)
        binding.etStatementPolicyHolderBAddress.error = null
        binding.etStatementPolicyHolderBPostalCode.setText(response.postalCode)
        binding.etStatementPolicyHolderBPostalCode.error = null
        binding.etStatementPolicyHolderBPhoneNumber.setText(response.phoneNumber)
        binding.etStatementPolicyHolderBPhoneNumber.error = null
        binding.etStatementPolicyHolderBEmail.setText(response.email)
        binding.etStatementPolicyHolderBEmail.error = null
    }

    private fun importInsuranceInformation(
        model: NewStatementViewModel,
        response: PolicyHolderVehicleBResponse
    ) {
        model.statementData.value?.apply {
            this.vehicleBInsuranceCompanyName =
                response.insuranceCertificate?.insuranceCompany?.name ?: ""
            this.vehicleBInsuranceCompanyPolicyNumber =
                response.insuranceCertificate?.policyNumber ?: ""
            this.vehicleBInsuranceCompanyGreenCardNumber =
                response.insuranceCertificate?.greenCardNumber ?: ""
            this.vehicleBInsuranceCertificateAvailabilityDate =
                response.insuranceCertificate?.availabilityDate?.toLocalDate()
            this.vehicleBInsuranceCertificateExpirationDate =
                response.insuranceCertificate?.expirationDate?.toLocalDate()
            this.vehicleBInsuranceAgencyName =
                response.insuranceCertificate?.insuranceAgency?.name ?: ""
            this.vehicleBInsuranceAgencyAddress =
                response.insuranceCertificate?.insuranceAgency?.address ?: ""
            this.vehicleBInsuranceAgencyCountry =
                response.insuranceCertificate?.insuranceAgency?.country ?: ""
            this.vehicleBInsuranceAgencyPhoneNumber =
                response.insuranceCertificate?.insuranceAgency?.phoneNumber ?: ""
            this.vehicleBInsuranceAgencyEmail =
                response.insuranceCertificate?.insuranceAgency?.email ?: ""
        }
    }
}