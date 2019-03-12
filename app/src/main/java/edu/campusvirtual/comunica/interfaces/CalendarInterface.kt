package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.calendar.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by jonathan on 3/1/18.
 */
interface CalendarInterface {

    @GET("/m/events/{id}")
    fun getEvents(@Path("id") collegeId: Int) : Call<Response>

    @POST("/m/events/confirm")
    fun confirmAssistance(@Query("eventId") eventId: String) : Call<ResponseBody>

}