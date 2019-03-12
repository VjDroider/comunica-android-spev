package edu.campusvirtual.comunica.models.conekta

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 3/7/18.
 */
class CardResponse {

    @SerializedName("data")
    @Expose
    var card : PaymentSource? = null

}