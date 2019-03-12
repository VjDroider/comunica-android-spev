package edu.campusvirtual.comunica.models.inbox

import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/23/18.
 */
class UnreadResponse {

    @SerializedName("data")
    var data: UnreadValue? = null

}

class UnreadResponseCOM(var numeroMensajesSinLeer: Int)

class UnreadValue {
    var value: Int = 0
}