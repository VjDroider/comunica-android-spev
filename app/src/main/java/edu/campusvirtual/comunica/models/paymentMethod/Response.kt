package edu.campusvirtual.comunica.models.paymentMethod

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 3/1/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var data: ArrayList<PaymentMethod>? = null

}