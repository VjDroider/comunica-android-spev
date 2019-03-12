package edu.campusvirtual.comunica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.campusvirtual.comunica.models.account.Account
import edu.campusvirtual.comunica.R

/**
 * Created by jonathan on 2/28/18.
 */
class AccountAdapter(val elements: ArrayList<Account>, val listener: (Account) -> Unit): RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cell_account, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(elements.get(position), listener)
    }


    override fun getItemCount(): Int = elements.size

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var accountName = itemView.findViewById<TextView>(R.id.textViewConceptId)
        var accountBalance = itemView.findViewById<Button>(R.id.textViewbalanceId)

        fun onBind(item: Account, listener: (Account) -> Unit) {

            accountName.setText(item.name)
            accountBalance.setText(item.balance.toString())
            itemView.setOnClickListener { listener(item) }

        }
    }
}