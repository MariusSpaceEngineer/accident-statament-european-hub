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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val apiService = CrashKitApp.apiService

    private var personalCardEditing = false
    private var insuranceCardEditing = false

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
                        fieldInsuranceCertAvailabilityDate,
                        fieldInsuranceCertExpirationDate,
                        fieldInsuranceAgencyName,
                        fieldInsuranceAgencyEmail,
                        fieldInsuranceAgencyPhoneNumber,
                        fieldInsuranceAgencyAddress,
                        fieldInsuranceAgencyCountry, insuranceInformationFields
                    )
                }
            }
        }
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
            field.key.isEnabled = isEnabled
        }
    }

    private suspend fun performChangesToInsuranceInformation(
        fieldInsuranceCompanyName: TextInputEditText,
        fieldPolicyNumber: TextInputEditText,
        fieldGreenCardNumber: TextInputEditText,
        fieldInsuranceCertAvailabilityDate: TextInputEditText,
        fieldInsuranceCertExpirationDate: TextInputEditText,
        fieldInsuranceAgencyName: TextInputEditText,
        fieldInsuranceAgencyEmail: TextInputEditText,
        fieldInsuranceAgencyPhoneNumber: TextInputEditText,
        fieldInsuranceAgencyAddress: TextInputEditText,
        fieldInsuranceAgencyCountry: TextInputEditText,
        fields: Map<TextInputEditText, String>

    ) {
        val insuranceAgency = InsuranceAgency(
            null,
            fieldInsuranceAgencyName.text.toString(),
            fieldInsuranceAgencyAddress.text.toString(),
            fieldInsuranceAgencyCountry.text.toString(),
            fieldInsuranceAgencyPhoneNumber.text.toString(),
            fieldInsuranceAgencyEmail.text.toString()
        )

        val insuranceCompany = InsuranceCompany(null, fieldInsuranceCompanyName.text.toString())

        val insuranceCertificateData = InsuranceCertificate(
            null,
            fieldPolicyNumber.text.toString(),
            fieldGreenCardNumber.text.toString(),
            fieldInsuranceCertAvailabilityDate.text.toString(),
            fieldInsuranceCertExpirationDate.text.toString(),
            insuranceAgency,
            insuranceCompany
        )

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
        phoneNumberField: TextInputEditText, fields: Map<TextInputEditText, String>

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
        response: Response<InsuranceCertificate>,
        fields: Map<TextInputEditText, String>
    ) {
        Log.i("Request", "Request code: ${response.code()}")
        if (response.isSuccessful) {
            val insuranceCertificate = response.body()
            if (insuranceCertificate != null) {
                binding.etProfileInsuranceCompanyPolicyNumberValue.setText(
                    insuranceCertificate.policyNumber
                )
                binding.etProfileInsuranceCompanyGreenCardNumberValue.setText(
                    insuranceCertificate.greenCardNumber
                )
                binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue.setText(
                    insuranceCertificate.availabilityDate
                )
                binding.etProfileInsuranceCompanyInsuranceExpirationDateValue.setText(
                    insuranceCertificate.expirationDate
                )

                if (insuranceCertificate.insuranceCompany != null) {
                    binding.etProfileInsuranceCompanyNameValue.setText(
                        insuranceCertificate.insuranceCompany.name
                    )
                }

                if (insuranceCertificate.insuranceAgency != null) {
                    binding.etProfileInsuranceAgencyNameValue.setText(
                        insuranceCertificate.insuranceAgency.name
                    )
                    binding.etProfileInsuranceAgencyEmailValue.setText(
                        insuranceCertificate.insuranceAgency.email
                    )
                    binding.etProfileInsuranceAgencyPhoneNumberValue.setText(
                        insuranceCertificate.insuranceAgency.phoneNumber
                    )
                    binding.etProfileInsuranceAgencyAddressValue.setText(
                        insuranceCertificate.insuranceAgency.address
                    )
                    binding.etProfileInsuranceAgencyCountryValue.setText(
                        insuranceCertificate.insuranceAgency.country
                    )
                }

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
            if (personalInformationResponse != null) {
                binding.etProfilePersonalFirstNameValue.setText(personalInformationResponse.firstName)
                binding.etProfilePersonalLastNameValue.setText(personalInformationResponse.lastName)
                binding.etProfilePersonalEmailValue.setText(personalInformationResponse.email)
                binding.etProfilePersonalPhoneValue.setText(personalInformationResponse.phoneNumber)
                binding.etProfilePersonalAddressValue.setText(personalInformationResponse.address)
                binding.etProfilePersonalPostalCodeValue.setText(personalInformationResponse.postalCode)

                if (personalInformationResponse.insuranceCertificate != null) {
                    binding.etProfileInsuranceCompanyPolicyNumberValue.setText(
                        personalInformationResponse.insuranceCertificate.policyNumber
                    )
                    binding.etProfileInsuranceCompanyGreenCardNumberValue.setText(
                        personalInformationResponse.insuranceCertificate.greenCardNumber
                    )
                    binding.etProfileInsuranceCompanyInsuranceAvailabilityDateValue.setText(
                        personalInformationResponse.insuranceCertificate.availabilityDate
                    )
                    binding.etProfileInsuranceCompanyInsuranceExpirationDateValue.setText(
                        personalInformationResponse.insuranceCertificate.expirationDate
                    )

                    if (personalInformationResponse.insuranceCertificate.insuranceCompany != null) {
                        binding.etProfileInsuranceCompanyNameValue.setText(
                            personalInformationResponse.insuranceCertificate.insuranceCompany.name
                        )
                    }

                    if (personalInformationResponse.insuranceCertificate.insuranceAgency != null) {
                        binding.etProfileInsuranceAgencyNameValue.setText(
                            personalInformationResponse.insuranceCertificate.insuranceAgency.name
                        )
                        binding.etProfileInsuranceAgencyEmailValue.setText(
                            personalInformationResponse.insuranceCertificate.insuranceAgency.email
                        )
                        binding.etProfileInsuranceAgencyPhoneNumberValue.setText(
                            personalInformationResponse.insuranceCertificate.insuranceAgency.phoneNumber
                        )
                        binding.etProfileInsuranceAgencyAddressValue.setText(
                            personalInformationResponse.insuranceCertificate.insuranceAgency.address
                        )
                        binding.etProfileInsuranceAgencyCountryValue.setText(
                            personalInformationResponse.insuranceCertificate.insuranceAgency.country
                        )
                    }

                }
                Toast.makeText(
                    requireContext(),
                    "Registration successful",
                    Toast.LENGTH_LONG
                )
                    .show()

            }
        } else {

            val errorMessage = "Unknown error"
            requireContext().createSimpleDialog(getString(R.string.error), errorMessage)
        }
    }

}