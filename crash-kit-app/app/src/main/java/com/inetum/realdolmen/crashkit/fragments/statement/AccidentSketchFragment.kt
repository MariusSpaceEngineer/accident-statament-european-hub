package com.inetum.realdolmen.crashkit.fragments.statement

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.SketchView
import com.inetum.realdolmen.crashkit.adapters.ShapesAdapter
import com.inetum.realdolmen.crashkit.databinding.FragmentAccidentSketchBinding

class AccidentSketchFragment : Fragment() {

    private var _binding: FragmentAccidentSketchBinding? = null
    private val binding get() = _binding!!

    private lateinit var sketchView: SketchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccidentSketchBinding.inflate(inflater, container, false)
        val view = binding.root

        sketchView = view.findViewById(R.id.sketchView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAccidentSketchSearchShape.setOnClickListener {
            val dialog = Dialog(requireContext(), R.style.Theme_CrashKit)
            val recyclerView = RecyclerView(requireContext())
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView.adapter = ShapesAdapter { drawableResId ->
                sketchView.addShape(drawableResId)
                dialog.dismiss()
            }

            dialog.setContentView(recyclerView)
            dialog.show()
        }


    }

}