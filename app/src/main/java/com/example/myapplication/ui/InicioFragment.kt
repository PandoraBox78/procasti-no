package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTareas.setOnClickListener {
            findNavController().navigate(R.id.navigation_tareas)
        }

        binding.btnBienestar.setOnClickListener {
            findNavController().navigate(R.id.navigation_bienestar)
        }

        binding.btnPomodoro.setOnClickListener {
            findNavController().navigate(R.id.navigation_pomodoro)
        }
        binding.btnStatistics.setOnClickListener {
            findNavController().navigate(R.id.navigation_statistics)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


