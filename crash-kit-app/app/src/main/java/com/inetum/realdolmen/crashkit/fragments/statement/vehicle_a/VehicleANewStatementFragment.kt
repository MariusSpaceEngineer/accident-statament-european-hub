package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleANewStatementBinding
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.MotorDTO
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.dto.TrailerDTO
import com.inetum.realdolmen.crashkit.dto.Vehicle
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.IValidationConfigure
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_NETWORK_REQUEST
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.showToast
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException

class VehicleANewStatementFragment : Fragment(), StatementDataHandler, IValidationConfigure {
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

    private var insuranceCertificates: List<InsuranceCertificate>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleANewStatementBinding.inflate(inflater, container, false)
        // Lock the screen orientation to portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

        checkUserState()

        setupButtonClickListeners()
        setupCheckboxListeners()
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner) { statementData ->
            //Policy Holder
            binding.etStatementPolicyHolderName.setText(statementData.policyHolderALastName)
            binding.etStatementPolicyHolderFirstName.setText(statementData.policyHolderAFirstName)
            binding.etStatementPolicyHolderAddress.setText(statementData.policyHolderAAddress)
            binding.etStatementPolicyHolderPostalCode.setText(statementData.policyHolderAPostalCode)
            binding.etStatementPolicyHolderPhoneNumber.setText(statementData.policyHolderAPhoneNumber)
            binding.etStatementPolicyHolderEmail.setText(statementData.policyHolderAEmail)
            //Motor
            binding.etStatementVehicleAMotorMarkType.setText(statementData.vehicleAMotorMarkType)
            binding.etStatementVehicleAMotorLicensePlate.setText(statementData.vehicleAMotorLicensePlate)
            binding.etStatementVehicleAMotorCountryOfRegistration.setText(statementData.vehicleAMotorCountryOfRegistration)
            binding.cbStatementVehicleAMotorAbsent.isChecked = statementData.vehicleAMotorAbsent
            //Trailer
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
            this.vehicleATrailerHasRegistration =
                binding.cbStatementTrailerHasRegistration.isChecked
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

    private fun checkUserState() {
        //Disable buttons if the user is not logged in
        binding.btnStatementVehicleAImportInsuranceInformation.isEnabled =
            !securedPreferences.isGuest()
        binding.btnStatementVehicleAImportMotorInsuranceInformation.isEnabled =
            !securedPreferences.isGuest()
        binding.btnStatementVehicleAImportTrailerInsuranceInformation.isEnabled =
            !securedPreferences.isGuest()
    }

    private fun setupCheckboxListeners() {
        binding.cbStatementTrailerHasRegistration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addTrailerFields()
                addTrailerFieldsForValidation()
            } else {
                removeTrailerFields()
            }
        }

        binding.cbStatementVehicleAMotorAbsent.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                removeMotorFields()

            } else {
                addMotorFields()
                addMotorFieldsForValidation()
            }
        }
    }

    private fun setupButtonClickListeners() {
        binding.btnStatementVehicleAImportInsuranceInformation.setOnClickListener {
            getPolicyHolderInsurances()
        }
        binding.btnStatementVehicleAImportMotorInsuranceInformation.setOnClickListener {
            importVehicleInformation(insuranceCertificates, MotorDTO::class.java)
        }
        binding.btnStatementVehicleAAddTrailer.setOnClickListener {
            hasTrailer = updateTrailerButtonState(hasTrailer)
        }
        binding.btnStatementVehicleAImportTrailerInsuranceInformation.setOnClickListener { handleTrailerInsuranceClick() }
        binding.btnStatementAccidentPrevious.setOnClickListener { handlePreviousClick() }
        binding.btnStatementAccidentNext.setOnClickListener { handleNextClick() }
    }

    private fun handleTrailerInsuranceClick() {
        importVehicleInformation(insuranceCertificates, TrailerDTO::class.java)
        removeTrailerFieldsErrors()
    }

    private fun handlePreviousClick() {
        updateViewModelFromUI(model)
        navController.popBackStack()
    }

    private fun handleNextClick() {
        formHelper.clearErrors()
        binding.tvStatementNoMotorNoTrailerError.visibility = View.GONE
        formHelper.validateFields(validationRules)

        if (fields.none { it.error != null } && isVehicleAssigned()) {
            updateViewModelFromUI(model)
            navigateToNextFragment()
        } else if (!isVehicleAssigned()) {
            binding.tvStatementNoMotorNoTrailerError.visibility = View.VISIBLE
        }
    }

    private fun navigateToNextFragment() {
        if (binding.cbStatementVehicleAMotorAbsent.isChecked) {
            if (binding.cbStatementTrailerHasRegistration.isChecked) {
                navController.navigate(R.id.vehicleATrailerInsuranceFragment)
            } else {
                navController.navigate(R.id.vehicleADriverFragment)
            }
        } else {
            navController.navigate(R.id.vehicleAMotorInsuranceFragment)
        }
    }

    private fun getPolicyHolderInsurances() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getPolicyHolderProfileInformation()
                withContext(Dispatchers.Main) {
                    handlePolicyHolderProfileResponse(response)
                }
            } catch (e: Exception) {
                handleErrorResponse(e)
            }
        }
    }

    private suspend fun handleErrorResponse(e: Exception) {
        Log.e(TAG_NETWORK_REQUEST, "Exception occurred: ", e)
        withContext(Dispatchers.Main) {
            val message = when (e) {
                is SocketTimeoutException -> requireContext().getString(
                    R.string.error_network
                )

                else -> requireContext().getString(R.string.unknown_error)
            }
            requireContext().createSimpleDialog(
                getString(R.string.error),
                message
            )
        }
    }

    private fun isVehicleAssigned(): Boolean {
        //If both trailer and motor are not present it returns false
        return !hasTrailer || !binding.cbStatementVehicleAMotorAbsent.isChecked
    }

    private fun handlePolicyHolderProfileResponse(response: Response<PolicyHolderResponse>) {
        Log.d(TAG_NETWORK_REQUEST, "Request code: ${response.code()}")
        if (response.isSuccessful) {
            handleSuccessfulPolicyHolderProfileResponse(response)
        } else {
            handleFailedPolicyHolderProfileResponse()
        }
    }

    private fun handleFailedPolicyHolderProfileResponse() {
        requireContext().createSimpleDialog(
            getString(R.string.error),
            getString(R.string.policy_holder_profile_fetch_failed)
        )
    }

    private fun handleSuccessfulPolicyHolderProfileResponse(response: Response<PolicyHolderResponse>) {
        val personalInformationResponse = response.body()
        if (personalInformationResponse != null) {
            requireContext().showToast(getString(R.string.import_successful))
            insuranceCertificates = personalInformationResponse.insuranceCertificates
            bindPolicyHolderInformationToUI(binding, personalInformationResponse)
        }
    }

    private fun updateTrailerButtonState(hasTrailer: Boolean): Boolean {
        val value: Boolean = !hasTrailer
        //If user has no trailer
        if (value) {
            binding.btnStatementVehicleAAddTrailer.text =
                requireContext().getString(R.string.add_trailer_button)
            binding.llStatementTrailerAFields.visibility = View.GONE
            binding.tvStatementVehicleATrailerTitle.visibility = View.GONE
            binding.cbStatementTrailerHasRegistration.visibility = View.GONE
        }
        //If user has trailer
        else {
            binding.btnStatementVehicleAAddTrailer.text =
                requireContext().getString(R.string.remove_trailer_button)
            binding.cbStatementTrailerHasRegistration.isChecked = false
            binding.tvStatementVehicleATrailerTitle.visibility = View.VISIBLE
            binding.cbStatementTrailerHasRegistration.visibility = View.VISIBLE
        }
        return value
    }

    private fun removeTrailerFields() {
        clearTrailerFields()
        (validationRules as MutableList<Triple<EditText, (String?) -> Boolean, String>>).removeAll { rule ->
            rule.first == binding.etStatementTrailerALicensePlate || rule.first == binding.etStatementTrailerACountryOfRegistration
        }

        removeTrailerFieldsErrors()
    }

    private fun clearTrailerFields() {
        binding.llStatementTrailerAFields.visibility = View.GONE
        binding.etStatementTrailerACountryOfRegistration.text = null
        binding.etStatementTrailerALicensePlate.text = null
    }

    private fun removeTrailerFieldsErrors() {
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
        binding.llStatementTrailerAFields.visibility = View.VISIBLE
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
        binding.llStatementMotorAFields.visibility = View.VISIBLE
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
        binding.llStatementMotorAFields.visibility = View.GONE

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

    /**
     * This function imports vehicle information based on the provided insurance certificates and vehicle type.
     *
     * @param insuranceCertificates A list of insurance certificates. Each certificate contains information about a vehicle.
     * @param vehicleType The class of the vehicle type to be imported (e.g., MotorDTO::class.java, TrailerDTO::class.java).
     *
     * If the list of insurance certificates is not null, the function filters the vehicles based on the provided vehicle type,
     * converts the filtered vehicles to string descriptions, and then shows a dialog for the user to select an insurance certificate.
     *
     * If the list of insurance certificates is null, the function shows a toast message indicating that no vehicles were found.
     */
    private fun importVehicleInformation(
        insuranceCertificates: List<InsuranceCertificate>?,
        vehicleType: Class<out Vehicle>
    ) {
        if (insuranceCertificates != null) {
            val vehicles = filterVehicles(insuranceCertificates, vehicleType)
            val insuranceCertificateStrings = convertVehiclesToStringDescription(vehicles)
            showInsuranceCertificateDialog(vehicles, insuranceCertificateStrings)
        } else {
            requireContext().showToast(getString(R.string.no_vehicles_found))
        }
    }

    private fun filterVehicles(
        insuranceCertificates: List<InsuranceCertificate>,
        vehicleType: Class<out Vehicle>
    ): List<InsuranceCertificate> {
        return insuranceCertificates.filter { vehicleType.isInstance(it.vehicle) }
    }

    private fun convertVehiclesToStringDescription(vehicles: List<InsuranceCertificate>): Array<String> {
        return vehicles.map {
            val vehicleInfo =
                "License Plate: ${it.vehicle?.licensePlate}\nCountry of Registration: ${it.vehicle?.countryOfRegistration}"
            if (it.vehicle is MotorDTO) {
                "$vehicleInfo\nMark Type: ${it.vehicle.markType}"
            } else {
                vehicleInfo
            }
        }.toTypedArray()
    }

    private fun showInsuranceCertificateDialog(
        vehicles: List<InsuranceCertificate>,
        insuranceCertificateStrings: Array<String>
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.insurance_certificate_select_dialog))
            .setSingleChoiceItems(insuranceCertificateStrings, -1) { dialog, which ->
                handleInsuranceSelection(vehicles[which])
                dialog.dismiss()
            }.show()
    }

    private fun handleInsuranceSelection(selectedInsurance: InsuranceCertificate) {
        when (val selectedVehicle = selectedInsurance.vehicle) {
            is MotorDTO -> {
                bindMotorInformationToUI(selectedVehicle)
                bindMotorInsuranceInformationToViewModel(selectedInsurance)
            }

            is TrailerDTO -> {
                bindTrailerInformationToUI(selectedVehicle)
                bindTrailerInsuranceInformationToViewModel(selectedInsurance)
            }

            null -> return
        }
    }

    private fun bindTrailerInsuranceInformationToViewModel(selectedInsurance: InsuranceCertificate) {
        model.statementData.value?.apply {
            this.vehicleATrailerInsuranceCompanyName =
                selectedInsurance.insuranceCompany?.name ?: ""
            this.vehicleATrailerInsuranceCompanyPolicyNumber =
                selectedInsurance.policyNumber.toString()
            this.vehicleATrailerInsuranceCompanyGreenCardNumber =
                selectedInsurance.greenCardNumber.toString()
            this.vehicleATrailerInsuranceCertificateAvailabilityDate =
                selectedInsurance.availabilityDate?.toLocalDate()
            this.vehicleATrailerInsuranceCertificateExpirationDate =
                selectedInsurance.expirationDate?.toLocalDate()
            this.vehicleATrailerInsuranceAgencyName =
                selectedInsurance.insuranceAgency?.name.toString()
            this.vehicleATrailerInsuranceAgencyAddress =
                selectedInsurance.insuranceAgency?.address.toString()
            this.vehicleATrailerInsuranceAgencyCountry =
                selectedInsurance.insuranceAgency?.country.toString()
            this.vehicleATrailerInsuranceAgencyPhoneNumber =
                selectedInsurance.insuranceAgency?.phoneNumber.toString()
            this.vehicleATrailerInsuranceAgencyEmail =
                selectedInsurance.insuranceAgency?.email.toString()
            this.vehicleATrailerMaterialDamageCovered =
                selectedInsurance.materialDamageCovered ?: false

        }
    }

    private fun bindMotorInsuranceInformationToViewModel(selectedInsurance: InsuranceCertificate) {
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
            this.vehicleAMotorMaterialDamageCovered =
                selectedInsurance.materialDamageCovered ?: false
        }
    }

    private fun bindTrailerInformationToUI(selectedVehicle: TrailerDTO) {
        removeTrailerFieldsErrors()
        binding.cbStatementTrailerHasRegistration.isChecked =
            selectedVehicle.hasRegistration
        binding.etStatementTrailerALicensePlate.setText(selectedVehicle.licensePlate)
        binding.etStatementTrailerACountryOfRegistration.setText(selectedVehicle.countryOfRegistration)
    }

    private fun bindMotorInformationToUI(selectedVehicle: MotorDTO) {
        removeMotorFieldsErrors()
        binding.etStatementVehicleAMotorMarkType.setText(selectedVehicle.markType)
        binding.etStatementVehicleAMotorLicensePlate.setText(selectedVehicle.licensePlate)
        binding.etStatementVehicleAMotorCountryOfRegistration.setText(
            selectedVehicle.countryOfRegistration
        )
    }

    private fun removeMotorFieldsErrors() {
        binding.etStatementVehicleAMotorMarkType.error = null
        binding.etStatementVehicleAMotorLicensePlate.error = null
        binding.etStatementVehicleAMotorCountryOfRegistration.error = null
    }

}

