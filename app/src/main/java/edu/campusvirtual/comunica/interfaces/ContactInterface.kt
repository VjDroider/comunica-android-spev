package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.contact.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by jonathan on 2/15/18.
 */
interface ContactInterface {

    @GET("/m/public/contact/{id}")
    fun getContact(@Path("id") collegeId: Int) : Call<Response>

}