package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBDriverBinding
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack


class VehicleBDriverFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleBDriverBinding? = null
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
        _binding =
            FragmentVehicleBDriverBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_b_driver_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleBCircumstancesFragment(),
                "vehicle_b_circumstances_fragment"
            )
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementVehicleBDriverName.setText(statementData.vehicleBDriverLastName)
            binding.etStatementVehicleBDriverFirstName.setText(statementData.vehicleBDriverFirstName)
            binding.etStatementVehicleBDriverDateOfBirth.setText(statementData.vehicleBDriverDateOfBirth)
            binding.etStatementVehicleBDriverAddress.setText(statementData.vehicleBDriverAddress)
            binding.etStatementVehicleBDriverCountry.setText(statementData.vehicleBDriverCountry)
            binding.etStatementVehicleBInsuranceAgencyPhoneNumber.setText(statementData.vehicleBDriverPhoneNumber)
            binding.etStatementVehicleBDriverEmail.setText(statementData.vehicleBDriverEmail)
            binding.etStatementVehicleBDriverDrivingLicenseNumber.setText(statementData.vehicleBDriverDrivingLicenseNr)
            binding.etStatementVehicleBDriverDrivingLicenseExpirationDate.setText(statementData.vehicleBDriverDrivingLicenseExpirationDate)
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBDriverLastName = binding.etStatementVehicleBDriverName.text.toString()
            this.vehicleBDriverFirstName =
                binding.etStatementVehicleBDriverFirstName.text.toString()
            this.vehicleBDriverDateOfBirth =
                binding.etStatementVehicleBDriverDateOfBirth.text.toString()
            this.vehicleBDriverAddress =
                binding.etStatementVehicleBDriverAddress.text.toString()
            this.vehicleBDriverCountry =
                binding.etStatementVehicleBDriverCountry.text.toString()
            this.vehicleBDriverPhoneNumber =
                binding.etStatementVehicleBInsuranceAgencyPhoneNumber.text.toString()
            this.vehicleBDriverEmail = binding.etStatementVehicleBDriverEmail.text.toString()
            this.vehicleBDriverDrivingLicenseNr =
                binding.etStatementVehicleBDriverDrivingLicenseNumber.text.toString()
            this.vehicleBDriverDrivingLicenseExpirationDate =
                binding.etStatementVehicleBDriverDrivingLicenseExpirationDate.text.toString()
        }
    }
}