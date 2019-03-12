package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.auth.AuthResponse
import edu.campusvirtual.comunica.models.auth.AuthResponseCOM
import edu.campusvirtual.comunica.models.auth.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import rx.Observable

/**
 * Created by jonathan on 2/13/18.
 */
interface AuthInterface {

    @GET("/api/authenticate/login")
    fun login(
            @Query("username") username: String,
            @Query("password") pass: String,
            @Query("device_token") deviceToken: String,
            @Query("id_Colegio") collegeId: String
    ) : Call<AuthResponse>

    @GET("/modulos/comunica/Authenticate.aspx")
    fun loginCOM(
        @Query("username") username: String,
        @Query("password") pass: String,
        @Query("device_token") deviceToken: String,
        @Query("devicetoken") device_token: String,
        @Query("id_Colegio") collegeId: String
    ): Call<AuthResponseCOM>

    @GET("/api/authenticate/login")
    fun loginProvider(
            @Query("provider") provider: String,
            @Query("access_token") accessToken: String,
            @Query("device_token") deviceToken: String,
            @Query("id_Colegio") collegeId: String
    ) : Call<AuthResponse>

    @PUT("/m/members/updateDeviceToken")
    fun updateDeviceToken(@Body body: Request) : Call<ResponseBody>

}