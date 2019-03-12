package edu.campusvirtual.comunica.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.inbox.Message
import edu.campusvirtual.comunica.models.inbox.MessageCOM
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.decorators.StickyHeaderItem
import edu.campusvirtual.comunica.models.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by jonathan on 2/20/18.
 */
const val TYPE_HEADER = 0
const val TYPE_ITEM = 1

class MessageAdapter(
        var messages: ArrayList<MessageCOM>,
        var context: Context,
        var mailbox: String,
        var listener: (item: MessageCOM) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderItem.StickyHeaderInterface {

    private var mSectionPositions: ArrayList<Int>? = null

   /* override fun getSections(): Array<String> {
        var sections:ArrayList<String> = arrayListOf()

        for(i in 0..messages.size - 1) {
            val msg = messages.get(i)

            if(!sections.contains(msg.Tema.toUpperCase())) {
                sections.add(msg.Tema.toUpperCase())
                mSectionPositions?.add(i)
            }
        }

        var array = arrayOfNulls<String>(sections.size)
        return sections.toArray(array)
    }

    override fun getSectionForPosition(p0: Int): Int {
        return mSectionPositions?.get(p0)!!
    }

    override fun getPositionForSection(p0: Int): Int {
        return mSectionPositions?.get(p0)!!
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == TYPE_HEADER) {
            ViewHolderHeader(LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_header, parent, false))
        } else {
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_cell, parent, false), context)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder) {
            holder.bind(messages.get(position), mailbox, listener)
        } else if(holder is ViewHolderHeader) {
            holder.bind(messages.get(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(messages[position].header) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    fun removeAt(position: Int) {
        messages.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View, var context: Context): RecyclerView.ViewHolder(itemView) {
        var statusImge = itemView.findViewById<ImageView>(R.id.statusImageId)
        var receiverName = itemView.findViewById<TextView>(R.id.receiverTextId)
        var subject = itemView.findViewById<TextView>(R.id.subjectTextId)
        var date = itemView.findViewById<TextView>(R.id.dateTextId)

        fun bind(item: MessageCOM, mailbox: String, listener: (item: MessageCOM) -> Unit) {
            statusImge.setImageDrawable(getDrawablePerStatus(item))
            if(mailbox == "Enviados") {
                receiverName.text = item.receiver_name
            } else {
                if(item.transmitter_name == null) {
                    receiverName.text = Constants.defaultTransmitter
                } else {
                    receiverName.text = item.transmitter_name
                }

            }

            subject.text = item.Tema
            date.text = getDateString(getDate(item.Fecha_Registro))

            itemView.setOnClickListener { listener(item) }
        }

        fun getDrawablePerStatus(message: MessageCOM) : Drawable {
            Log.d("MESSAGE", message.Tema + " - " + message.Leido + " - " + message.acusado + " - " + message.Enviado)
            if(message.Leido) {
                return ContextCompat.getDrawable(context, R.drawable.ic_icon_mviewed)!!
            } else if(message.Enviado) {
                return ContextCompat.getDrawable(context, R.drawable.ic_icon_mdelivered)!!
            } else if(message.acusado) {
                return ContextCompat.getDrawable(context, R.drawable.ic_icon_msent)!!
            } else {
                return ContextCompat.getDrawable(context, R.drawable.ic_icon_mnsent)!!
            }
        }

        fun getDate(date: String) : Date? {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

            try {
                return format.parse(date)
            } catch(e: ParseException) {
                Log.d("ERROR", "error: " + e.message)
                return null
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getDateString(date: Date?) : String? {
            val locale = Locale("es", "mx")
            val format = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", locale)

            try {
                var dateTime = format.format(date)

                return dateTime
            } catch(e: Throwable) {
                Log.d("ERROR", "message: " + e.message)
                return null
            }
        }
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = 0
        var position = itemPosition
        do {
            if (this.isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.message_header
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val year = header.findViewById<TextView>(R.id.headerTitle)

        year.text = messages.get(headerPosition).Tema


    }

    override fun isHeader(itemPosition: Int): Boolean {
        return messages.get(itemPosition).header
    }

    class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {
        val year = itemView.findViewById<TextView>(R.id.headerTitle)


        fun bind(item: MessageCOM) {

            year.text = item.Tema


        }
    }
}