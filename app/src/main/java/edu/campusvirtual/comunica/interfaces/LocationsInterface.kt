package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.location.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by jonathan on 2/28/18.
 */
interface LocationsInterface {

    @GET("/m/public/locations/{college}")
    fun getLocations(@Path("college") collegeId: Int) : Call<Response>

    @GET("/m/public/geofence/{college}")
    fun getGeofences(@Path("college") collegeId: Int) : Call<Response>
}