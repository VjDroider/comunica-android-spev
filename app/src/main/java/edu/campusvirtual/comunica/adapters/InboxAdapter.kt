package edu.campusvirtual.comunica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.inbox.Inbox
import edu.campusvirtual.comunica.R

class InboxAdapter(val inboxes: ArrayList<Inbox>, val listener: (Inbox) -> Unit): RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.inbox_item_cell, parent, false)

        return InboxAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(inboxes[position], listener) }

    override fun getItemCount(): Int = inboxes.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleInbox)
        val count = itemView.findViewById<Button>(R.id.countInbox)

        fun bind(item: Inbox, listener: (Inbox) -> Unit) {
            title.text = item.title

            if(item.badge) {
                count.visibility = View.VISIBLE
                count.text = item.count.toString()
            } else {
                count.visibility = View.INVISIBLE
            }


            itemView.setOnClickListener { listener(item) }
        }
    }
}