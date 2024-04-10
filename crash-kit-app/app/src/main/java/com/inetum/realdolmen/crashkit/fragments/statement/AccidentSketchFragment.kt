package com.inetum.realdolmen.crashkit.fragments.statement

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.SketchView
import com.inetum.realdolmen.crashkit.adapters.ShapesAdapter
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentSketchBinding

class AccidentSketchFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentAccidentSketchBinding? = null
    private val binding get() = _binding!!

    private lateinit var sketchView: SketchView
    private lateinit var navBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if(this::navController.isInitialized) {
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
        _binding = FragmentAccidentSketchBinding.inflate(inflater, container, false)
        val view = binding.root

        sketchView = view.findViewById(R.id.sketchView)
        sketchView.setupButtons(
            binding.btnAccidentSketchDelete,
            binding.btnAccidentSketchChangeAddress
        )

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = findNavController()
        navBar = requireActivity().findViewById(R.id.bottomNavigationView)

        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }

        navBar.visibility = View.GONE

        binding.btnAccidentSketchSearchShape.setOnClickListener {
            val dialog = Dialog(requireContext(), R.style.Theme_CrashKit)
            val recyclerView = RecyclerView(requireContext())
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView.adapter = ShapesAdapter { drawableResId, priority ->
                sketchView.addShape(drawableResId, priority)
                dialog.dismiss()
            }

            dialog.setContentView(recyclerView)
            dialog.show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    }
}