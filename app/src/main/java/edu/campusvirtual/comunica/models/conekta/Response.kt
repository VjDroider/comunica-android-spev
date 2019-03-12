package edu.campusvirtual.comunica.models.conekta

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 3/1/18.
 */
class Response {
    @SerializedName("data")
    @Expose
    var data: Order? = null
}