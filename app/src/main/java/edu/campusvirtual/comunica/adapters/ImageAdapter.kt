package edu.campusvirtual.comunica.adapters

import android.content.Context
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.models.inbox.Inbox
import edu.campusvirtual.comunica.R
import java.io.File
import java.net.URL
import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream


class ImageAdapter(var context: Context, val images: ArrayList<String>, val listener: (Int) -> Unit): RecyclerView.Adapter<ImageAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)

        return ImageAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(context, images[position], listener) }

    override fun getItemCount(): Int = images.size

    fun deleteItem(pos: Int) {
        images.removeAt(pos)
        notifyItemRemoved(pos)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.textViewTitle)
        val image = itemView.findViewById<ImageView>(R.id.imageViewImage)

        fun bind(context: Context, item: String, listener: (Int) -> Unit) {

            try {
                var ext = item.substring(item.lastIndexOf("."));
                if(ext == ".mp4") {
                    title.text = "video numero " + (adapterPosition + 1).toString()
                    var thumb = ThumbnailUtils.createVideoThumbnail(item, MediaStore.Video.Thumbnails.MICRO_KIND)
                    Picasso.with(context).load(getImageUri(context, thumb)).fit().into(image)
                } else {
                    title.text = "imagen numero " + (adapterPosition + 1).toString()
                    Picasso.with(context).load(item).fit().into(image)
                }

            } catch (e: Exception) {

            }

            itemView.setOnClickListener { listener(adapterPosition) }
        }

        fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
            return Uri.parse(path)
        }
    }

}