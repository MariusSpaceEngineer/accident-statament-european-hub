package com.inetum.realdolmen.crashkit.fragments.statement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentNewStatementBinding
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleANewStatementFragment
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack

class NewStatementFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentNewStatementBinding? = null
    private val binding get() = _binding!!

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleANewStatementFragment(),
                "vehicle_a_new_statement_fragment"
            )
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementAccidentDate.setText(statementData.dateOfAccident)
            binding.etStatementAccidentLocation.setText(statementData.accidentLocation)
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
            this.dateOfAccident = binding.etStatementAccidentDate.text.toString()
            this.accidentLocation = binding.etStatementAccidentLocation.text.toString()
            this.materialDamageToOtherVehicles =
                binding.cbStatementAccidentMaterialDamageOtherVehicles.isChecked
            this.materialDamageToObjects =
                binding.cbStatementAccidentMaterialDamageOtherObjects.isChecked
            this.witnessName = binding.etStatementWitnessName.text.toString()
            this.witnessAddress = binding.etStatementWitnessAddress.text.toString()
            this.witnessPhoneNumber = binding.etStatementWitnessPhone.text.toString()
        }
    }
}