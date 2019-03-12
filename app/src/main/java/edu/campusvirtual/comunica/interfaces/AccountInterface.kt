package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.account.Response
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by jonathan on 2/28/18.
 */
interface AccountInterface {

    @GET("/m/accounts")
    fun getAccounts() : Call<Response>
}