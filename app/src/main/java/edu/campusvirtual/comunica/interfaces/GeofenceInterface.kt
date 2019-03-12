package edu.campusvirtual.comunica.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by jonathan on 3/12/18.
 */
interface GeofenceInterface {
    @POST("/m/messages/geofence")
    fun geofence(@Query("geofenceId") geofenceId: String, @Query("geoId") geoId: String) : Call<ResponseBody>

    @GET("/Modulos/Comunica/registraGeoNotificacion.aspx")
    fun geofenceCOM(
            @Query("id_Colegio") id_Colegio: Int,
            @Query("userName") userName: String,
            @Query("deviceToken") deviceToken: String,
            @Query("transitionType") transitionType: Int,
            @Query("geoId") geoId: Int,
            @Query("name") name: String
    ) : Call<ResponseBody>
}
