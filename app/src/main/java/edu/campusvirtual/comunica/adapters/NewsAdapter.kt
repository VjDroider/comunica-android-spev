package edu.campusvirtual.comunica.adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.item.Item
import edu.campusvirtual.comunica.models.item.ItemCOM
import edu.campusvirtual.comunica.R

class NewsAdapter(val items: List<ItemCOM>, val listener: (ItemCOM) -> Unit) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position), listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cell_default, parent, false)

        return NewsAdapter.ViewHolder(v)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val text = itemView.findViewById<TextView>(R.id.textViewId)

        fun bind(item: ItemCOM, listener: (ItemCOM) -> Unit) {
            text?.text = item.menuItemTexto

            itemView.setOnClickListener { listener(item) }
        }
    }
}
