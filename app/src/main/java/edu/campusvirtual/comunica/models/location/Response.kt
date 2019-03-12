package edu.campusvirtual.comunica.models.location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/28/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var locations: ArrayList<Location>? = null

}