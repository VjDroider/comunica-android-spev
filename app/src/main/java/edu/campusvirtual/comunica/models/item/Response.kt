package edu.campusvirtual.comunica.models.item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import edu.campusvirtual.comunica.models.banner.BannerCOM
import edu.campusvirtual.comunica.models.calendar.EventCOM

/**
 * Created by jonathan on 2/16/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var elements: ArrayList<Item>? = null
}

class ResponseCOM(var paginas: ArrayList<ItemCOM>, var banners: ArrayList<BannerCOM>, var eventos: ArrayList<EventCOM>)