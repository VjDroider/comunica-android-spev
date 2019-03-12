package edu.campusvirtual.comunica.models.configuration

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/16/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var config: ArrayList<Configuration>? = null

}