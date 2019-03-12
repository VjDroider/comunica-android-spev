package edu.campusvirtual.comunica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.models.MoreItem

/**
 * Created by jonathan on 2/16/18.
 */
class MoreAdapter(val elements: ArrayList<MoreItem>, val listener: (MoreItem) -> Unit) : RecyclerView.Adapter<MoreAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return MoreAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_simple_icon, parent, false))
    }

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(elements.get(position), listener)
    }

    fun reload() {
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val leftIcon: ImageView = view.findViewById(R.id.leftIconId)
        var textDescription: TextView = view.findViewById(R.id.singleTextId)

        fun onBind(item: MoreItem, listener: (MoreItem) -> Unit) {
            leftIcon.setImageDrawable(item.leftIcon)
            textDescription.setText(item.text)

            itemView.setOnClickListener {
                listener(item)
            }
        }
    }

}