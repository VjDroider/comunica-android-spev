package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.account.BodyLog
import edu.campusvirtual.comunica.models.account.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by jonathan on 2/28/18.
 */
interface LogInterface {

    @POST("/api/log")
    fun saveLog(@Body body: BodyLog) : Call<ResponseBody>

}