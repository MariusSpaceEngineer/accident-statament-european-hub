package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var _binding: FragmentVehicleACircumstancesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()

        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }
        // Inflate the layout for this fragment
        _binding = FragmentVehicleACircumstancesBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
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
        
        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            navController.navigate(R.id.vehicleAMiscellaneousFragment)
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.cbStatementCircumstancesVehicleAParkedStopped.isChecked =
                statementData.vehicleAParkedStopped
            binding.cbStatementCircumstancesVehicleALeavingParkingOpeningDoor.isChecked =
                statementData.vehicleALeavingParkingOpeningDoor
            binding.cbStatementCircumstancesVehicleAEnteringParking.isChecked =
                statementData.vehicleAEnteringParking
            binding.cbStatementCircumstancesVehicleAEmergingFromCarParkPrivateGroundTrack.isChecked =
                statementData.vehicleAEmergingParkPrivateGroundTrack
            binding.cbStatementCircumstancesVehicleAEnteringCarParkPrivateGroundTrack.isChecked =
                statementData.vehicleAEnteringCarParkPrivateGroundTrack
            binding.cbStatementCircumstancesVehicleAEnteringRoundabout.isChecked =
                statementData.vehicleAEnteringRoundabout
            binding.cbStatementCircumstancesVehicleACirculatingRoundabout.isChecked =
                statementData.vehicleACirculatingRoundabout
            binding.cbStatementCircumstancesVehicleAStrikingRearSameDirectionSameLane.isChecked =
                statementData.vehicleAStrikingRearSameDirectionLane
            binding.cbStatementCircumstancesVehicleAGoingSameDirectionDifferentLane.isChecked =
                statementData.vehicleAGoingSameDirectionDifferentLane
            binding.cbStatementCircumstancesVehicleAChangingLanes.isChecked =
                statementData.vehicleAChangingLane
            binding.cbStatementCircumstancesVehicleAOvertaking.isChecked =
                statementData.vehicleAOvertaking
            binding.cbStatementCircumstancesVehicleATurningRight.isChecked =
                statementData.vehicleATurningRight
            binding.cbStatementCircumstancesVehicleATurningLeft.isChecked =
                statementData.vehicleATurningLeft
            binding.cbStatementCircumstancesVehicleAReversing.isChecked =
                statementData.vehicleAReversing
            binding.cbStatementCircumstancesVehicleAEncroachingReservedLaneForOppositeDirection.isChecked =
                statementData.vehicleAEncroachingLaneOppositeDirection
            binding.cbStatementCircumstancesVehicleAComingRightJunction.isChecked =
                statementData.vehicleAComingRightJunction
            binding.cbStatementCircumstancesVehicleANotObservedSignRedLight.isChecked =
                statementData.vehicleANotObservedSignRedLight
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleAParkedStopped =
                binding.cbStatementCircumstancesVehicleAParkedStopped.isChecked
            this.vehicleALeavingParkingOpeningDoor =
                binding.cbStatementCircumstancesVehicleALeavingParkingOpeningDoor.isChecked
            this.vehicleAEnteringParking =
                binding.cbStatementCircumstancesVehicleAEnteringParking.isChecked
            this.vehicleAEmergingParkPrivateGroundTrack =
                binding.cbStatementCircumstancesVehicleAEmergingFromCarParkPrivateGroundTrack.isChecked
            this.vehicleAEnteringCarParkPrivateGroundTrack =
                binding.cbStatementCircumstancesVehicleAEnteringCarParkPrivateGroundTrack.isChecked
            this.vehicleAEnteringRoundabout =
                binding.cbStatementCircumstancesVehicleAEnteringRoundabout.isChecked
            this.vehicleACirculatingRoundabout =
                binding.cbStatementCircumstancesVehicleACirculatingRoundabout.isChecked
            this.vehicleAStrikingRearSameDirectionLane =
                binding.cbStatementCircumstancesVehicleAStrikingRearSameDirectionSameLane.isChecked
            this.vehicleAGoingSameDirectionDifferentLane =
                binding.cbStatementCircumstancesVehicleAGoingSameDirectionDifferentLane.isChecked
            this.vehicleAChangingLane =
                binding.cbStatementCircumstancesVehicleAChangingLanes.isChecked
            this.vehicleAOvertaking = binding.cbStatementCircumstancesVehicleAOvertaking.isChecked
            this.vehicleATurningRight =
                binding.cbStatementCircumstancesVehicleATurningRight.isChecked
            this.vehicleATurningLeft = binding.cbStatementCircumstancesVehicleATurningLeft.isChecked
            this.vehicleAReversing = binding.cbStatementCircumstancesVehicleAReversing.isChecked
            this.vehicleAEncroachingLaneOppositeDirection =
                binding.cbStatementCircumstancesVehicleAEncroachingReservedLaneForOppositeDirection.isChecked
            this.vehicleAComingRightJunction =
                binding.cbStatementCircumstancesVehicleAComingRightJunction.isChecked
            this.vehicleANotObservedSignRedLight =
                binding.cbStatementCircumstancesVehicleANotObservedSignRedLight.isChecked
        }
    }
}