package edu.campusvirtual.comunica.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.kardex.Grade
import edu.campusvirtual.comunica.models.kardex.Kardex
import edu.campusvirtual.comunica.R

/**
 * Created by jonathan on 2/28/18.
 */
class GradesAdapter(val elements: ArrayList<Grade>): RecyclerView.Adapter<GradesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_grade, parent, false))

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(elements.get(position))
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var statusView = itemView.findViewById<View>(R.id.viewStatusId)
        var stuffName = itemView.findViewById<TextView>(R.id.textViewNameId)
        var status = itemView.findViewById<TextView>(R.id.textViewStatusId)
        var score = itemView.findViewById<TextView>(R.id.textViewScoreId)

        fun onBind(item: Grade) {
            if(item.status != "Aprobada") {
                statusView.setBackgroundColor(Color.RED)
            }

            stuffName.setText(item.subject)
            status.setText(item.status)
            score.setText(item.value.toString())

        }
    }
}