package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.receiver.ReceiverCOM
import edu.campusvirtual.comunica.models.receiver.Response
import edu.campusvirtual.comunica.models.receiver.ResponseCOM
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by jonathan on 2/27/18.
 */
interface ReceiverInterface {

    @GET("/m/recipients")
    fun getReceivers() : Call<Response>

    @GET("/Modulos/Comunica/getListasDistribucion.aspx")
    fun getReceiversCOM(
            @Query("id_Persona") id_Persona: Int,
            @Query("id_Colegio") id_Colegio: Int,
            @Query("userName") userName: String
    ) : Call<ArrayList<ReceiverCOM>>

}