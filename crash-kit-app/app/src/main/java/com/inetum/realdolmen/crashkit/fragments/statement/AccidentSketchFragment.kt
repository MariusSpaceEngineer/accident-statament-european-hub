package com.inetum.realdolmen.crashkit.fragments.statement

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.accidentsketch.SketchView
import com.inetum.realdolmen.crashkit.adapters.ShapesAdapter
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentSketchBinding
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel
import com.inetum.realdolmen.crashkit.utils.SpacesItemDecoration
import com.inetum.realdolmen.crashkit.utils.StatementDataHandler

class AccidentSketchFragment : Fragment(), StatementDataHandler {
    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView

    private val viewModel: NewStatementViewModel by activityViewModels()
    private lateinit var sketchView: SketchView

    private var _binding: FragmentAccidentSketchBinding? = null
    private val binding get() = _binding!!

    private var sketchBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Make orientation landscape
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    }

    override fun onResume() {
        super.onResume()
        //Make orientation landscape
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccidentSketchBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpSketchView(view)

        return view
    }

    /**
     * This method is invoked when the activity's state has been created.
     * It is crucial to initialize the NavController and NavBar within this method to prevent the app from crashing.
     * The NavController must be initialized first in the activity, otherwise, it could lead to a crash.
     *
     * @deprecated This method is deprecated in Java.
     *
     * The method performs the following operations:
     * 1. Initializes the NavController by finding the NavController for this fragment.
     * 2. Initializes the NavBar by finding the BottomNavigationView in the activity.
     * 3. If there is a saved instance state, restores the state of the NavController from it.
     * 4. Sets the visibility of the NavBar to GONE to hide it.
     * 5. Sets up click listeners for the activity.
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = findNavController()
        navBar = requireActivity().findViewById(R.id.bottomNavigationView)

        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }

        navBar.visibility = View.GONE

        setupClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveNavControllerState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun updateUIFromViewModel(model: NewStatementViewModel) {
        TODO()
    }

    override fun updateViewModelFromUI(model: NewStatementViewModel) {
        model.statementData.value?.apply {
            viewModel.statementData.value?.accidentSketch = sketchBitmap
        }
        model.accidentSketchShapes.value?.apply {
            viewModel.accidentSketchShapes.value = sketchView.shapes
        }
    }

    private fun setUpSketchView(view: FrameLayout) {
        sketchView = view.findViewById(R.id.sketchView)
        sketchView.viewModel = viewModel
        sketchView.setupButtons(
            binding.btnAccidentSketchDelete,
            binding.btnAccidentSketchChangeAddress
        )
        viewModel.accidentSketchShapes.observe(viewLifecycleOwner) { newShapes ->
            // Only add new shapes
            newShapes.forEach { newShape ->
                if (newShape !in sketchView.shapes) {
                    sketchView.shapes.add(newShape)
                }
            }
            sketchView.invalidate()
        }
    }

    private fun setupClickListeners() {
        binding.btnAccidentSketchSearchShape.setOnClickListener {
            val dialog = Dialog(requireContext(), R.style.Theme_CrashKit)
            val recyclerView = setupRecyclerView(dialog)

            dialog.setContentView(recyclerView)
            dialog.show()
        }

        binding.ivAccidentSketchPrevious.setOnClickListener {
            sketchBitmap = if (sketchView.shapes.isNotEmpty()) {
                createBitmapFromView(sketchView)
            } else {
                null
            }
            updateViewModelFromUI(viewModel)
            navController.popBackStack()
        }

        binding.ivAccidentSketchNext.setOnClickListener {
            sketchBitmap = if (sketchView.shapes.isNotEmpty()) {
                createBitmapFromView(sketchView)
            } else {
                null
            }
            updateViewModelFromUI(viewModel)
            navController.navigate(R.id.accidentStatementOverviewFragment)
        }
    }

    private fun setupRecyclerView(dialog: Dialog): RecyclerView {
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.addItemDecoration(SpacesItemDecoration(20))
        recyclerView.adapter = ShapesAdapter { drawableResId, priority ->
            sketchView.addShape(drawableResId, priority)
            dialog.dismiss()
        }
        return recyclerView
    }

    private fun saveNavControllerState(outState: Bundle) {
        if (this::navController.isInitialized) {
            outState.putBundle("nav_state", navController.saveState())
        }
    }

    private fun createBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

}