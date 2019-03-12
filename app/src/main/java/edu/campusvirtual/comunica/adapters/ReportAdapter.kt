package edu.campusvirtual.comunica.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.models.calendar.EventCOM
import edu.campusvirtual.comunica.models.inbox.Report
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ReportAdapter(val inboxes: ArrayList<Report>, val listener: (Report) -> Unit): RecyclerView.Adapter<ReportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cell_report, parent, false)

        return ReportAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(inboxes[position], listener) }

    override fun getItemCount(): Int = inboxes.size


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val hour = itemView.findViewById<TextView>(R.id.reportHourId)
        val title = itemView.findViewById<TextView>(R.id.ReportTemaId)
        val imageBell = itemView.findViewById<ImageView>(R.id.reportImageBellId)
        val imageEmail = itemView.findViewById<ImageView>(R.id.reportImageEmailId)
        val imageError = itemView.findViewById<ImageView>(R.id.reportImageErrorId)
        val bellText = itemView.findViewById<TextView>(R.id.reportTextBellId)
        val emailText = itemView.findViewById<TextView>(R.id.reportTextEmailId)
        val errorText = itemView.findViewById<TextView>(R.id.reportTextErrorId)

        fun bind(item: Report, listener: (Report) -> Unit) {
            title.text = item._Tema
            hour.text = getHourString(getDate(item._Fecha))

            emailText.text = item._iContador_Email_Delivered.toString() + "/" + item._iContador_Email.toString()
            if(item._iContador_Email == item._iContador_Email_Delivered) {
                imageEmail.setImageResource(R.drawable.ic_circle_email)
            } else {
                imageEmail.setImageResource(R.drawable.ic_circle_email_black)
            }

            bellText.text = item._iContador_Mobile_Delivered.toString() + "/" + item._iContador_Mobile.toString()
            if(item._iContador_Mobile == item._iContador_Mobile_Delivered) {
                imageBell.setImageResource(R.drawable.ic_circle_bell)
            } else {
                imageBell.setImageResource(R.drawable.ic_circle_bell_black)
            }
            errorText.text = item._iError.toString()
            itemView.setOnClickListener { listener(item) }
        }

        fun getDate(date: String) : Date? {
            val locale = Locale("es", "mx")
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale)

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

        @SuppressLint("SimpleDateFormat")
        fun getHourString(date: Date?) : String? {
            val locale = Locale("es", "mx")
            val format = SimpleDateFormat("HH:mm", locale)

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
