package edu.campusvirtual.comunica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.R
import edu.campusvirtual.comunica.models.conekta.PaymentSource

/**
 * Created by jonathan on 3/7/18.
 */
class PaymentMethodAdapter(val elements: ArrayList<PaymentSource>): RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_payment_source, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.onBind(elements.get(position)) }

    override fun getItemCount(): Int = elements.size

    fun removeAt(position: Int) {
        elements.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        val context = itemView.context
        val titlePayment = itemView.findViewById<TextView>(R.id.textViewTitlePaymentId)
        val descriptionPayment = itemView.findViewById<TextView>(R.id.textViewPaymentDescriptionId)
        val image = itemView.findViewById<ImageView>(R.id.imageViewPaymentId)
        val imageArrow = itemView.findViewById<ImageView>(R.id.imageViewArrow)

        fun onBind(item: PaymentSource) {

            titlePayment.text = "**** **** ****" + item.last4
            descriptionPayment.text = item.exp_month + "/" + item.exp_year + "    " + item.name
            image.setImageResource(getDrawable(item.brand)!!)
            imageArrow.visibility = View.INVISIBLE
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