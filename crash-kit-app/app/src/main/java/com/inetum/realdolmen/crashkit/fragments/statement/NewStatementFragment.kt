package com.inetum.realdolmen.crashkit.fragments.statement

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentNewStatementBinding
import com.inetum.realdolmen.crashkit.dto.LocationCoordinatesData
import com.inetum.realdolmen.crashkit.dto.RequestResponse
import com.inetum.realdolmen.crashkit.helpers.FormHelper
import com.inetum.realdolmen.crashkit.utils.DateTimePicker
import com.inetum.realdolmen.crashkit.utils.IValidationConfigure
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_LOCATION
import com.inetum.realdolmen.crashkit.utils.LogTags.TAG_NETWORK_REQUEST
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.showToast
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException
import java.time.LocalDateTime

class NewStatementFragment : Fragment(), StatementDataHandler, IValidationConfigure {
    private lateinit var navController: NavController
    private lateinit var model: NewStatementViewModel
    private lateinit var formHelper: FormHelper
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var _binding: FragmentNewStatementBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = mutableListOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> =
        mutableListOf()

    private val dateTimePicker by lazy {
        DateTimePicker(requireContext())
    }

    private val apiService = CrashKitApp.apiService

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            requireContext().showToast(getString(R.string.location_permission_denied))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewStatementBinding.inflate(inflater, container, false)
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

