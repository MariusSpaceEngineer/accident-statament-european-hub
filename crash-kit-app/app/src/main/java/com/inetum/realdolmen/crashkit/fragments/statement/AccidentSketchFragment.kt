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
import com.inetum.realdolmen.crashkit.adapters.SpacesItemDecoration
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentSketchBinding
import com.inetum.realdolmen.crashkit.utils.NewStatementViewModel

class AccidentSketchFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView

    private val viewModel: NewStatementViewModel by activityViewModels()
    private lateinit var sketchView: SketchView

    private var _binding: FragmentAccidentSketchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Make orientation landscape
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccidentSketchBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpSketchView(view)

        return view
    }

    //Needed otherwise the navController would crash the app as it has to be initialized first in the activity
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

    override fun onDestroyView() {
        super.onDestroyView()
        //Restore orientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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
            val recyclerView = RecyclerView(requireContext())
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView.addItemDecoration(SpacesItemDecoration(20))
            recyclerView.adapter = ShapesAdapter { drawableResId, priority ->
                sketchView.addShape(drawableResId, priority)
                dialog.dismiss()
            }

            dialog.setContentView(recyclerView)
            dialog.show()
        }

        binding.ivAccidentSketchPrevious.setOnClickListener {
            val sketch = createBitmapFromView(sketchView)
            viewModel.statementData.value?.apply {
                viewModel.statementData.value?.accidentSketch = sketch
            }
            navController.popBackStack()
        }

        binding.ivAccidentSketchNext.setOnClickListener {
            val sketch = createBitmapFromView(sketchView)
            viewModel.statementData.value?.apply {
                viewModel.statementData.value?.accidentSketch = sketch
            }
            navController.navigate(R.id.accidentStatementOverviewFragment)
        }
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