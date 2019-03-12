package edu.campusvirtual.comunica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.account.Charge
import edu.campusvirtual.comunica.models.inbox.Inbox
import edu.campusvirtual.comunica.R

/**
 * Created by jonathan on 3/1/18.
 */
class ChargesAdapter(val charges: ArrayList<Charge>): RecyclerView.Adapter<ChargesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cell_charge, parent, false)

        return ChargesAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(charges[position]) }

    override fun getItemCount(): Int = charges.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.textViewConceptId)
        val count = itemView.findViewById<Button>(R.id.textViewbalanceId)

        fun bind(item: Charge) {
            title.text = item.concept
            count.text = item.balance.toString()
        }
    }
}