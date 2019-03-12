package edu.campusvirtual.comunica.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.history.History
import edu.campusvirtual.comunica.models.kardex.Kardex
import edu.campusvirtual.comunica.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jonathan on 3/1/18.
 */
class HistoryAdapter(val elements: ArrayList<History>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_history, parent, false))

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(elements.get(position))
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var concept = itemView.findViewById<TextView>(R.id.textViewConceptId)
        var payment = itemView.findViewById<TextView>(R.id.textViewPaymentMethodId)
        var amount = itemView.findViewById<TextView>(R.id.textViewMontoId)
        var date = itemView.findViewById<TextView>(R.id.textViewDateId)

        fun onBind(item: History) {

            concept.setText("concepto: " + item.concept)
            payment.setText("Metodo de pago: " + item.way_to_pay)
            amount.setText("$ " + item.amount.toString())
            date.setText(getDateString(getDate(item.date)))

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