package edu.campusvirtual.comunica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.template.Template
import edu.campusvirtual.comunica.R

class TemplateAdapter(val templates: ArrayList<Template>, val listener: (Template) -> Unit) : RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return TemplateAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_default, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(templates.get(position), listener)
    }

    override fun getItemCount(): Int = templates.size


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var text = itemView.findViewById<TextView>(R.id.textViewId)

        fun onBind(template: Template, listener: (Template) -> Unit) {
            text.text = template.title

            itemView.setOnClickListener {
                listener(template)
            }
        }

    }
}