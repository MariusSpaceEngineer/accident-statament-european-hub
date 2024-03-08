package com.inetum.realdolmen.crashkit.fragments.statement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentNewStatementBinding
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a.VehicleANewStatementFragment
import com.inetum.realdolmen.crashkit.utils.printBackStack

class NewStatementFragment : Fragment() {
    private var _binding: FragmentNewStatementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()


        binding.btnStatementAccidentNext.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, VehicleANewStatementFragment())
                addToBackStack("vehicle_a_new_statement_fragment")
                setReorderingAllowed(true)
                commit()
            }
        }

    }
}