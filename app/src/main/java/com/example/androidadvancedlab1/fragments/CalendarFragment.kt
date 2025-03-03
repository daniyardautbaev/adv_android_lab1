package com.example.androidadvancedlab1.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.Manifest
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.animation.AnimationUtils
import com.example.androidadvancedlab1.model.CalendarEvent
import com.example.androidadvancedlab1.adapter.EventAdapter
import com.example.androidadvancedlab1.R

class CalendarFragment : Fragment() {

    private val calendarPermission = Manifest.permission.READ_CALENDAR
    private val calendarPermissionCode = 100
    private lateinit var eventAdapter: EventAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        eventAdapter = EventAdapter { event ->
            Toast.makeText(requireContext(), "Вы выбрали: ${event.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = eventAdapter

        requestCalendarPermission()
    }

    private fun requestCalendarPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), calendarPermission)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(calendarPermission), calendarPermissionCode)
        } else {
            loadCalendarEvents()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == calendarPermissionCode && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCalendarEvents()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCalendarEvents() {
        val events = mutableListOf<CalendarEvent>()

        val uri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART
        )

        val selection = "${CalendarContract.Events.DTSTART} >= ?"
        val selectionArgs = arrayOf(System.currentTimeMillis().toString())

        val cursor = requireContext().contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.use {
            while (it.moveToNext()) {
                val eventId = it.getLong(0)
                val title = it.getString(1)
                val startTime = it.getLong(2)

                events.add(CalendarEvent(eventId, title, startTime))
            }
        }

        eventAdapter.setEvents(events)
        applyRecyclerViewAnimation()
    }

    private fun applyRecyclerViewAnimation() {
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }
}
