package com.inetum.realdolmen.crashkit.fragments.statement

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentStatementSignatureBinding
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler

class AccidentStatementSignatureFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentAccidentStatementSignatureBinding? = null
    private val binding get() = _binding!!

    private lateinit var vehicleASignaturePad: SignaturePad
    private lateinit var vehicleBSignaturePad: SignaturePad

    private var driverASigned = false
    private var driverBSigned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccidentStatementSignatureBinding.inflate(inflater, container, false)

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

        vehicleASignaturePad = binding.spStatementVehicleA
        vehicleASignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {

            override fun onStartSigning() {
                driverASigned = true
            }

            override fun onSigned() {
                // Event triggered when the pad is signed
            }

            override fun onClear() {
                driverASigned = false
            }
        })

        vehicleBSignaturePad = binding.spStatementVehicleB
        vehicleBSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {

            override fun onStartSigning() {
                driverBSigned = true
            }

            override fun onSigned() {
                // Event triggered when the pad is signed
            }

            override fun onClear() {
                driverBSigned = false
            }
        })

        binding.btnStatementVehicleADisagree.setOnClickListener {
            createCustomDialog(
                requireContext(),
                R.layout.disagree_dialog,
                R.color.secondary,
                R.color.input_field_background,
                R.drawable.disagree_dialog_background,
                "Proceed",
                "Revert"
            ) { _, _ ->
                navController.popBackStack(R.id.homeFragment, false)
            }
        }

        binding.btnStatementVehicleBDisagree.setOnClickListener {
            createCustomDialog(
                requireContext(),
                R.layout.disagree_dialog,
                R.color.secondary,
                R.color.input_field_background,
                R.drawable.disagree_dialog_background,
                "Proceed",
                "Revert"
            ) { _, _ ->
                navController.popBackStack(R.id.homeFragment, false)
            }
        }


        binding.btnStatementAccidentSubmit.setOnClickListener {
            if (driversAgree()) {
                binding.tvStatementSignatureNeededError.visibility = View.GONE
                createCustomDialog(
                    requireContext(),
                    R.layout.submit_dialog,
                    R.color.primary800,
                    R.color.input_field_background,
                    R.drawable.submit_dialog_background,
                    "Proceed",
                    "Revert"
                ) { _, _ ->
                    navController.popBackStack(R.id.homeFragment, false)
                }
                updateViewModelFromUI(model)
            } else {
                binding.tvStatementSignatureNeededError.visibility = View.VISIBLE
            }
        }

        binding.btnStatementAccidentPrevious.setOnClickListener {
            navController.popBackStack()
        }

    }

    private fun createCustomDialog(
        context: Context,
        layoutResId: Int,
        positiveButtonColorResId: Int,
        negativeButtonColorResId: Int,
        backgroundColorResId: Int,
        positiveButtonText: String,
        negativeButtonText: String,
        onPositiveClick: (Any, Any) -> Unit,
    ) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(layoutResId, null)

        builder.setView(dialogLayout)
        builder.setPositiveButton(positiveButtonText, null)
        builder.setNegativeButton(negativeButtonText, null)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(backgroundColorResId)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(ContextCompat.getColor(context, positiveButtonColorResId))
            setOnClickListener { view ->
                onPositiveClick(view, this)
                dialog.cancel()
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setTextColor(ContextCompat.getColor(context, negativeButtonColorResId))
            setOnClickListener {
                dialog.cancel()
            }
        }
    }

    private fun driversAgree(): Boolean {
        return driverASigned && driverBSigned
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        TODO("Not yet implemented")
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.driverASignature = vehicleASignaturePad.signatureBitmap
            this.driverBSignature = vehicleBSignaturePad.signatureBitmap
        }
    }


}