package edu.campusvirtual.comunica.models.slides

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/15/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var elements: ArrayList<Slide>? = null

}