package edu.campusvirtual.comunica.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.kardex.Kardex
import edu.campusvirtual.comunica.R

/**
 * Created by jonathan on 2/28/18.
 */
class KardexAdapter(val elements: ArrayList<Kardex>, val listener: (Kardex) -> Unit): RecyclerView.Adapter<KardexAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_default, parent, false))

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(elements.get(position), listener)
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var text = itemView.findViewById<TextView>(R.id.textViewId)

        fun onBind(item: Kardex, listener: (Kardex) -> Unit) {
            Log.d("here", "heree")
            text.setText(item.study_plan + " - " + item.student)

            itemView.setOnClickListener { listener(item) }
        }
    }
}