package com.inetum.realdolmen.crashkit.fragments

import android.os.Bundle
import android.transition.ChangeTransform
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.inetum.realdolmen.crashkit.dto.PersonalInformationData
import com.inetum.realdolmen.crashkit.dto.PolicyHolderPersonalInformationResponse
import com.inetum.realdolmen.crashkit.dto.PolicyHolderResponse
import com.inetum.realdolmen.crashkit.utils.areFieldsValid
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

class ProfileFragment : Fragment() {

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

        //Fetch profile information
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                apiService.getPolicyHolderProfileInformation()
            }
            handlePolicyHolderProfileResponse(response)
        }

        //Personal Information Card
        val fieldFirstName = binding.etProfilePersonalFirstNameValue
        val fieldLastName = binding.etProfilePersonalLastNameValue
        val fieldEmail = binding.etProfilePersonalEmailValue
        val fieldAddress = binding.etProfilePersonalAddressValue
        val fieldPostalCode = binding.etProfilePersonalPostalCodeValue
        val fieldPhoneNumber = binding.etProfilePersonalPhoneValue
        val personalInformationFields = mapOf(
            binding.etProfilePersonalFirstNameValue to "First Name is required!",
            binding.etProfilePersonalLastNameValue to "Last Name is required!",
            binding.etProfilePersonalEmailValue to "Email is required!",
            binding.etProfilePersonalAddressValue to "Address is required!",
            binding.etProfilePersonalPostalCodeValue to "Postal code is required!",
            binding.etProfilePersonalPhoneValue to "Phone Number is required!"
        )

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

        binding.btnProfilePersonalCardUpdate.setOnClickListener {
            if (personalInformationFields.areFieldsValid()) {
                lifecycleScope.launch {
                    performChangesToPersonalInformation(
                        fieldFirstName,
                        fieldLastName,
                        fieldEmail,
                        fieldAddress,
                        fieldPostalCode,
                        fieldPhoneNumber,
                        personalInformationFields
                    )
                }
            }
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

        //Insurance information card
        val fieldInsuranceCompanyName = binding.etProfileInsuranceCompanyNameValue
        val fieldPolicyNumber = binding.etProfileInsuranceCompanyPolicyNumberValue
        val fieldGreenCardNumber = binding.etProfileInsuranceCompanyGreenCardNumberValue
        val fieldInsuranceCertAvailabilityDate =
            binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue
        val fieldInsuranceCertExpirationDate =
            binding.etProfileInsuranceCompanyInsuranceExpirationDateValue
        val fieldInsuranceAgencyName = binding.etProfileInsuranceAgencyNameValue
        val fieldInsuranceAgencyEmail = binding.etProfileInsuranceAgencyEmailValue
        val fieldInsuranceAgencyPhoneNumber = binding.etProfileInsuranceAgencyPhoneNumberValue
        val fieldInsuranceAgencyAddress = binding.etProfileInsuranceAgencyAddressValue
        val fieldInsuranceAgencyCountry = binding.etProfileInsuranceAgencyCountryValue
        val insuranceInformationFields = mapOf(
            binding.etProfileInsuranceCompanyNameValue to "Company Name is required!",
            binding.etProfileInsuranceCompanyPolicyNumberValue to "Policy Number is required!",
            binding.etProfileInsuranceCompanyGreenCardNumberValue to "Green Card Number is required!",
            binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue to "Date is required!",
            binding.etProfileInsuranceCompanyInsuranceExpirationDateValue to "Date is required!",
            binding.etProfileInsuranceAgencyNameValue to "Agency Name is required!",
            binding.etProfileInsuranceAgencyEmailValue to "Agency Email is required",
            binding.etProfileInsuranceAgencyPhoneNumberValue to "Phone Number is required!",
            binding.etProfileInsuranceAgencyAddressValue to "Agency address is required!",
            binding.etProfileInsuranceAgencyCountryValue to "Agency Country is required!"
        )

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
            if (insuranceInformationFields.areFieldsValid()) {
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
                        insuranceInformationFields
                    )
                }
            }
        }
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
        personalInformationFields: Map<TextInputEditText, String>,
        fieldLayout: LinearLayout,
        editText: TextView,
        updateButton: Button,
        cardExpandButton: ImageButton,
        cardLayout: ConstraintLayout
    ): Boolean {

        var editMode = beingEdited
        editMode = !editMode

        if (editMode) {
            editText.setText("Cancel")
            setFieldState(personalInformationFields, true)
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
            editText.setText(R.string.edit)
            setFieldState(personalInformationFields, false)
            updateButton.visibility = View.GONE
        }
        return editMode
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setFieldState(fields: Map<TextInputEditText, String>, isEnabled: Boolean) {
        for (field in fields) {
            if (field.key == view?.findViewById<TextInputEditText>(R.id.et_profile_insurance_company_insurance_availability_date_value) || field.key == view?.findViewById<TextInputEditText>(
                    R.id.et_profile_insurance_company_insurance_expiration_date_value
                )
            ) {
                continue
            } else {
                field.key.isEnabled = isEnabled
            }
        }
    }

    private suspend fun performChangesToInsuranceInformation(
        fieldInsuranceCompanyName: TextInputEditText,
        fieldPolicyNumber: TextInputEditText,
        fieldGreenCardNumber: TextInputEditText,
        fieldInsuranceAgencyName: TextInputEditText,
        fieldInsuranceAgencyEmail: TextInputEditText,
        fieldInsuranceCertAvailabilityDate: TextInputEditText,
        fieldInsuranceCertExpirationDate: TextInputEditText,
        fieldInsuranceAgencyPhoneNumber: TextInputEditText,
        fieldInsuranceAgencyAddress: TextInputEditText,
        fieldInsuranceAgencyCountry: TextInputEditText,
        fields: Map<TextInputEditText, String>

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

        val insuranceCertificateData = InsuranceCertificate(
            selectedCertificate?.id,
            fieldPolicyNumber.text.toString(),
            fieldGreenCardNumber.text.toString(),
            fieldInsuranceCertAvailabilityDate.text.toString().toLocalDate()?.toIsoString(),
            fieldInsuranceCertExpirationDate.text.toString().toLocalDate()?.toIsoString(),
            insuranceAgency,
            insuranceCompany
        )

        Log.i("insurance certificate", insuranceCertificateData.toString())

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                apiService.updateInsuranceCertificateInformation(insuranceCertificateData)
            withContext(Dispatchers.Main) {
                handlePolicyHolderInsuranceInformationResponse(response, fields)
            }
        }
    }

    private suspend fun performChangesToPersonalInformation(
        firstNameField: TextInputEditText,
        lastNameField: TextInputEditText,
        emailField: TextInputEditText,
        addressField: TextInputEditText,
        postalCodeField: TextInputEditText,
        phoneNumberField: TextInputEditText,
        fields: Map<TextInputEditText, String>

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
                handlePolicyHolderPersonalInformationResponse(response, fields)
            }
        }
    }

    private fun handlePolicyHolderInsuranceInformationResponse(
        response: Response<List<InsuranceCertificate>>,
        fields: Map<TextInputEditText, String>
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
        fields: Map<TextInputEditText, String>
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
            val insuranceCertificateStrings =
                insuranceCertificates!!.map { "Company name: ${it.insuranceCompany?.name}\nAgency name: ${it.insuranceAgency?.name}\nPolicy Number: ${it.policyNumber}" }
                    .toMutableList()
            insuranceCertificateStrings.add("Create a new certificate")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select an Insurance Certificate")
                .setSingleChoiceItems(
                    insuranceCertificateStrings.toTypedArray(),
                    -1
                ) { dialog, which ->
                    if (which == insuranceCertificateStrings.lastIndex) {
                        selectedCertificate= null

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
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }
}