package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.profile.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by jonathan on 2/20/18.
 */
interface ProfileInterface {

    @Headers("Content-Type: application/json")
    @PUT(("/m/members/updateprofile"))
    fun updateProfile(@Body body: Request) : Call<ResponseBody>

}