package edu.campusvirtual.comunica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.calendar.Event
import edu.campusvirtual.comunica.models.calendar.EventCOM
import edu.campusvirtual.comunica.R


class CalendarAdapter(val inboxes: ArrayList<EventCOM>, val listener: (EventCOM) -> Unit): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cell_default, parent, false)

        return CalendarAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(inboxes[position], listener) }

    override fun getItemCount(): Int = inboxes.size


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.textViewId)

        fun bind(item: EventCOM, listener: (EventCOM) -> Unit) {
            title.text = item.title

            itemView.setOnClickListener { listener(item) }
        }
    }
}
