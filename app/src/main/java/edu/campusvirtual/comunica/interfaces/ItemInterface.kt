package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.item.Response
import edu.campusvirtual.comunica.models.item.ResponseCOM
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by jonathan on 2/16/18.
 */
interface ItemInterface {

    @GET("/m/items/{id}")
    fun getItems(@Path("id") collegeId: Int) : Call<Response>

    @GET("/modulos/comunica/getInfoPublica.aspx")
    fun getItemsCOM(@Query("id_Colegio") collegeId: Int, @Query("lastmodified") last: String = "2010-01-01T00:00:00.000") : Call<ResponseCOM>

}