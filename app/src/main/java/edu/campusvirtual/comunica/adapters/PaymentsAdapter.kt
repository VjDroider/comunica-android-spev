package edu.campusvirtual.comunica.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.account.Account
import edu.campusvirtual.comunica.models.paymentMethod.PaymentMethod
import edu.campusvirtual.comunica.R

/**
 * Created by jonathan on 3/1/18.
 */
class PaymentsAdapter(var elements: ArrayList<PaymentMethod>, val listener: (PaymentMethod) -> Unit): RecyclerView.Adapter<PaymentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_payment_method, parent, false))

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(elements.get(position), listener)
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        val context = itemView.context
        val titlePayment = itemView.findViewById<TextView>(R.id.textViewTitlePaymentId)
        val descriptionPayment = itemView.findViewById<TextView>(R.id.textViewPaymentDescriptionId)
        val image = itemView.findViewById<ImageView>(R.id.imageViewPaymentId)

        fun onBind(item: PaymentMethod, listener: (PaymentMethod) -> Unit) {

            titlePayment.text = getType(item.type)
            descriptionPayment.text = getDescription(item.type)
            image.setImageResource(getDrawable(item.type)!!)

            itemView.setOnClickListener { listener(item) }

        }

        fun getDrawable(type: String) : Int? {
            when(type) {
                "card" -> {
                    return context.resources.getIdentifier("visamastercard", "drawable", context.packageName)
                }
                "oxxo" -> {
                    return context.resources.getIdentifier("oxxo_pay", "drawable", context.packageName)
                }
                "spei" -> {
                    return context.resources.getIdentifier("spei_brand", "drawable", context.packageName)
                }
                else -> {
                    return null
                }
            }
        }

        fun getType(type: String): String? {
            when(type) {
                "card" -> {
                    return "VISA | MASTERCARD | AMEX"
                }
                "oxxo" -> {
                    return "OXXO | PAY"
                }
                "spei" -> {
                    return "SPEI"
                }
                else -> {
                    return null
                }
            }
        }

        fun getDescription(type: String): String? {
            when(type) {
                "card" -> {
                    return "Pago seguro con tarjeta"
                }
                "oxxo" -> {
                    return "Pagos con oxxo pay"
                }
                "spei" -> {
                    return "Pago por transferencia"
                }
                else -> {
                    return null
                }
            }
        }
    }
}