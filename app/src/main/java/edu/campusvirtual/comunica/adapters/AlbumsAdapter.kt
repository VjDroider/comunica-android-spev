package edu.campusvirtual.comunica.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.models.gallery.Album
import edu.campusvirtual.comunica.models.gallery.Photo
import edu.campusvirtual.comunica.R

class AlbumsAdapter(val context: Context, val albums: ArrayList<Album>, val listener: (Photo, Int, Int, Boolean) -> Unit) : SectionedRecyclerViewAdapter<AlbumsAdapter.MainVH>() {

    override fun onBindViewHolder(holder: MainVH, section: Int, relativePosition: Int, absolutePosition: Int) {
        // holder?.title?.setText(String.format("S:%d, P:%d, A:%d", section, relativePosition, absolutePosition))
        holder.onBind(context, section, albums.get(section).photos.get(relativePosition), listener)
    }

    override fun onBindHeaderViewHolder(holder: MainVH?, section: Int, expanded: Boolean) {
        holder?.title?.setText(albums.get(section).name)
        holder?.description?.setText(albums.get(section).description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainVH {
        val layout: Int
        when (viewType) {
            SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER -> layout = R.layout.list_item_header
            SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM -> layout = R.layout.cell_photo
            SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER -> layout = R.layout.list_item_footer
            else ->
                // Our custom item, which is the 0 returned in getItemViewType() above
                layout = R.layout.list_item_main_bold
        }

        val v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false)
        return MainVH(v, this)
    }

    override fun onBindFooterViewHolder(holder: MainVH?, section: Int) {
        // holder?.title?.setText(String.format("Section footer %d", section));
    }

    override fun getSectionCount(): Int {
        return albums.size
    }

    override fun getItemCount(section: Int): Int {
        return albums.get(section).photos.size
    }

    class MainVH(itemView: View, val adapter: AlbumsAdapter) : SectionedViewHolder(itemView) {

        var imageView: ImageView? = itemView.findViewById<ImageView>(R.id.imgId)
        var title: TextView? = itemView.findViewById<TextView>(R.id.title)
        var description: TextView? = itemView.findViewById<TextView>(R.id.description)

        fun onBind(context: Context, section: Int, item: Photo, listener: (Photo, Int, Int, Boolean) -> Unit) {
            Picasso.with(context).load(item.url).into(imageView)

            itemView.setOnClickListener {
                if (isFooter) {
                    // ignore footer clicks
                }

                if (isHeader) {
                    // adapter.toggleSectionExpanded(relativePosition.section())
                }

                listener(item, relativePosition.section(), relativePosition.relativePos(), false)


            }

            itemView.setOnLongClickListener {

                listener(item, relativePosition.section(), relativePosition.relativePos(), true)
                true
            }
        }
    }
}