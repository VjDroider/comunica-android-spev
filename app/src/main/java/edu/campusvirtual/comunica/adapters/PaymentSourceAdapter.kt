package edu.campusvirtual.comunica.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.campusvirtual.comunica.models.conekta.PaymentSource
import edu.campusvirtual.comunica.models.gallery.Photo
import edu.campusvirtual.comunica.models.paymentMethod.PaymentMethod
import edu.campusvirtual.comunica.R

/**
 * Created by jonathan on 3/6/18.
 */
class PaymentSourceAdapter(val elements: ArrayList<PaymentSource>, val listener: (PaymentSource) -> Unit): RecyclerView.Adapter<PaymentSourceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_payment_source, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.onBind(elements.get(position), listener) }

    override fun getItemCount(): Int = elements.size

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        val context = itemView.context
        val titlePayment = itemView.findViewById<TextView>(R.id.textViewTitlePaymentId)
        val descriptionPayment = itemView.findViewById<TextView>(R.id.textViewPaymentDescriptionId)
        val image = itemView.findViewById<ImageView>(R.id.imageViewPaymentId)

        fun onBind(item: PaymentSource, listener: (PaymentSource) -> Unit) {

            titlePayment.text = "**** **** ****" + item.last4
            descriptionPayment.text = item.exp_month + "/" + item.exp_year + "    " + item.name
            image.setImageResource(getDrawable(item.brand)!!)

            itemView.setOnClickListener { listener(item) }

        }

        fun getDrawable(type: String) : Int? {
            when(type.toLowerCase()) {
                "visa" -> {
                    return context.resources.getIdentifier("visa", "drawable", context.packageName)
                }
                "mastercard", "mc" -> {
                    return context.resources.getIdentifier("mastercard", "drawable", context.packageName)
                }
                "amex" -> {
                    return context.resources.getIdentifier("amex", "drawable", context.packageName)
                }
                else -> {
                    return context.resources.getIdentifier("visa", "drawable", context.packageName)
                }
            }
        }


    }

}