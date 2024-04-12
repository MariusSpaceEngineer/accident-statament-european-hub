package com.inetum.realdolmen.crashkit.fragments.statement

import android.Manifest
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
import androidx.lifecycle.Observer
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
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.ValidationConfigure
import com.inetum.realdolmen.crashkit.utils.createSimpleDialog
import com.inetum.realdolmen.crashkit.utils.to24Format
import com.inetum.realdolmen.crashkit.utils.toLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.time.LocalDateTime

class NewStatementFragment : Fragment(), StatementDataHandler, ValidationConfigure {
    private lateinit var navController: NavController
    private lateinit var model: NewStatementViewModel
    private lateinit var formHelper: FormHelper
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var _binding: FragmentNewStatementBinding? = null
    private val binding get() = _binding!!

    private var fields: List<TextView> = listOf()
    private var validationRules: List<Triple<EditText, (String?) -> Boolean, String>> = listOf()

    private val dateTimePicker by lazy {
        DateTimePicker(requireContext())
    }

    private val apiService = CrashKitApp.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        navController = findNavController()

        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }
        // Inflate the layout for this fragment
        _binding = FragmentNewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                .show()
        }
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
        binding.btnStatementAccidentPrevious.isEnabled = false

        formHelper = FormHelper(requireContext(), fields)

        setupValidation()

        updateUIFromViewModel(model)

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.btnStatementAccidentNext.setOnClickListener {

            formHelper.clearErrors()

            updateViewModelFromUI(model)

            formHelper.validateFields(validationRules)

            if (fields.none { it.error != null }) {
                //If no errors, navigate to the next fragment
                navController.navigate(R.id.vehicleANewStatementFragment)
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

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementAccidentDate.setText(
                (statementData.dateOfAccident?.to24Format() ?: "")
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
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.dateOfAccident = dateTimePicker.dateTime
            this.accidentLocation = binding.etStatementAccidentLocation.text.toString()
            this.injured = binding.cbStatementAccidentInjured.isChecked
            this.materialDamageToOtherVehicles =
                binding.cbStatementAccidentMaterialDamageOtherVehicles.isChecked
            this.materialDamageToObjects =
                binding.cbStatementAccidentMaterialDamageOtherObjects.isChecked
            this.witnessName = binding.etStatementWitnessName.text.toString()
            this.witnessAddress = binding.etStatementWitnessAddress.text.toString()
            this.witnessPhoneNumber = binding.etStatementWitnessPhone.text.toString()
        }
    }

    override fun setupValidation(
    ) {
        this.fields = listOf(
            binding.etStatementAccidentDate,
            binding.etStatementAccidentLocation,
            binding.cbStatementAccidentMaterialDamageOtherVehicles,
            binding.cbStatementAccidentMaterialDamageOtherObjects,
            binding.etStatementWitnessName,
            binding.etStatementWitnessAddress,
            binding.etStatementWitnessPhone
        )

        this.validationRules = listOf<Triple<EditText, (String?) -> Boolean, String>>(
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

    private fun getCurrentLocation() {
        //Any other priority will update the location less frequent
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()
        try {
            fusedLocationProviderClient.getCurrentLocation(priority, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    Log.d("Location", "location is found: $location")
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
                .addOnFailureListener { exception ->
                    Log.d("Location", "Coordinates fetch failed with exception: $exception")
                }
        } catch (securityException: SecurityException) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.location_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleAccidentLocationResponse(response: Response<RequestResponse>) {
        Log.i("Request", "Request code: ${response.code()}")
        val addressResponse = response.body()
        if (addressResponse != null) {
            if (response.isSuccessful) {
                binding.etStatementAccidentLocation.setText(addressResponse.successMessage)
            } else {
                requireContext().createSimpleDialog(
                    getString(R.string.error),
                    addressResponse.errorMessage!!
                )
            }
        }
    }

}