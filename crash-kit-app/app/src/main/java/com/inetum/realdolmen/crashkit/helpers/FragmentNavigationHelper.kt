package com.inetum.realdolmen.crashkit.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentNavigationHelper(private val fragmentManager: FragmentManager) {

    fun navigateToFragment(
        fragmentContainerView: Int,
        fragment: Fragment,
        backstackName: String
    ) {
        fragmentManager.beginTransaction().apply {
            replace(fragmentContainerView, fragment)
            addToBackStack(backstackName)
            setReorderingAllowed(true)
            commit()
        }
    }

    fun popBackStackInclusive(fragmentTag: String) {
        fragmentManager.popBackStack(
            fragmentTag,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}