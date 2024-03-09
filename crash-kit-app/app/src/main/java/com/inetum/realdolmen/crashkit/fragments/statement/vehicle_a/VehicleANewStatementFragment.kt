package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.NewStatementViewModel
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.StatementData
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleANewStatementBinding
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleANewStatementFragment : Fragment() {
    private var _binding: FragmentVehicleANewStatementBinding? = null
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
        _binding = FragmentVehicleANewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        // Observe the statementData
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementPolicyHolderName.setText(statementData.policyHolderALastName)
            binding.etStatementPolicyHolderFirstName.setText(statementData.policyHolderAFirstName)
            binding.etStatementPolicyHolderAddress.setText(statementData.policyHolderAAddress)
            binding.etStatementPolicyHolderPhoneNumber.setText(statementData.policyHolderAPhoneNumber)
            binding.etStatementPolicyHolderEmail.setText(statementData.policyHolderAEmail)
            binding.etStatementVehicleAMarkType.setText(statementData.vehicleAMarkType)
            binding.etStatementVehicleARegistrationNumber.setText(statementData.vehicleARegistrationNumber)
            binding.etStatementVehicleACountry.setText(statementData.vehicleACountryOfRegistration)
        })

        binding.btnStatementAccidentPrevious.setOnClickListener {
            findNavController().apply {
                navigate(R.id.newStatementFragment)
            }
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            model.statementData.value?.apply {
                this.policyHolderALastName = binding.etStatementPolicyHolderName.text.toString()
                this.policyHolderAFirstName = binding.etStatementPolicyHolderFirstName.text.toString()
                this.policyHolderAAddress = binding.etStatementPolicyHolderAddress.text.toString()
                this.policyHolderAPhoneNumber = binding.etStatementPolicyHolderPhoneNumber.text.toString()
                this.policyHolderAEmail = binding.etStatementPolicyHolderEmail.text.toString()
            }

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, VehicleAInsuranceFragment())
                addToBackStack("vehicle_a_insurance_fragment")
                setReorderingAllowed(true)
                commit()
            }
        }

    }

}