package com.example.androidadvancedlab1.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.androidadvancedlab1.R

class BroadcastFragment : Fragment(R.layout.fragment_broadcast) {

    private lateinit var textView: TextView

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                val isAirplaneModeOn = intent.getBooleanExtra("state", false)
                updateUI(isAirplaneModeOn)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_broadcast, container, false)
        textView = view.findViewById(R.id.textView)

        // Устанавливаем мягкий фон
        view.setBackgroundColor(android.graphics.Color.parseColor("#F0F0F0"))

        // Начальный текст
        textView.text = "🔄 Checking Airplane Mode..."
        textView.textSize = 24f
        textView.setTextColor(android.graphics.Color.BLACK)

        return view
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(receiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(receiver)
    }

    private fun updateUI(isAirplaneModeOn: Boolean) {
        val newText = if (isAirplaneModeOn) "✅ Airplane Mode Enabled" else "❌ Airplane Mode Disabled"
        textView.text = newText
    }
}