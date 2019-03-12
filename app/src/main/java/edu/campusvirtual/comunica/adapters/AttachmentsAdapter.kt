package edu.campusvirtual.comunica.adapters

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.models.attachment.Attachment
import edu.campusvirtual.comunica.models.gallery.Photo
import edu.campusvirtual.comunica.R
import java.io.ByteArrayOutputStream
import java.net.URL
import android.media.MediaMetadataRetriever
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.library.Util
import java.io.File


class AttachmentsAdapter(val context: Context, val elements: ArrayList<Attachment>, val listener: (Attachment, Int, Boolean) -> Unit): RecyclerView.Adapter<AttachmentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attachment_cell, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.onBind(context, elements.get(position), listener) }

    override fun getItemCount(): Int = elements.size

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var imageView: ImageView = v.findViewById<ImageView>(R.id.imgId)
        var imageViewPlay: ImageView = v.findViewById<ImageView>(R.id.imageViewPlay)
        var imageViewCheck: ImageView = v.findViewById<ImageView>(R.id.imageViewCheck)

        fun onBind(context: Context, item: Attachment, listener: (Attachment, Int, Boolean) -> Unit) {

            itemView.setOnClickListener { listener(item, adapterPosition, false) }

            if(item.isSelected) {
                imageViewCheck.visibility = View.VISIBLE
            } else {
                imageViewCheck.visibility = View.INVISIBLE
            }

            if(item.isVideo) {
                imageViewPlay.visibility = View.VISIBLE
                Picasso.with(context).load(Uri.parse(item.preview)).into(imageView)
            } else {
                imageViewPlay.visibility = View.INVISIBLE
                Picasso.with(context).load(item.url).into(imageView)
            }

            itemView.setOnLongClickListener {
                val imagen = itemView.findViewById<ImageView>(R.id.imgId)

                imagen.buildDrawingCache()
                // val bitmap = imagen.getDrawingCache()
                listener(item, adapterPosition, true)
                true
            }

        }

        fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
            return Uri.parse(path)
        }
    }

}