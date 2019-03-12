package edu.campusvirtual.comunica.models.file

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/26/18.
 */
class Response {

    @SerializedName("Photos")
    @Expose
    val photos: ArrayList<File>? = null

}
