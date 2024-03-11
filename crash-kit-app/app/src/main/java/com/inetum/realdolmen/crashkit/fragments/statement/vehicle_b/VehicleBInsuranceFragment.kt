package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBInsuranceBinding
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleBInsuranceFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleBInsuranceBinding? = null
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
            FragmentVehicleBInsuranceBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_b_insurance_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleBDriverFragment(),
                "vehicle_b_driver_fragment"
            )
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementVehicleBInsuranceCompanyName.setText(statementData.vehicleBInsuranceCompanyName)
            binding.etStatementVehicleBInsuranceCompanyPolicyNumber.setText(statementData.vehicleBInsuranceCompanyPolicyNumber)
            binding.etStatementVehicleBInsuranceCompanyGreenCardNumber.setText(statementData.vehicleBInsuranceCompanyGreenCardNumber)
            binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate.setText(
                statementData.vehicleBInsuranceCertificateAvailabilityDate
            )
            binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate.setText(
                statementData.vehicleBInsuranceCertificateExpirationDate
            )
            binding.etStatementVehicleBInsuranceAgencyName.setText(statementData.vehicleBInsuranceAgencyName)
            binding.etStatementVehicleBInsuranceAgencyAddress.setText(statementData.vehicleBInsuranceAgencyAddress)
            binding.etStatementVehicleBInsuranceAgencyCountry.setText(statementData.vehicleBInsuranceAgencyCountry)
            binding.etStatementVehicleBInsuranceAgencyPhoneNumber.setText(statementData.vehicleBInsuranceAgencyPhoneNumber)
            binding.cbStatementDamagedCovered.isChecked =
                statementData.vehicleBMaterialDamageCovered
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBInsuranceCompanyName =
                binding.etStatementVehicleBInsuranceCompanyName.text.toString()
            this.vehicleBInsuranceCompanyPolicyNumber =
                binding.etStatementVehicleBInsuranceCompanyPolicyNumber.text.toString()
            this.vehicleBInsuranceCompanyGreenCardNumber =
                binding.etStatementVehicleBInsuranceCompanyGreenCardNumber.text.toString()
            this.vehicleBInsuranceCertificateAvailabilityDate =
                binding.etStatementVehicleBInsuranceCompanyCertificateAvailabilityDate.text.toString()
            this.vehicleBInsuranceCertificateExpirationDate =
                binding.etStatementVehicleBInsuranceCompanyCertificateExpirationDate.text.toString()
            this.vehicleBInsuranceAgencyName =
                binding.etStatementVehicleBInsuranceAgencyName.text.toString()
            this.vehicleBInsuranceAgencyAddress =
                binding.etStatementVehicleBInsuranceAgencyAddress.text.toString()
            this.vehicleBInsuranceAgencyCountry =
                binding.etStatementVehicleBInsuranceAgencyCountry.text.toString()
            this.vehicleBInsuranceAgencyPhoneNumber =
                binding.etStatementVehicleBInsuranceAgencyPhoneNumber.text.toString()
            this.vehicleBMaterialDamageCovered = binding.cbStatementDamagedCovered.isChecked
        }
    }

}