package edu.campusvirtual.comunica.models.template

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/23/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var templates: ArrayList<Template>? = null
}