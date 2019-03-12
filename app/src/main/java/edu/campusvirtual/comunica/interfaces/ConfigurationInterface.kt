package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.configuration.*
import edu.campusvirtual.comunica.models.profile.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by jonathan on 2/16/18.
 */
interface ConfigurationInterface {

    @GET("/m/configuration/{id}")
    fun getConfigs(@Path("id") collegeId: Int, @Query("names") forKeys: String) : Call<Response>

    @GET("/m/personalConfig")
    fun getPersonalConfigs() : Call<PersonalResponse>

    @Headers("Content-Type: application/json")
    @POST(("/m/personalConfig"))
    fun updatePersonalConfig(@Body body: PersonalRequest) : Call<PersonalConfiguration>

    @GET("/m/configuration/{college}")
    fun getConfig(@Path("college") collegeId: Int, @Query("name") name: String) : Call<SingleResponse>

    // For comunica
    @GET("/modulos/comunica/getGlobalConfig.aspx")
    fun getAllConfigs(@Query("id_Colegio") college: Int) : Call<SingleResponseCOM>


}