package edu.campusvirtual.comunica.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.history.History
import edu.campusvirtual.comunica.models.kardex.Kardex
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.models.inbox.SingleReport
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jonathan on 3/1/18.
 */
class ReportDetailAdapter(val elements: ArrayList<SingleReport>): RecyclerView.Adapter<ReportDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_report_detail, parent, false))

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(elements.get(position))
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var imageType = itemView.findViewById<ImageView>(R.id.imageTypeId)
        var who = itemView.findViewById<TextView>(R.id.textViewWhoId)
        var imageError = itemView.findViewById<ImageView>(R.id.imageErrorId)
        var labelError = itemView.findViewById<TextView>(R.id.textViewMsgErrorId)
        var imageRead = itemView.findViewById<ImageView>(R.id.imageViewRead)
        var imageSent = itemView.findViewById<ImageView>(R.id.imageViewSent)

        fun onBind(item: SingleReport) {

            if(item.id_Canal_Comunicacion == 1) {
                imageType.setImageResource(R.drawable.ic_vector_circle_email)
            } else {
                imageType.setImageResource(R.drawable.ic_vector_circle_bell)
            }

            who.text = item.ToId_Name
            labelError.text = getStatus(item)

            if(item.sError.isEmpty()) {
                imageError.visibility = View.GONE
            } else {
                imageError.visibility = View.VISIBLE
            }

            if(!item.Enviado) {
                imageSent.visibility = View.GONE
            } else {
                imageSent.visibility = View.VISIBLE
            }

            if(!item.Leido) {
                imageRead.visibility = View.GONE
            } else {
                imageRead.visibility = View.VISIBLE
            }

        }

        fun getStatus(report: SingleReport): String{
            if(!report.sError.isEmpty()) return report.sError
            else if(report.Leido) return "Estatus: Leido"
            else if(report.Enviado) return "Estatus: Enviado"
            return "Estatus: Sin enviar"
        }

        fun getDate(date: String) : Date? {
            val format = SimpleDateFormat("yyyy/MM/dd")

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
            } catch(e: ParseException) {
                Log.d("ERROR", "message: " + e.message)
                return null
            }
        }
    }
}