package edu.campusvirtual.comunica.models.configuration

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 3/7/18.
 */
class SingleResponse {
    @SerializedName("data")
    @Expose
    var data: Configuration? = null
}

class SingleResponseCOM {

    @SerializedName("items")
    @Expose
    var items: ArrayList<Configuration>? = null

}