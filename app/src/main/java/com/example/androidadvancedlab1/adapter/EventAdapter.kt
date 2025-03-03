package com.example.androidadvancedlab1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidadvancedlab1.R
import com.example.androidadvancedlab1.model.CalendarEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(private val onEventClick: (CalendarEvent) -> Unit) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events: List<CalendarEvent> = emptyList()

    fun setEvents(newEvents: List<CalendarEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view, onEventClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View, private val onEventClick: (CalendarEvent) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        private val eventDate: TextView = itemView.findViewById(R.id.eventDate)

        fun bind(event: CalendarEvent) {
            eventTitle.text = event.title

            // Форматируем дату
            val formattedDate = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
                .format(Date(event.startTime))
            eventDate.text = formattedDate

            // Нажатие на карточку
            itemView.setOnClickListener {
                onEventClick(event)
            }
        }
    }
}
