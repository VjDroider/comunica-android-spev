package edu.campusvirtual.comunica.models.conekta

/**
 * Created by jonathan on 3/1/18.
 */
class Order(
        var id: String,
        var amount: Double,
        var payment_status: String,
        var charges: Charge
)