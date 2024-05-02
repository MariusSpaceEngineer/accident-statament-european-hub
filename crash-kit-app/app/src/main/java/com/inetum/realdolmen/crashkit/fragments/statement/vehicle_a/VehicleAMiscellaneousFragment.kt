package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.adapters.ImageAdapter
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleAMiscellaneousBinding
import com.inetum.realdolmen.crashkit.fragments.statement.PointOfImpactSketch
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler
import com.inetum.realdolmen.crashkit.utils.createBitmapFromView

class VehicleAMiscellaneousFragment : Fragment(), StatementDataHandler {
    private lateinit var model: NewStatementViewModel
    private lateinit var navController: NavController
    private lateinit var pointOfImpactSketchView: PointOfImpactSketch

    private var _binding: FragmentVehicleAMiscellaneousBinding? = null
    private val binding get() = _binding!!

    private var accidentImages = mutableListOf<Bitmap>()
    private val requestImageCapture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null && accidentImages.size < 3) {
                accidentImages.add(imageBitmap)

                // Update the ViewPager2's adapter
                val viewPager =
                    view?.findViewById<ViewPager2>(R.id.vp_statement_vehicle_a_accident_photos)
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
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val shapes = listOf(
        R.drawable.personal_car_vehicle,
        R.drawable.motorcycle_vehicle,
        R.drawable.truck_vehicle,
        R.drawable.direction_arrow
    )
    private var pointOfImpactSketchBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]

    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::navController.isInitialized) {
            // Save the NavController's state
            outState.putBundle("nav_state", navController.saveState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            FragmentVehicleAMiscellaneousBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpSketchView(view)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        // Get a reference to your PointOfImpactSketch and ScrollView
        val scrollView = view.findViewById<ScrollView>(R.id.sv_statement_vehicle_a_miscellaneous)

        // Set an onTouchListener on the PointOfImpactSketch view
        pointOfImpactSketchView.setOnTouchListener { _, _ ->
            // When user touches the PointOfImpactSketch view, we consume the touch event and disable the scroll on the parent ScrollView
            scrollView.requestDisallowInterceptTouchEvent(true)
            false
        }

        updateUIFromViewModel(model)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            pointOfImpactSketchBitmap = if (pointOfImpactSketchView.shapes.isNotEmpty()) {
                pointOfImpactSketchView.createBitmapFromView()
            } else {
                null
            }
            updateViewModelFromUI(model)

            navController.popBackStack()
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            pointOfImpactSketchBitmap = if (pointOfImpactSketchView.shapes.isNotEmpty()) {
                pointOfImpactSketchView.createBitmapFromView()
            } else {
                null
            }
            updateViewModelFromUI(model)

            navController.navigate(R.id.vehicleBNewStatementFragment)
        }

        binding.btnStatementAccidentPicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted, start image capture
                if (accidentImages.size < 3) {
                    startImageCapture()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Maximum number of images reached",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Request camera permission
                requestCameraPermission()
            }
        }
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        model.statementData.observe(viewLifecycleOwner) { statementData ->
            binding.etStatementVehicleADriverRemarks.setText(statementData.vehicleARemarks)
            binding.etStatementVehicleADamageDescription.setText(statementData.vehicleADamageDescription)
            if (!statementData.vehicleAAccidentPhotos.isNullOrEmpty()) {
                accidentImages = statementData.vehicleAAccidentPhotos!!
                val viewPager =
                    view?.findViewById<ViewPager2>(R.id.vp_statement_vehicle_a_accident_photos)
                viewPager?.visibility = View.VISIBLE
                viewPager?.adapter = ImageAdapter(accidentImages, this.requireContext())
            }
        }
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            this.vehicleARemarks = binding.etStatementVehicleADriverRemarks.text.toString()
            this.vehicleADamageDescription =
                binding.etStatementVehicleADamageDescription.text.toString()
            this.vehicleAAccidentPhotos = accidentImages
            this.vehicleAPointOfImpactSketch = pointOfImpactSketchBitmap
        }

        model.pointOfImpactVehicleASketchShapes.value?.apply {
            model.pointOfImpactVehicleASketchShapes.value = pointOfImpactSketchView.shapes
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

    private fun setUpSketchView(view: View) {
        pointOfImpactSketchView = view.findViewById(R.id.poi_vehicle_a_sketch)
        pointOfImpactSketchView.viewModel = model



        if (!model.pointOfImpactVehicleASketchShapes.value.isNullOrEmpty()) {
            model.pointOfImpactVehicleASketchShapes.observe(viewLifecycleOwner) { shapes ->
                // Only add new shapes
                shapes.forEach { newShape ->
                    if (newShape !in pointOfImpactSketchView.shapes) {
                        pointOfImpactSketchView.shapes.add(newShape)
                    }
                }
                pointOfImpactSketchView.invalidate()
            }
        } else {
            pointOfImpactSketchView.addShapes(shapes)
        }

    }

}
