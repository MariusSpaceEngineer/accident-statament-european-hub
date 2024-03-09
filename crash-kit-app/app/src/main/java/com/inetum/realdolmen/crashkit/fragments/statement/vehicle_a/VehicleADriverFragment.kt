package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.NewStatementViewModel
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleADriverBinding
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleADriverFragment : Fragment() {

    private var _binding: FragmentVehicleADriverBinding? = null
    private val binding get() = _binding!!
    private lateinit var model: NewStatementViewModel

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

        // Observe the statementData
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

        binding.btnStatementAccidentPrevious.setOnClickListener {

            requireActivity().supportFragmentManager.apply {
                popBackStack("vehicle_a_driver_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)

            }
        }

        binding.btnStatementAccidentNext.setOnClickListener {

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

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragmentContainerView,
                    VehicleACircumstancesFragment()
                )
                addToBackStack("vehicle_a_circumstances_fragment")
                setReorderingAllowed(true)
                commit()
            }
        }

    }
}