package edu.campusvirtual.comunica.models.inbox

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by jonathan on 2/20/18.
 */
class Response {

    @SerializedName("data")
    @Expose
    var list: ArrayList<Message>? = null

}

class ResponseReportCOM {

    @SerializedName("data")
    @Expose
    var list: ArrayList<Report>? = null

}


class ResponseDetailReport {
    @SerializedName("data")
    @Expose
    var list: ResponseReport? = null
}

class ResponseReport(var _Lista_Status: ArrayList<SingleReport>)

class ResponseSend(var success: String)