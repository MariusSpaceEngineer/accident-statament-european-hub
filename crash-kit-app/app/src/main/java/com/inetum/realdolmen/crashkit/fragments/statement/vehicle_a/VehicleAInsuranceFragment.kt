package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.NewStatementViewModel
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleAInsuranceBinding
import com.inetum.realdolmen.crashkit.utils.printBackStack


class VehicleAInsuranceFragment : Fragment() {
    private var _binding: FragmentVehicleAInsuranceBinding? = null
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
        _binding = FragmentVehicleAInsuranceBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        // Observe the statementData
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementVehicleAInsuranceCompanyName.setText(statementData.vehicleAInsuranceCompanyName)
            binding.etStatementVehicleAInsuranceCompanyPolicyNumber.setText(statementData.vehicleAInsuranceCompanyPolicyNumber)
            binding.etStatementVehicleAInsuranceCompanyGreenCardNumber.setText(statementData.vehicleAInsuranceCompanyGreenCardNumber)
            binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate.setText(
                statementData.vehicleAInsuranceCertificateAvailabilityDate
            )
            binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate.setText(
                statementData.vehicleAInsuranceCertificateExpirationDate
            )
            binding.etStatementVehicleAInsuranceAgencyName.setText(statementData.vehicleAInsuranceAgencyName)
            binding.etStatementVehicleAInsuranceAgencyAddress.setText(statementData.vehicleAInsuranceAgencyAddress)
            binding.etStatementVehicleAInsuranceAgencyCountry.setText(statementData.vehicleAInsuranceAgencyCountry)
            binding.etStatementVehicleAInsuranceAgencyPhoneNumber.setText(statementData.vehicleAInsuranceAgencyPhoneNumber)
            binding.cbStatementDamagedCovered.isChecked =
                statementData.vehicleAMaterialDamageCovered
        })

        binding.btnStatementAccidentPrevious.setOnClickListener {
            requireActivity().supportFragmentManager.apply {
                popBackStack(
                    "vehicle_a_insurance_fragment",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            model.statementData.value?.apply {
                this.vehicleAInsuranceCompanyName =
                    binding.etStatementVehicleAInsuranceCompanyName.text.toString()
                this.vehicleAInsuranceCompanyPolicyNumber =
                    binding.etStatementVehicleAInsuranceCompanyPolicyNumber.text.toString()
                this.vehicleAInsuranceCompanyGreenCardNumber =
                    binding.etStatementVehicleAInsuranceCompanyGreenCardNumber.text.toString()
                this.vehicleAInsuranceCertificateAvailabilityDate =
                    binding.etStatementVehicleAInsuranceCompanyCertificateAvailabilityDate.text.toString()
                this.vehicleAInsuranceCertificateExpirationDate =
                    binding.etStatementVehicleAInsuranceCompanyCertificateExpirationDate.text.toString()
                this.vehicleAInsuranceAgencyName =
                    binding.etStatementVehicleAInsuranceAgencyName.text.toString()
                this.vehicleAInsuranceAgencyAddress =
                    binding.etStatementVehicleAInsuranceAgencyAddress.text.toString()
                this.vehicleAInsuranceAgencyCountry =
                    binding.etStatementVehicleAInsuranceAgencyCountry.text.toString()
                this.vehicleAInsuranceAgencyPhoneNumber =
                    binding.etStatementVehicleAInsuranceAgencyPhoneNumber.text.toString()
                this.vehicleAMaterialDamageCovered = binding.cbStatementDamagedCovered.isChecked
            }

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, VehicleADriverFragment())
                addToBackStack("vehicle_a_driver_fragment")
                setReorderingAllowed(true)
                commit()
            }
        }
    }

}