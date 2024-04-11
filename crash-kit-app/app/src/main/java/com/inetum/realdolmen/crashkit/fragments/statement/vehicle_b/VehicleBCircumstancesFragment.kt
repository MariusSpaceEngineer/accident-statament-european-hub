package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

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
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBCircumstancesBinding
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler

class VehicleBCircumstancesFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentVehicleBCircumstancesBinding? = null
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
        navController = findNavController()

        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }
        // Inflate the layout for this fragment
        _binding =
            FragmentVehicleBCircumstancesBinding.inflate(inflater, container, false)
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

            navController.navigate(R.id.vehicleBMiscellaneousFragment)
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.cbStatementCircumstancesVehicleBParkedStopped.isChecked =
                statementData.vehicleBParkedStopped
            binding.cbStatementCircumstancesVehicleBLeavingParkingOpeningDoor.isChecked =
                statementData.vehicleBLeavingParkingOpeningDoor
            binding.cbStatementCircumstancesVehicleBEnteringParking.isChecked =
                statementData.vehicleBEnteringParking
            binding.cbStatementCircumstancesVehicleBEmergingFromCarParkPrivateGroundTrack.isChecked =
                statementData.vehicleBEmergingParkPrivateGroundTrack
            binding.cbStatementCircumstancesVehicleBEnteringCarParkPrivateGroundTrack.isChecked =
                statementData.vehicleBEnteringCarParkPrivateGroundTrack
            binding.cbStatementCircumstancesVehicleBEnteringRoundabout.isChecked =
                statementData.vehicleBEnteringRoundabout
            binding.cbStatementCircumstancesVehicleBCirculatingRoundabout.isChecked =
                statementData.vehicleBCirculatingRoundabout
            binding.cbStatementCircumstancesVehicleBStrikingRearSameDirectionSameLane.isChecked =
                statementData.vehicleBStrikingRearSameDirectionLane
            binding.cbStatementCircumstancesVehicleBGoingSameDirectionDifferentLane.isChecked =
                statementData.vehicleBGoingSameDirectionDifferentLane
            binding.cbStatementCircumstancesVehicleBChangingLanes.isChecked =
                statementData.vehicleBChangingLane
            binding.cbStatementCircumstancesVehicleBOvertaking.isChecked =
                statementData.vehicleBOvertaking
            binding.cbStatementCircumstancesVehicleBTurningRight.isChecked =
                statementData.vehicleBTurningRight
            binding.cbStatementCircumstancesVehicleBTurningLeft.isChecked =
                statementData.vehicleBTurningLeft
            binding.cbStatementCircumstancesVehicleBReversing.isChecked =
                statementData.vehicleBReversing
            binding.cbStatementCircumstancesVehicleBEncroachingReservedLaneForOppositeDirection.isChecked =
                statementData.vehicleBEncroachingLaneOppositeDirection
            binding.cbStatementCircumstancesVehicleBComingRightJunction.isChecked =
                statementData.vehicleBComingRightJunction
            binding.cbStatementCircumstancesVehicleBNotObservedSignRedLight.isChecked =
                statementData.vehicleBNotObservedSignRedLight
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBParkedStopped =
                binding.cbStatementCircumstancesVehicleBParkedStopped.isChecked
            this.vehicleBLeavingParkingOpeningDoor =
                binding.cbStatementCircumstancesVehicleBLeavingParkingOpeningDoor.isChecked
            this.vehicleBEnteringParking =
                binding.cbStatementCircumstancesVehicleBEnteringParking.isChecked
            this.vehicleBEmergingParkPrivateGroundTrack =
                binding.cbStatementCircumstancesVehicleBEmergingFromCarParkPrivateGroundTrack.isChecked
            this.vehicleBEnteringCarParkPrivateGroundTrack =
                binding.cbStatementCircumstancesVehicleBEnteringCarParkPrivateGroundTrack.isChecked
            this.vehicleBEnteringRoundabout =
                binding.cbStatementCircumstancesVehicleBEnteringRoundabout.isChecked
            this.vehicleBCirculatingRoundabout =
                binding.cbStatementCircumstancesVehicleBCirculatingRoundabout.isChecked
            this.vehicleBStrikingRearSameDirectionLane =
                binding.cbStatementCircumstancesVehicleBStrikingRearSameDirectionSameLane.isChecked
            this.vehicleBGoingSameDirectionDifferentLane =
                binding.cbStatementCircumstancesVehicleBGoingSameDirectionDifferentLane.isChecked
            this.vehicleBChangingLane =
                binding.cbStatementCircumstancesVehicleBChangingLanes.isChecked
            this.vehicleBOvertaking =
                binding.cbStatementCircumstancesVehicleBOvertaking.isChecked
            this.vehicleBTurningRight =
                binding.cbStatementCircumstancesVehicleBTurningRight.isChecked
            this.vehicleBTurningLeft =
                binding.cbStatementCircumstancesVehicleBTurningLeft.isChecked
            this.vehicleBReversing = binding.cbStatementCircumstancesVehicleBReversing.isChecked
            this.vehicleBEncroachingLaneOppositeDirection =
                binding.cbStatementCircumstancesVehicleBEncroachingReservedLaneForOppositeDirection.isChecked
            this.vehicleBComingRightJunction =
                binding.cbStatementCircumstancesVehicleBComingRightJunction.isChecked
            this.vehicleBNotObservedSignRedLight =
                binding.cbStatementCircumstancesVehicleBNotObservedSignRedLight.isChecked
        }
    }

}