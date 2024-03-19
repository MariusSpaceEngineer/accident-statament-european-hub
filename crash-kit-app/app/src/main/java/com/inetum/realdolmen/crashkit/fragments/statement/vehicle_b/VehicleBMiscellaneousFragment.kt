package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBMiscellaneousBinding
import com.inetum.realdolmen.crashkit.fragments.statement.AccidentStatementOverviewFragment
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleBMiscellaneousFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel

    private var _binding: FragmentVehicleBMiscellaneousBinding? = null
    private val binding get() = _binding!!

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }

    private var accidentImage: Bitmap? = null

    private val requestImageCapture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                binding.ivStatementVehicleBAccidentPhoto.setImageBitmap(
                    imageBitmap.scale(
                        850,
                        850
                    )
                )
                binding.ivStatementVehicleBAccidentPhoto.visibility = View.VISIBLE
                accidentImage = imageBitmap
                model.statementData.value?.vehicleBAccidentPhoto = imageBitmap
                Toast.makeText(requireContext(), "Image captured successfully!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Error capturing image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, start image capture
            startImageCapture()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
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
            FragmentVehicleBMiscellaneousBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.popBackStackInclusive("vehicle_b_miscellaneous_fragment")
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                AccidentStatementOverviewFragment(),
                "accident_overview_fragment"
            )
        }

        binding.btnStatementAccidentPicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted, start image capture
                startImageCapture()
            } else {
                // Request camera permission
                requestCameraPermission()
            }
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            binding.etStatementVehicleBDriverRemarks.setText(statementData.vehicleBRemarks)
            binding.etStatementVehicleBDamageDescription.setText(statementData.vehicleBDamageDescription)
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBRemarks = binding.etStatementVehicleBDriverRemarks.text.toString()
            this.vehicleBDamageDescription =
                binding.etStatementVehicleBDamageDescription.text.toString()
        }
    }

    private fun requestCameraPermission() {
        // Request camera permission
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun startImageCapture() {
        val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        requestImageCapture.launch(captureImageIntent)
    }

}