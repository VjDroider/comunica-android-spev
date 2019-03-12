package edu.campusvirtual.comunica.models.receiver

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/27/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var data: ArrayList<Receiver>? = null

}

class ResponseCOM(var id_Lista_Distribucion: Int, var Nombre: String)