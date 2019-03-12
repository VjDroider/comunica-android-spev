package edu.campusvirtual.comunica.models.configuration

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 3/6/18.
 */
class PersonalResponse {

    @SerializedName("data")
    @Expose
    var data: ArrayList<PersonalConfiguration>? = null

}