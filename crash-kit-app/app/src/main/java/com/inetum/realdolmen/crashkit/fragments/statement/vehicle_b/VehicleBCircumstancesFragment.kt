package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBCircumstancesBinding
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler

class VehicleBCircumstancesFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private lateinit var checkboxes: List<CheckBox>
    private lateinit var checkedCheckboxes: List<CheckBox>

    private var _binding: FragmentVehicleBCircumstancesBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lock the screen orientation to portrait
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentVehicleBCircumstancesBinding.inflate(inflater, container, false)
        return binding.root
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
        navController = findNavController()

        setupCheckboxes()

        updateUIFromViewModel(model)

        setupNavigationButtons()
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.vehicleBCircumstances.observe(viewLifecycleOwner) { circumstances ->
            val checkedCheckboxes = circumstances.map { it.text.toString() }
            checkboxes.forEach { checkbox ->
                checkbox.isChecked = checkbox.text.toString() in checkedCheckboxes
            }
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.vehicleBCircumstances.value = checkedCheckboxes
    }

    private fun setupNavigationButtons() {
        binding.btnStatementAccidentPrevious.setOnClickListener {
            checkedCheckboxes = getCheckedCheckboxes(checkboxes)

            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            checkedCheckboxes = getCheckedCheckboxes(checkboxes)

            updateViewModelFromUI(model)

            navController.navigate(R.id.vehicleBMiscellaneousFragment)
        }
    }

    private fun setupCheckboxes() {
        setupCheckboxesList()
        setupCheckboxListeners()
    }

    /**
     * This method sets up listeners for all checkboxes in the `checkboxes` list.
     *
     * The method does the following:
     * 1. Retrieves a string from the application's context with the key `R.string.label_amount_of_crosses`.
     * 2. Iterates over each checkbox in the `checkboxes` list.
     * 3. For each checkbox, it sets an `OnCheckedChangeListener`.
     * 4. Inside the listener, it counts the number of checkboxes that are currently checked.
     * 5. It then updates the text of `tvStatementCircumstancesVehicleATotalCrosses` TextView in the `binding` object with the count of checked checkboxes.
     */
    @SuppressLint("SetTextI18n")
    private fun setupCheckboxListeners() {
        val string = requireContext().getString(R.string.label_amount_of_crosses)

        checkboxes.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { _, _ ->
                val checkedCount = checkboxes.count { it.isChecked }
                // Update your TextView with the checkedCount here
                binding.tvStatementCircumstancesVehicleBTotalCrosses.text =
                    "$string: $checkedCount"
            }
        }
    }

    private fun setupCheckboxesList() {
        checkboxes = listOf(
            // Update the UI here based on the new statementData
            binding.cbStatementCircumstancesVehicleBParkedStopped,
            binding.cbStatementCircumstancesVehicleBLeavingParkingOpeningDoor,
            binding.cbStatementCircumstancesVehicleBEnteringParking,
            binding.cbStatementCircumstancesVehicleBEmergingFromCarParkPrivateGroundTrack,
            binding.cbStatementCircumstancesVehicleBEnteringCarParkPrivateGroundTrack,
            binding.cbStatementCircumstancesVehicleBEnteringRoundabout,
            binding.cbStatementCircumstancesVehicleBCirculatingRoundabout,
            binding.cbStatementCircumstancesVehicleBStrikingRearSameDirectionSameLane,
            binding.cbStatementCircumstancesVehicleBGoingSameDirectionDifferentLane,
            binding.cbStatementCircumstancesVehicleBChangingLanes,
            binding.cbStatementCircumstancesVehicleBOvertaking,
            binding.cbStatementCircumstancesVehicleBTurningRight,
            binding.cbStatementCircumstancesVehicleBTurningLeft,
            binding.cbStatementCircumstancesVehicleBReversing,
            binding.cbStatementCircumstancesVehicleBEncroachingReservedLaneForOppositeDirection,
            binding.cbStatementCircumstancesVehicleBComingRightJunction,
            binding.cbStatementCircumstancesVehicleBNotObservedSignRedLight,
        )
    }

    private fun getCheckedCheckboxes(checkboxes: List<CheckBox>): List<CheckBox> {
        return checkboxes.filter { it.isChecked }
    }
}