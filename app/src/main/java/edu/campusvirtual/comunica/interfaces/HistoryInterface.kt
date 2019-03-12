package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.history.Response
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by jonathan on 3/1/18.
 */
interface HistoryInterface {

    @GET("/m/accounts/paymenthistory")
    fun getHistory() : Call<Response>

}