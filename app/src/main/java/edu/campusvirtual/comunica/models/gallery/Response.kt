package edu.campusvirtual.comunica.models.gallery

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 3/2/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var data: Gallery? = null

}