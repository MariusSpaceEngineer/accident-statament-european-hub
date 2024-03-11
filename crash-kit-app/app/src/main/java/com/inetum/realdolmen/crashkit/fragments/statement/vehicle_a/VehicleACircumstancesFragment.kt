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
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleACircumstancesBinding
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleACircumstancesFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleACircumstancesBinding? = null
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
        _binding = FragmentVehicleACircumstancesBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_a_circumstances_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                VehicleAMiscellaneousFragment(),
                "vehicle_a_miscellaneous_fragment"
            )
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