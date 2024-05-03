package com.inetum.realdolmen.crashkit.fragments

import android.os.Bundle
import android.transition.ChangeTransform
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentProfileBinding
import com.inetum.realdolmen.crashkit.dto.InsuranceAgency
import com.inetum.realdolmen.crashkit.dto.InsuranceCertificate
import com.inetum.realdolmen.crashkit.dto.InsuranceCompany
import com.inetum.realdolmen.crashkit.dto.MotorDTO
import com.inetum.realdolmen.crashkit.dto.PersonalInformationData
import com.inetum.realdolmen.crashkit.dto.PolicyHolderPersonalInformationResponse
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.dto.TrailerDTO
import com.inetum.realdolmen.crashkit.dto.Vehicle
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toIsoString
import com.inetum.realdolmen.crashkit.utils.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ProfileFragment : Fragment(), ValidationConfigure {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService

    private val changeSupport = PropertyChangeSupport(this)

    private var personalCardEditing = false
    private var insuranceCardEditing = false
        set(value) {
            val oldValue = field
            field = value

            // Notify listeners about the change
            changeSupport.firePropertyChange(
                "insuranceCardEditing",
                oldValue,
                value
            )
        }

    private var insuranceCertificateAvailabilityDate: LocalDate? = null
        set(newValue) {
            val oldValue = field
            field = newValue

            // Notify listeners about the change
            changeSupport.firePropertyChange(
                "insuranceCertificateAvailabilityDate",
                oldValue,
                newValue
            )
        }

    private var insuranceCertificateExpirationDate: LocalDate? = null
        set(newValue) {
            val oldValue = field
            field = newValue

            // Notify listeners about the change
            changeSupport.firePropertyChange(
                "insuranceCertificateExpirationDate",
                oldValue,
                newValue
            )
        }

    private val insuranceCertificateDateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
        .setTitleText("Select dates")
        .build()

    private var insuranceCertificates: List<InsuranceCertificate>? = null
    private var selectedCertificate: InsuranceCertificate? = null

    private var personalInformationFields: List<TextView> = listOf()
    private var personalInformationValidationRules: List<Triple<EditText, (String?) -> Boolean, String>> =
        listOf()
    private lateinit var personalFormHelper: FormHelper

    private var insuranceInformationFields: List<TextView> = listOf()
    private var insuranceInformationValidationRules: MutableList<Triple<EditText, (String?) -> Boolean, String>> =
        mutableListOf()
    private lateinit var insuranceFormHelper: FormHelper

    private lateinit var fieldPersonalFirstName: TextView
    private lateinit var fieldPersonalLastName: TextView
    private lateinit var fieldPersonalEmail: TextView
    private lateinit var fieldPersonalAddress: TextView
    private lateinit var fieldPersonalPostalCode: TextView
    private lateinit var fieldPersonalPhoneNumber: TextView

    private lateinit var fieldInsuranceCompanyName: TextView
    private lateinit var fieldPolicyNumber: TextView
    private lateinit var fieldGreenCardNumber: TextView
    private lateinit var fieldInsuranceCertAvailabilityDate: TextView
    private lateinit var fieldInsuranceCertExpirationDate: TextView
    private lateinit var fieldInsuranceAgencyName: TextView
    private lateinit var fieldInsuranceAgencyEmail: TextView
    private lateinit var fieldInsuranceAgencyPhoneNumber: TextView
    private lateinit var fieldInsuranceAgencyAddress: TextView
    private lateinit var fieldInsuranceAgencyCountry: TextView
    private lateinit var fieldInsuranceVehicleMarkType: TextView
    private lateinit var fieldInsuranceVehicleLicensePlate: TextView
    private lateinit var fieldInsuranceVehicleCountryOfRegistration: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalFormHelper = FormHelper(requireContext(), personalInformationFields)
        insuranceFormHelper = FormHelper(requireContext(), insuranceInformationFields)

        fetchProfileInformation()

        setupValidation()

        setupPersonalInformationCardFields()
        setupPersonalInformationCardButtonListeners()

        setupInsuranceInformationCardFields()
        setupInsuranceCardButtonListeners()

        setupInsuranceCardCheckboxListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setupValidation(
    ) {
        this.personalInformationFields = listOf(
            binding.etProfilePersonalFirstNameValue,
            binding.etProfilePersonalLastNameValue,
            binding.etProfilePersonalEmailValue,
            binding.etProfilePersonalAddressValue,
            binding.etProfilePersonalPostalCodeValue,
            binding.etProfilePersonalPhoneValue
        )

        this.personalInformationValidationRules =
            listOf<Triple<EditText, (String?) -> Boolean, String>>(
                Triple(
                    binding.etProfilePersonalFirstNameValue,
                    { value -> value.isNullOrEmpty() },
                    personalFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfilePersonalFirstNameValue,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    personalFormHelper.errors.noDigitsAllowed
                ),
                Triple(
                    binding.etProfilePersonalLastNameValue,
                    { value -> value.isNullOrEmpty() },
                    personalFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfilePersonalLastNameValue,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    personalFormHelper.errors.noDigitsAllowed
                ),
                Triple(
                    binding.etProfilePersonalEmailValue,
                    { value -> value.isNullOrEmpty() },
                    personalFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfilePersonalEmailValue,
                    { value ->
                        !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                            value
                        ).matches()
                    },
                    personalFormHelper.errors.invalidEmail
                ),
                Triple(
                    binding.etProfilePersonalAddressValue,
                    { value -> value.isNullOrEmpty() },
                    personalFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfilePersonalPostalCodeValue,
                    { value -> value.isNullOrEmpty() },
                    personalFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfilePersonalPhoneValue,
                    { value -> value.isNullOrEmpty() },
                    personalFormHelper.errors.fieldRequired
                )
            )

        this.insuranceInformationFields = listOf(
            binding.etProfileInsuranceCompanyNameValue,
            binding.etProfileInsuranceCompanyPolicyNumberValue,
            binding.etProfileInsuranceCompanyGreenCardNumberValue,
            binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue,
            binding.etProfileInsuranceCompanyInsuranceExpirationDateValue,
            binding.etProfileInsuranceAgencyNameValue,
            binding.etProfileInsuranceAgencyEmailValue,
            binding.etProfileInsuranceAgencyPhoneNumberValue,
            binding.etProfileInsuranceAgencyAddressValue,
            binding.etProfileInsuranceAgencyCountryValue,
            binding.etProfileInsuranceAgencyVehicleMarkTypeValue,
            binding.etProfileInsuranceVehicleLicensePlateValue,
            binding.etProfileInsuranceVehicleCountryOfRegistrationValue
        )

        this.insuranceInformationValidationRules =
            mutableListOf(
                Triple(
                    binding.etProfileInsuranceCompanyNameValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceCompanyNameValue,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    insuranceFormHelper.errors.noDigitsAllowed
                ),
                Triple(
                    binding.etProfileInsuranceCompanyPolicyNumberValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceCompanyGreenCardNumberValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue, { value ->
                        value?.toLocalDate()?.isAfter(LocalDate.now()) ?: false
                    }, insuranceFormHelper.errors.futureDate
                ),
                Triple(
                    binding.etProfileInsuranceCompanyInsuranceExpirationDateValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceAgencyNameValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceAgencyNameValue,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    insuranceFormHelper.errors.noDigitsAllowed
                ),
                Triple(
                    binding.etProfileInsuranceAgencyEmailValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceAgencyEmailValue,
                    { value ->
                        !value.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                            value
                        ).matches()
                    },
                    insuranceFormHelper.errors.invalidEmail
                ),
                Triple(
                    binding.etProfileInsuranceAgencyPhoneNumberValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceAgencyAddressValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceAgencyCountryValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceAgencyCountryValue,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    insuranceFormHelper.errors.noDigitsAllowed
                ),
                Triple(
                    binding.etProfileInsuranceVehicleLicensePlateValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceVehicleCountryOfRegistrationValue,
                    { value -> value.isNullOrEmpty() },
                    insuranceFormHelper.errors.fieldRequired
                ),
                Triple(
                    binding.etProfileInsuranceVehicleCountryOfRegistrationValue,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    insuranceFormHelper.errors.noDigitsAllowed
                ),
            )
    }

    private fun setupInsuranceCardCheckboxListeners() {
        binding.cbProfileInsuranceAgencyVehicleIsTrailer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.tilProfileInsuranceAgencyVehicleMarkTypeLabel.visibility = View.GONE
                binding.etProfileInsuranceAgencyVehicleMarkTypeValue.setText("")
                // Remove the validation rule unrelated to the trailer
                insuranceInformationValidationRules.removeAll { it.first == binding.etProfileInsuranceAgencyVehicleMarkTypeValue }

            } else {
                binding.tilProfileInsuranceAgencyVehicleMarkTypeLabel.visibility = View.VISIBLE
                // Add the validation rule related to the motor
                insuranceInformationValidationRules.add(
                    Triple(
                        binding.etProfileInsuranceAgencyVehicleMarkTypeValue,
                        { value -> value.isNullOrEmpty() },
                        insuranceFormHelper.errors.fieldRequired
                    )
                )
            }
        }
    }

    private fun setupInsuranceInformationCardFields() {
        fieldInsuranceCompanyName = binding.etProfileInsuranceCompanyNameValue
        fieldPolicyNumber = binding.etProfileInsuranceCompanyPolicyNumberValue
        fieldGreenCardNumber = binding.etProfileInsuranceCompanyGreenCardNumberValue
        fieldInsuranceCertAvailabilityDate =
            binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue
        fieldInsuranceCertExpirationDate =
            binding.etProfileInsuranceCompanyInsuranceExpirationDateValue
        fieldInsuranceAgencyName = binding.etProfileInsuranceAgencyNameValue
        fieldInsuranceAgencyEmail = binding.etProfileInsuranceAgencyEmailValue
        fieldInsuranceAgencyPhoneNumber = binding.etProfileInsuranceAgencyPhoneNumberValue
        fieldInsuranceAgencyAddress = binding.etProfileInsuranceAgencyAddressValue
        fieldInsuranceAgencyCountry = binding.etProfileInsuranceAgencyCountryValue
        fieldInsuranceVehicleMarkType = binding.etProfileInsuranceAgencyVehicleMarkTypeValue
        fieldInsuranceVehicleLicensePlate = binding.etProfileInsuranceVehicleLicensePlateValue
        fieldInsuranceVehicleCountryOfRegistration =
            binding.etProfileInsuranceVehicleCountryOfRegistrationValue
    }

    private fun setupPersonalInformationCardFields() {
        fieldPersonalFirstName = binding.etProfilePersonalFirstNameValue
        fieldPersonalLastName = binding.etProfilePersonalLastNameValue
        fieldPersonalEmail = binding.etProfilePersonalEmailValue
        fieldPersonalAddress = binding.etProfilePersonalAddressValue
        fieldPersonalPostalCode = binding.etProfilePersonalPostalCodeValue
        fieldPersonalPhoneNumber = binding.etProfilePersonalPhoneValue
    }

    private fun addChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    private fun toggleCardFields(
        cardLayout: ConstraintLayout,
        cardFieldLayout: LinearLayout,
        toggleButton: ImageButton,
        expandedImage: Int,
        collapsedImage: Int
    ) {
        if (cardFieldLayout.visibility == View.GONE) {
            cardFieldLayout.visibility = View.VISIBLE
            toggleButton.setImageResource(expandedImage)
        } else {
            cardFieldLayout.visibility = View.GONE
            toggleButton.setImageResource(collapsedImage)
        }

        TransitionManager.beginDelayedTransition(
            cardLayout,
            ChangeTransform()
        )
    }

    private fun updateCard(
        beingEdited: Boolean,
        fields: List<TextView>,
        fieldLayout: LinearLayout,
        editText: TextView,
        updateButton: Button,
        cardExpandButton: ImageButton,
        cardLayout: ConstraintLayout
    ): Boolean {

        var editMode = beingEdited
        editMode = !editMode

        if (editMode) {
            editText.text = getString(R.string.cancel_button)
            setFieldState(fields, true)
            updateButton.visibility = View.VISIBLE

            if (fieldLayout.visibility == View.GONE) {
                fieldLayout.visibility = View.VISIBLE
                cardExpandButton.setImageResource(R.drawable.arrow_drop_up)
            }

            TransitionManager.beginDelayedTransition(
                cardLayout,
                ChangeTransform()
            )
        } else {
            editText.setText(R.string.edit_button)
            setFieldState(fields, false)
            updateButton.visibility = View.GONE
        }
        return editMode
    }

    private fun setFieldState(fields: List<TextView>, isEnabled: Boolean) {
        for (field in fields) {
            if (field == view?.findViewById<TextInputEditText>(R.id.et_profile_insurance_company_insurance_availability_date_value) || field == view?.findViewById<TextInputEditText>(
                    R.id.et_profile_insurance_company_insurance_expiration_date_value
                )
            ) {
                continue
            } else {
                field.isEnabled = isEnabled
            }
        }
    }

    private suspend fun performChangesToInsuranceInformation(
        fieldInsuranceCompanyName: TextView,
        fieldPolicyNumber: TextView,
        fieldGreenCardNumber: TextView,
        fieldInsuranceAgencyName: TextView,
        fieldInsuranceAgencyEmail: TextView,
        fieldInsuranceCertAvailabilityDate: TextView,
        fieldInsuranceCertExpirationDate: TextView,
        fieldInsuranceAgencyPhoneNumber: TextView,
        fieldInsuranceAgencyAddress: TextView,
        fieldInsuranceAgencyCountry: TextView,
        fieldInsuranceVehicleMarkType: TextView,
        fieldInsuranceVehicleLicensePlate: TextView,
        fieldInsuranceVehicleCountryOfRegistration: TextView,
        fieldMaterialDamageCovered: CheckBox,
    ) {
        val insuranceAgency = InsuranceAgency(
            selectedCertificate?.insuranceAgency?.id,
            fieldInsuranceAgencyName.text.toString(),
            fieldInsuranceAgencyAddress.text.toString(),
            fieldInsuranceAgencyCountry.text.toString(),
            fieldInsuranceAgencyPhoneNumber.text.toString(),
            fieldInsuranceAgencyEmail.text.toString()
        )

        val insuranceCompany = InsuranceCompany(
            selectedCertificate?.insuranceCompany?.id,
            fieldInsuranceCompanyName.text.toString()
        )

        val vehicleId = selectedCertificate?.vehicle?.id
        val licensePlate = fieldInsuranceVehicleLicensePlate.text.toString()
        val countryOfRegistration = fieldInsuranceVehicleCountryOfRegistration.text.toString()

        val vehicle: Vehicle?

        if (fieldInsuranceVehicleMarkType.text.isNotEmpty()) {
            val markType = fieldInsuranceVehicleMarkType.text.toString()
            vehicle = MotorDTO(vehicleId, licensePlate, countryOfRegistration, markType = markType)
        } else {
            vehicle = TrailerDTO(vehicleId, licensePlate, countryOfRegistration, true, null)
        }

        //TODO ask for material damage

        val insuranceCertificateData = InsuranceCertificate(
            selectedCertificate?.id,
            fieldPolicyNumber.text.toString(),
            fieldGreenCardNumber.text.toString(),
            fieldInsuranceCertAvailabilityDate.text.toString().toLocalDate()?.toIsoString(),
            fieldInsuranceCertExpirationDate.text.toString().toLocalDate()?.toIsoString(),
            fieldMaterialDamageCovered.isChecked,
            insuranceAgency,
            insuranceCompany,
            vehicle
        )

        Log.i("insurance certificate", insuranceCertificateData.toString())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    apiService.updateInsuranceCertificateInformation(insuranceCertificateData)
                withContext(Dispatchers.Main) {
                    handlePolicyHolderInsuranceInformationResponse(
                        response,
                        insuranceInformationFields
                    )
                }
            } catch (e: Exception) {
                Log.e("NetworkRequest", "Exception occurred: ", e)
                withContext(Dispatchers.Main) {
                    val message = when (e) {
                        is java.net.SocketTimeoutException -> requireContext().getString(
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
        }
    }

    private suspend fun performChangesToPersonalInformation(
        firstNameField: TextView,
        lastNameField: TextView,
        emailField: TextView,
        addressField: TextView,
        postalCodeField: TextView,
        phoneNumberField: TextView

    ) {
        val personalInformationData = PersonalInformationData(
            firstNameField.text.toString(),
            lastNameField.text.toString(),
            emailField.text.toString(),
            addressField.text.toString(),
            postalCodeField.text.toString(),
            phoneNumberField.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.updatePolicyHolderPersonalInformation(personalInformationData)
            withContext(Dispatchers.Main) {
                handlePolicyHolderPersonalInformationResponse(response, personalInformationFields)
            }
        }
    }

    private fun handlePolicyHolderInsuranceInformationResponse(
        response: Response<List<InsuranceCertificate>>,
        fields: List<TextView>
    ) {
        Log.i("Request", "Request code: ${response.code()}")
        if (response.isSuccessful) {
            val responseBody = response.body()

            if (!responseBody.isNullOrEmpty()) {
                insuranceCertificates = responseBody
                showInsuranceDialog()

                Toast.makeText(
                    requireContext(),
                    "Changes successful",
                    Toast.LENGTH_LONG

                )
                    .show()

                insuranceCardEditing = updateCard(
                    insuranceCardEditing,
                    fields,
                    binding.llProfileInsurance,
                    binding.tvProfileInsuranceCardEdit,
                    binding.btnProfileInsuranceCardUpdate,
                    binding.ibProfileInsuranceCardButton,
                    binding.clProfileInsuranceCard
                )
            }
        } else {

            val errorMessage = "Unknown error"
            requireContext().createSimpleDialog(getString(R.string.error), errorMessage)
        }
    }

    private fun handlePolicyHolderPersonalInformationResponse(
        response: Response<PolicyHolderPersonalInformationResponse>,
        fields: List<TextView>
    ) {
        Log.i("Request", "Request code: ${response.code()}")
        if (response.isSuccessful) {
            val personalInformationResponse = response.body()
            if (personalInformationResponse != null) {
                binding.etProfilePersonalFirstNameValue.setText(personalInformationResponse.firstName)
                binding.etProfilePersonalLastNameValue.setText(personalInformationResponse.lastName)
                binding.etProfilePersonalEmailValue.setText(personalInformationResponse.email)
                binding.etProfilePersonalPhoneValue.setText(personalInformationResponse.phoneNumber)
                binding.etProfilePersonalAddressValue.setText(personalInformationResponse.address)
                binding.etProfilePersonalPostalCodeValue.setText(personalInformationResponse.postalCode)

                Toast.makeText(
                    requireContext(),
                    "Changes successful",
                    Toast.LENGTH_LONG

                )
                    .show()

                personalCardEditing = updateCard(
                    personalCardEditing,
                    fields,
                    binding.llProfilePersonal,
                    binding.tvProfilePersonalCardEdit,
                    binding.btnProfilePersonalCardUpdate,
                    binding.ibProfilePersonalCardButton,
                    binding.clProfilePersonalCard
                )
            }
        } else {

            val errorMessage = "Unknown error"
            requireContext().createSimpleDialog(getString(R.string.error), errorMessage)
        }
    }

    private fun handlePolicyHolderProfileResponse(response: Response<PolicyHolderResponse>) {
        Log.i("Request", "Request code: ${response.code()}")
        if (response.isSuccessful) {
            val personalInformationResponse = response.body()
            Log.i("Request", "Request body: ${response.body()}")
            if (personalInformationResponse != null) {
                binding.etProfilePersonalFirstNameValue.setText(personalInformationResponse.firstName)
                binding.etProfilePersonalLastNameValue.setText(personalInformationResponse.lastName)
                binding.etProfilePersonalEmailValue.setText(personalInformationResponse.email)
                binding.etProfilePersonalPhoneValue.setText(personalInformationResponse.phoneNumber)
                binding.etProfilePersonalAddressValue.setText(personalInformationResponse.address)
                binding.etProfilePersonalPostalCodeValue.setText(personalInformationResponse.postalCode)


                if (!personalInformationResponse.insuranceCertificates.isNullOrEmpty()) {
                    insuranceCertificates = personalInformationResponse.insuranceCertificates
                    Log.i("certificates", insuranceCertificates.toString())
                    showInsuranceDialog()
                } else {
                    binding.tvProfileInsuranceCardChangeInsurance.visibility = View.GONE
                }
            } else {

                val errorMessage = "Unknown error"
                requireContext().createSimpleDialog(getString(R.string.error), errorMessage)
            }
        }
    }

    private fun showInsuranceDialog(
    ) {
        if (insuranceCertificates != null) {
            val insuranceCertificateStrings = insuranceCertificates!!.map { certificate ->
                val vehicle = certificate.vehicle
                val vehicleType = when (vehicle) {
                    is MotorDTO -> "Motor"
                    is TrailerDTO -> "Trailer"
                    else -> "Unknown"
                }
                val markType = if (vehicle is MotorDTO) vehicle.markType else "N/A"

                "Vehicle Type: $vehicleType\n" + "Company name: ${certificate.insuranceCompany?.name}\n" +
                        "Agency name: ${certificate.insuranceAgency?.name}\n" +
                        "Policy Number: ${certificate.policyNumber}\n" +
                        "Mark Type: $markType\n" +
                        "License Plate: ${vehicle?.licensePlate}\n" +
                        "Country of Registration: ${vehicle?.countryOfRegistration}\n"
            }.toMutableList()

            insuranceCertificateStrings.add("Create a new certificate")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select an Insurance Certificate")
                .setSingleChoiceItems(
                    insuranceCertificateStrings.toTypedArray(),
                    -1
                ) { dialog, which ->
                    if (which == insuranceCertificateStrings.lastIndex) {
                        selectedCertificate = null

                        binding.cbProfileInsuranceAgencyVehicleIsTrailer.isChecked = false

                        binding.etProfileInsuranceCompanyPolicyNumberValue.setText("")
                        binding.etProfileInsuranceCompanyGreenCardNumberValue.setText("")
                        binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue.setText("")
                        binding.etProfileInsuranceCompanyInsuranceExpirationDateValue.setText("")
                        binding.etProfileInsuranceCompanyNameValue.setText("")
                        binding.etProfileInsuranceAgencyNameValue.setText("")
                        binding.etProfileInsuranceAgencyEmailValue.setText("")
                        binding.etProfileInsuranceAgencyPhoneNumberValue.setText("")
                        binding.etProfileInsuranceAgencyAddressValue.setText("")
                        binding.etProfileInsuranceAgencyCountryValue.setText("")
                        binding.etProfileInsuranceAgencyVehicleMarkTypeValue.setText("")
                        binding.etProfileInsuranceVehicleLicensePlateValue.setText("")
                        binding.etProfileInsuranceVehicleCountryOfRegistrationValue.setText("")
                        binding.cbProfileInsuranceVehicleMaterialDamageCovered.isChecked = false

                    } else {
                        selectedCertificate = insuranceCertificates?.get(which)

                        // Update the UI with the selected certificate
                        binding.etProfileInsuranceCompanyPolicyNumberValue.setText(
                            selectedCertificate?.policyNumber ?: ""
                        )
                        binding.etProfileInsuranceCompanyGreenCardNumberValue.setText(
                            selectedCertificate?.greenCardNumber ?: ""
                        )
                        binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue.setText(
                            selectedCertificate?.availabilityDate?.toLocalDate()?.to24Format() ?: ""
                        )
                        binding.etProfileInsuranceCompanyInsuranceExpirationDateValue.setText(
                            selectedCertificate?.expirationDate?.toLocalDate()?.to24Format() ?: ""
                        )
                        binding.cbProfileInsuranceVehicleMaterialDamageCovered.isChecked =
                            selectedCertificate?.materialDamageCovered ?: false
                        if (selectedCertificate?.insuranceCompany != null) {
                            binding.etProfileInsuranceCompanyNameValue.setText(
                                selectedCertificate?.insuranceCompany?.name ?: ""
                            )
                        }
                        if (selectedCertificate?.insuranceAgency != null) {
                            binding.etProfileInsuranceAgencyNameValue.setText(
                                selectedCertificate?.insuranceAgency?.name ?: ""
                            )
                            binding.etProfileInsuranceAgencyEmailValue.setText(
                                selectedCertificate?.insuranceAgency?.email ?: ""
                            )
                            binding.etProfileInsuranceAgencyPhoneNumberValue.setText(
                                selectedCertificate?.insuranceAgency?.phoneNumber ?: ""
                            )
                            binding.etProfileInsuranceAgencyAddressValue.setText(
                                selectedCertificate?.insuranceAgency?.address ?: ""
                            )
                            binding.etProfileInsuranceAgencyCountryValue.setText(
                                selectedCertificate?.insuranceAgency?.country ?: ""
                            )
                        }
                        if (selectedCertificate?.vehicle != null) {
                            if (selectedCertificate?.vehicle is TrailerDTO) {
                                Log.i("Selected certificate", "Is trailer")
                                binding.etProfileInsuranceAgencyVehicleMarkTypeValue.setText("")
                                binding.cbProfileInsuranceAgencyVehicleIsTrailer.isChecked = true
                            }
                            if (selectedCertificate?.vehicle is MotorDTO) {
                                Log.i("Selected certificate", "Is motor")
                                binding.etProfileInsuranceAgencyVehicleMarkTypeValue.setText(
                                    (selectedCertificate?.vehicle as MotorDTO).markType
                                )
                                binding.cbProfileInsuranceAgencyVehicleIsTrailer.isChecked = false
                            }
                            binding.etProfileInsuranceVehicleLicensePlateValue.setText(
                                selectedCertificate?.vehicle?.licensePlate ?: ""
                            )
                            binding.etProfileInsuranceVehicleCountryOfRegistrationValue.setText(
                                selectedCertificate?.vehicle?.countryOfRegistration ?: ""
                            )
                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupInsuranceCardButtonListeners() {
        binding.ibProfileInsuranceCardButton.setOnClickListener {
            toggleCardFields(
                binding.clProfileInsuranceCard,
                binding.llProfileInsurance,
                binding.ibProfileInsuranceCardButton,
                R.drawable.arrow_drop_up,
                R.drawable.arrow_drop_down
            )
        }

        binding.tvProfileInsuranceCardEdit.setOnClickListener {
            insuranceCardEditing = updateCard(
                insuranceCardEditing,
                insuranceInformationFields,
                binding.llProfileInsurance,
                binding.tvProfileInsuranceCardEdit,
                binding.btnProfileInsuranceCardUpdate,
                binding.ibProfileInsuranceCardButton,
                binding.clProfileInsuranceCard
            )
        }

        binding.btnProfileInsuranceCardUpdate.setOnClickListener {
            validateAndSubmitInsuranceInformation()
        }
    }

    private fun validateAndSubmitInsuranceInformation() {
        insuranceFormHelper.clearErrors()

        insuranceFormHelper.validateFields(insuranceInformationValidationRules)

        if (insuranceInformationFields.none { it.error != null }) {
            lifecycleScope.launch {
                performChangesToInsuranceInformation(
                    fieldInsuranceCompanyName,
                    fieldPolicyNumber,
                    fieldGreenCardNumber,
                    fieldInsuranceAgencyName,
                    fieldInsuranceAgencyEmail,
                    fieldInsuranceCertAvailabilityDate,
                    fieldInsuranceCertExpirationDate,
                    fieldInsuranceAgencyPhoneNumber,
                    fieldInsuranceAgencyAddress,
                    fieldInsuranceAgencyCountry,
                    fieldInsuranceVehicleMarkType,
                    fieldInsuranceVehicleLicensePlate,
                    fieldInsuranceVehicleCountryOfRegistration,
                    binding.cbProfileInsuranceVehicleMaterialDamageCovered
                )
            }
        }
    }

    private fun setupPersonalInformationCardButtonListeners() {
        binding.ibProfilePersonalCardButton.setOnClickListener {

            toggleCardFields(
                binding.clProfilePersonalCard,
                binding.llProfilePersonal,
                binding.ibProfilePersonalCardButton,
                R.drawable.arrow_drop_up,
                R.drawable.arrow_drop_down
            )

        }

        binding.tvProfilePersonalCardEdit.setOnClickListener {
            personalCardEditing = updateCard(
                personalCardEditing,
                personalInformationFields,
                binding.llProfilePersonal,
                binding.tvProfilePersonalCardEdit,
                binding.btnProfilePersonalCardUpdate,
                binding.ibProfilePersonalCardButton,
                binding.clProfilePersonalCard
            )

        }

        binding.tvProfileInsuranceCardChangeInsurance.setOnClickListener {
            showInsuranceDialog()
        }

        binding.btnProfileDateTimePickerInsuranceCertificateDates.setOnClickListener {
            insuranceCertificateDateRangePicker.show(
                parentFragmentManager,
                "insurance_certificate_date_picker"
            )
            insuranceCertificateDateRangePicker.addOnPositiveButtonClickListener { selection ->
                insuranceCertificateAvailabilityDate = Instant.ofEpochMilli(selection.first).atZone(
                    ZoneId.systemDefault()
                ).toLocalDate()
                insuranceCertificateExpirationDate = Instant.ofEpochMilli(selection.second).atZone(
                    ZoneId.systemDefault()
                ).toLocalDate()
            }

            addChangeListener {
                binding.btnProfileDateTimePickerInsuranceCertificateDates.isEnabled =
                    insuranceCardEditing
            }

            addChangeListener {

                binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue.setText(
                    (insuranceCertificateAvailabilityDate?.to24Format() ?: "")
                )
                binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue.error = null
            }

            addChangeListener {
                binding.etProfileInsuranceCompanyInsuranceExpirationDateValue.setText(
                    (insuranceCertificateExpirationDate?.to24Format() ?: "")

                )
                binding.etProfileInsuranceCompanyInsuranceExpirationDateValue.error = null
            }
        }

        binding.btnProfilePersonalCardUpdate.setOnClickListener {
            validateAndSubmitPersonalInformation()
        }
    }

    private fun validateAndSubmitPersonalInformation() {
        personalFormHelper.clearErrors()

        personalFormHelper.validateFields(personalInformationValidationRules)

        if (personalInformationFields.none { it.error != null }) {
            lifecycleScope.launch {
                performChangesToPersonalInformation(
                    fieldPersonalFirstName,
                    fieldPersonalLastName,
                    fieldPersonalEmail,
                    fieldPersonalAddress,
                    fieldPersonalPostalCode,
                    fieldPersonalPhoneNumber
                )
            }
        }
    }

    private fun fetchProfileInformation() {
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                apiService.getPolicyHolderProfileInformation()
            }
            handlePolicyHolderProfileResponse(response)
        }
    }

}