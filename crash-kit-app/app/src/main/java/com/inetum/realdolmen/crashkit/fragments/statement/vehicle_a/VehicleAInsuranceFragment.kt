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
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleAInsuranceBinding
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack


class VehicleAInsuranceFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleAInsuranceBinding? = null
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
        _binding = FragmentVehicleAInsuranceBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_a_insurance_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleADriverFragment(),
                "vehicle_a_driver_fragment"
            )
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
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
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
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
    }

}