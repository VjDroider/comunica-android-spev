package edu.campusvirtual.comunica.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.models.gallery.Photo
import edu.campusvirtual.comunica.R
import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import java.io.File


/**
 * Created by jonathan on 3/2/18.
 */
class PhotosAdapter(val context: Context, val elements: ArrayList<Photo>, val listener: (Photo, Int, Boolean) -> Unit): RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_photo, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.onBind(context, elements.get(position), listener) }

    override fun getItemCount(): Int = elements.size

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var imageView: ImageView = v.findViewById<ImageView>(R.id.imgId)

        fun onBind(context: Context, item: Photo, listener: (Photo, Int, Boolean) -> Unit) {

            Picasso.with(context).load(item.url).into(imageView)

            itemView.setOnClickListener { listener(item, adapterPosition, false) }
            itemView.setOnLongClickListener {
                val imagen = itemView.findViewById<ImageView>(R.id.imgId)

                imagen.buildDrawingCache()
                // val bitmap = imagen.getDrawingCache()
                listener(item, adapterPosition, true)
                true
            }

        }
    }
}