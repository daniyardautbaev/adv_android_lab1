package com.example.androidadvancedlab1.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.androidadvancedlab1.R
import com.example.androidadvancedlab1.service.MusicPlayerService
import android.util.Log
import android.app.Notification
class MusicFragment : Fragment() {

    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createNotificationChannel()

        val view = inflater.inflate(R.layout.fragment_music, container, false)
        startButton = view.findViewById(R.id.start)
        pauseButton = view.findViewById(R.id.pause)
        stopButton = view.findViewById(R.id.stop)

        setupButtonListeners()

        return view
    }

    private fun setupButtonListeners() {
        startButton.setOnClickListener {
            handleStartButtonClick()
        }

        pauseButton.setOnClickListener {
            handlePauseButtonClick()
        }

        stopButton.setOnClickListener {
            handleStopButtonClick()
        }
    }

    private fun handleStartButtonClick() {
        val serviceIntent = Intent(context, MusicPlayerService::class.java)
        serviceIntent.action = MusicPlayerService.PLAY_ACTION
        activity?.startService(serviceIntent)
        Log.d("MusicFragment", "Start button clicked")
    }

    private fun handlePauseButtonClick() {
        val serviceIntent = Intent(context, MusicPlayerService::class.java)
        serviceIntent.action = MusicPlayerService.PAUSE_ACTION
        activity?.startService(serviceIntent)
        Log.d("MusicFragment", "Pause button clicked")
    }

    private fun handleStopButtonClick() {
        val serviceIntent = Intent(context, MusicPlayerService::class.java)
        serviceIntent.action = MusicPlayerService.STOP_FOREGROUND_ACTION
        activity?.stopService(serviceIntent)
        Log.d("MusicFragment", "Stop button clicked")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MusicPlayerService.NOTIFICATION_CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music player is running"
                setSound(null, null) // Отключаем звук уведомления
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = context?.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d("MusicFragment", "Notification channel created")
        }
    }
}