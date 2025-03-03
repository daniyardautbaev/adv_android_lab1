package com.example.androidadvancedlab1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidadvancedlab1.R

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("Fragment Loaded!")  // Debugging

        val navController = findNavController()

        view.findViewById<Button>(R.id.instagram)?.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_instagramFragment)
        }
        view.findViewById<Button>(R.id.music)?.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_musicFragment)
        }
        view.findViewById<Button>(R.id.airplane)?.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_broadcastFragment)
        }
        view.findViewById<Button>(R.id.calendar)?.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_calendarFragment)
        }
    }
}
