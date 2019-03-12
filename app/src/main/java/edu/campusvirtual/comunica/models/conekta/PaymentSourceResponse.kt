package edu.campusvirtual.comunica.models.conekta

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 3/6/18.
 */
class PaymentSourceResponse {

    @SerializedName("data")
    @Expose
    var data : ArrayList<PaymentSource>? = null

}