package com.app.syspoint.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.syspoint.databinding.FragmentHomeBinding
import com.app.syspoint.utils.Constants
import com.app.syspoint.viewmodel.home.HomeViewModel

/*class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setHasOptionsMenu(true)

        if (Constants.solictaRuta) {
            creaRutaSeleccionada()
        }



        return binding.root
    }
}*/