        setupWitnessCheckboxListener()
        setupButtonClickListeners()
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner) { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementAccidentDate.setText(
                (statementData.dateOfAccident?.to24Format() ?: LocalDateTime.now().to24Format())
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
            //The checkbox needs to be unchecked to show witness fields
            binding.cbStatementWitnessPresent.isChecked = !statementData.witnessIsPresent
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.dateOfAccident = dateTimePicker.dateTime ?: LocalDateTime.now()
            this.accidentLocation = binding.etStatementAccidentLocation.text.toString()
            this.injured = binding.cbStatementAccidentInjured.isChecked
            this.materialDamageToOtherVehicles =
                binding.cbStatementAccidentMaterialDamageOtherVehicles.isChecked
            this.materialDamageToObjects =
                binding.cbStatementAccidentMaterialDamageOtherObjects.isChecked
            this.witnessName = binding.etStatementWitnessName.text.toString()
            this.witnessAddress = binding.etStatementWitnessAddress.text.toString()
            this.witnessPhoneNumber = binding.etStatementWitnessPhone.text.toString()
            //If the checkbox is checked then no witness is present
            this.witnessIsPresent = !binding.cbStatementWitnessPresent.isChecked
        }
    }

    override fun setupValidation(
    ) {
        this.fields = mutableListOf(
            binding.etStatementAccidentDate,
            binding.etStatementAccidentLocation,
            binding.cbStatementAccidentMaterialDamageOtherVehicles,
            binding.cbStatementAccidentMaterialDamageOtherObjects,
            binding.etStatementWitnessName,
            binding.etStatementWitnessAddress,
            binding.etStatementWitnessPhone
        )

        this.validationRules = mutableListOf<Triple<EditText, (String?) -> Boolean, String>>(
            Triple(
                binding.etStatementAccidentDate,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementAccidentDate, { value ->
                    value?.toLocalDateTime()?.isAfter(LocalDateTime.now()) ?: false
                }, formHelper.errors.futureDate
            ),
            Triple(
                binding.etStatementAccidentLocation,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementWitnessName,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementWitnessName,
                { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                formHelper.errors.noDigitsAllowed
            ),
            Triple(
                binding.etStatementWitnessAddress,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
            Triple(
                binding.etStatementWitnessPhone,
                { value -> value.isNullOrEmpty() },
                formHelper.errors.fieldRequired
            ),
        )
    }

    private fun setupWitnessCheckboxListener() {
        binding.cbStatementWitnessPresent.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                removeWitnessFields()
            } else {
                addWitnessFields()
                addWitnessFieldsForValidation()
            }
        }
    }

    private fun setupButtonClickListeners() {
        binding.btnStatementAccidentNext.setOnClickListener {
            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                //If no errors, navigate to the next fragment
                navController.navigate(R.id.accidentStatementOverviewFragment)
            }
        }

        binding.btnDateTimePicker.setOnClickListener {
            dateTimePicker.pickDateTime()
        }

        dateTimePicker.addDateChangeListener {
            binding.etStatementAccidentDate.setText(
                (dateTimePicker.dateTime?.to24Format() ?: "")
            )
            binding.etStatementAccidentDate.error = null
        }

        binding.btnStatementAccidentLocation.setOnClickListener {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    /**
     * This method retrieves the current location of the device.
     * It uses the FusedLocationProviderClient to get the current location with a high accuracy priority.
     * High accuracy is used to get the best updates possible about the location.
     *
     * If the location is successfully retrieved, it fetches the address for that location.
     * If the location retrieval fails, it handles the failure.
     * If there's a security exception (usually due to missing permissions), it handles the security exception.
     *
     * @throws SecurityException if location permissions are not granted.
     */
    private fun getCurrentLocation() {
        // High accuracy priority is used to get the best updates possible about the location
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()
        try {
            fusedLocationProviderClient.getCurrentLocation(priority, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    Log.d(TAG_LOCATION, "Location is found: $location")
                    fetchLocationAddress(location)
                }
                .addOnFailureListener { exception ->
                    handleLocationFailure(exception)
                }
        } catch (securityException: SecurityException) {
            handleSecurityException()
        }
    }

    private fun fetchLocationAddress(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getLocationAddress(
                    LocationCoordinatesData(
                        location.latitude,
                        location.longitude
                    )
                )
                withContext(Dispatchers.Main) {
                    handleAccidentLocationResponse(response)
                }
            } catch (e: Exception) {
                handleErrorResponse(e)
            }
        }
    }

    private fun handleLocationFailure(exception: Exception) {
        Log.e(TAG_LOCATION, "Coordinates fetch failed with exception: $exception")
    }

    private fun handleSecurityException() {
        Toast.makeText(
            requireContext(),
            requireContext().getString(R.string.location_permission_denied),
            Toast.LENGTH_LONG
        ).show()
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

    private fun handleAccidentLocationResponse(response: Response<RequestResponse>) {
        Log.d(TAG_NETWORK_REQUEST, "Request code: ${response.code()}")
        val addressResponse = response.body()
        if (addressResponse != null) {
            if (response.isSuccessful) {
                handleSuccessfulLocationAddressResponse(addressResponse)
            } else {
                requireContext().createSimpleDialog(
                    getString(R.string.error),
                    addressResponse.errorMessage!!
                )
            }
        }
    }

    private fun handleSuccessfulLocationAddressResponse(addressResponse: RequestResponse) {
        binding.etStatementAccidentLocation.setText(addressResponse.successMessage)
        requireContext().showToast(getString(R.string.location_address_fetch_successful))
    }

    private fun addWitnessFieldsForValidation() {
        (validationRules as MutableList).apply {
            add(
                Triple(
                    binding.etStatementWitnessName,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementWitnessName,
                    { value -> !value.isNullOrEmpty() && value.any { it.isDigit() } },
                    formHelper.errors.noDigitsAllowed
                )
            )
            add(
                Triple(
                    binding.etStatementWitnessAddress,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
            add(
                Triple(
                    binding.etStatementWitnessPhone,
                    { value -> value.isNullOrEmpty() },
                    formHelper.errors.fieldRequired
                )
            )
        }
    }

    private fun addWitnessFields() {
        binding.llStatementWitnessFields.visibility = View.VISIBLE

        (fields as MutableList).apply {
            add(binding.etStatementWitnessName)
            add(binding.etStatementWitnessAddress)
            add(binding.etStatementWitnessPhone)
        }
    }

    private fun removeWitnessFields() {
        clearWitnessFields()

        (validationRules as MutableList<Triple<EditText, (String?) -> Boolean, String>>).removeAll { rule ->
            rule.first == binding.etStatementWitnessName || rule.first == binding.etStatementWitnessAddress
                    || rule.first == binding.etStatementWitnessPhone
        }

        (fields as MutableList<TextView>).removeAll { field ->
            if (field == binding.etStatementWitnessName || field == binding.etStatementWitnessAddress ||
                field == binding.etStatementWitnessPhone
            ) {
                (field as EditText).error = null
                true
            } else {
                false
            }
        }
    }

    private fun clearWitnessFields() {
        binding.llStatementWitnessFields.visibility = View.GONE
        binding.etStatementWitnessName.text = null
        binding.etStatementWitnessAddress.text = null
        binding.etStatementWitnessPhone.text = null
    }
}