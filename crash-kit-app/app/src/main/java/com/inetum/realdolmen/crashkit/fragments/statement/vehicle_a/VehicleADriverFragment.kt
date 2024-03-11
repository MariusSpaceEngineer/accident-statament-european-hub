package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleADriverBinding
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleADriverFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleADriverBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentVehicleADriverBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_a_driver_fragment")

        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleACircumstancesFragment(),
                "vehicle_a_circumstances_fragment"
            )
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementVehicleADriverName.setText(statementData.vehicleADriverLastName)
            binding.etStatementVehicleADriverFirstName.setText(statementData.vehicleADriverFirstName)
            binding.etStatementVehicleADriverDateOfBirth.setText(statementData.vehicleADriverDateOfBirth)
            binding.etStatementVehicleADriverAddress.setText(statementData.vehicleADriverAddress)
            binding.etStatementVehicleADriverCountry.setText(statementData.vehicleADriverCountry)
            binding.etStatementVehicleAInsuranceAgencyPhoneNumber.setText(statementData.vehicleADriverPhoneNumber)
            binding.etStatementVehicleADriverEmail.setText(statementData.vehicleADriverEmail)
            binding.etStatementVehicleADriverDrivingLicenseNumber.setText(statementData.vehicleADriverDrivingLicenseNr)
            binding.etStatementVehicleADriverDrivingLicenseExpirationDate.setText(statementData.vehicleADriverDrivingLicenseExpirationDate)
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleADriverLastName = binding.etStatementVehicleADriverName.text.toString()
            this.vehicleADriverFirstName =
                binding.etStatementVehicleADriverFirstName.text.toString()
            this.vehicleADriverDateOfBirth =
                binding.etStatementVehicleADriverDateOfBirth.text.toString()
            this.vehicleADriverAddress =
                binding.etStatementVehicleADriverAddress.text.toString()
            this.vehicleADriverCountry =
                binding.etStatementVehicleADriverCountry.text.toString()
            this.vehicleADriverPhoneNumber =
                binding.etStatementVehicleAInsuranceAgencyPhoneNumber.text.toString()
            this.vehicleADriverEmail = binding.etStatementVehicleADriverEmail.text.toString()
            this.vehicleADriverDrivingLicenseNr =
                binding.etStatementVehicleADriverDrivingLicenseNumber.text.toString()
            this.vehicleADriverDrivingLicenseExpirationDate =
                binding.etStatementVehicleADriverDrivingLicenseExpirationDate.text.toString()
        }
    }
}