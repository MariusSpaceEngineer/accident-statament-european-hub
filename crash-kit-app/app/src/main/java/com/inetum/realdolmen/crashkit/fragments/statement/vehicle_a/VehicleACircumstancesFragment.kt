package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleACircumstancesBinding
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler

class VehicleACircumstancesFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private lateinit var checkboxes: List<CheckBox>
    private lateinit var checkedCheckboxes: List<CheckBox>

    private var _binding: FragmentVehicleACircumstancesBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleACircumstancesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::navController.isInitialized) {
            // Save the NavController's state
            outState.putBundle("nav_state", navController.saveState())
        }
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setupCheckboxes()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            checkedCheckboxes = getCheckedCheckboxes(checkboxes)

            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            checkedCheckboxes = getCheckedCheckboxes(checkboxes)
            updateViewModelFromUI(model)

            navController.navigate(R.id.vehicleAMiscellaneousFragment)
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.vehicleACircumstances.observe(viewLifecycleOwner, Observer { circumstances ->
            val checkedCheckboxes = circumstances.map { it.text.toString() }
            checkboxes.forEach { checkbox ->
                checkbox.isChecked = checkbox.text.toString() in checkedCheckboxes
            }
        })

    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.vehicleACircumstances.value = checkedCheckboxes
    }

    private fun setupCheckboxes() {
        setupCheckboxesList()
        setupCheckboxListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setupCheckboxListeners() {
        val string = requireContext().getString(R.string.label_amount_of_crosses)

        checkboxes.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { _, _ ->
                val checkedCount = checkboxes.count { it.isChecked }
                // Update your TextView with the checkedCount here
                binding.tvStatementCircumstancesVehicleATotalCrosses.text =
                    "$string: $checkedCount"
            }
        }
    }

    private fun setupCheckboxesList() {
        checkboxes = listOf(
            // Update the UI here based on the new statementData
            binding.cbStatementCircumstancesVehicleAParkedStopped,
            binding.cbStatementCircumstancesVehicleALeavingParkingOpeningDoor,
            binding.cbStatementCircumstancesVehicleAEnteringParking,
            binding.cbStatementCircumstancesVehicleAEmergingFromCarParkPrivateGroundTrack,
            binding.cbStatementCircumstancesVehicleAEnteringCarParkPrivateGroundTrack,
            binding.cbStatementCircumstancesVehicleAEnteringRoundabout,
            binding.cbStatementCircumstancesVehicleACirculatingRoundabout,
            binding.cbStatementCircumstancesVehicleAStrikingRearSameDirectionSameLane,
            binding.cbStatementCircumstancesVehicleAGoingSameDirectionDifferentLane,
            binding.cbStatementCircumstancesVehicleAChangingLanes,
            binding.cbStatementCircumstancesVehicleAOvertaking,
            binding.cbStatementCircumstancesVehicleATurningRight,
            binding.cbStatementCircumstancesVehicleATurningLeft,
            binding.cbStatementCircumstancesVehicleAReversing,
            binding.cbStatementCircumstancesVehicleAEncroachingReservedLaneForOppositeDirection,
            binding.cbStatementCircumstancesVehicleAComingRightJunction,
            binding.cbStatementCircumstancesVehicleANotObservedSignRedLight,
        )
    }

    private fun getCheckedCheckboxes(checkboxes: List<CheckBox>): List<CheckBox> {
        return checkboxes.filter { it.isChecked }
    }
}