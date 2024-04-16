package com.inetum.realdolmen.crashkit.fragments.statement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.gcacace.signaturepad.views.SignaturePad
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

        binding.btnStatementAccidentSubmit.setOnClickListener {
            if (driversAgree()) {
                binding.tvStatementSignatureNeededError.visibility = View.GONE
                updateViewModelFromUI(model)
            } else {
                binding.tvStatementSignatureNeededError.visibility = View.VISIBLE
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