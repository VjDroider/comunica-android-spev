package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.kardex.Response
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by jonathan on 2/28/18.
 */
interface KardexInterface {

    @GET("/m/kardex")
    fun getKardex() : Call<Response>

}
