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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.adapters.ImageAdapter
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleBMiscellaneousBinding
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler

class VehicleBMiscellaneousFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController

    private var _binding: FragmentVehicleBMiscellaneousBinding? = null
    private val binding get() = _binding!!

    private var accidentImages = mutableListOf<Bitmap>()
    private val requestImageCapture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null && accidentImages.size < 3) {
                accidentImages.add(imageBitmap)

                // Update the ViewPager2's adapter
                val viewPager =
                    view?.findViewById<ViewPager2>(R.id.vp_statement_vehicle_b_accident_photos)
                viewPager?.visibility = View.VISIBLE
                viewPager?.adapter = ImageAdapter(accidentImages, this.requireContext())

                Toast.makeText(
                    requireContext(),
                    "Image captured successfully!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Error capturing image or maximum number of images reached",
                Toast.LENGTH_SHORT
            ).show()
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

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            updateViewModelFromUI(model)

            navController.navigate(R.id.accidentSketchFragment)
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
            if (!statementData.vehicleBAccidentPhotos.isNullOrEmpty()) {
                accidentImages = statementData.vehicleBAccidentPhotos!!
                val viewPager =
                    view?.findViewById<ViewPager2>(R.id.vp_statement_vehicle_b_accident_photos)
                viewPager?.visibility = View.VISIBLE
                viewPager?.adapter = ImageAdapter(accidentImages, this.requireContext())
            }
        })
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleBRemarks = binding.etStatementVehicleBDriverRemarks.text.toString()
            this.vehicleBDamageDescription =
                binding.etStatementVehicleBDamageDescription.text.toString()
            this.vehicleBAccidentPhotos = accidentImages

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