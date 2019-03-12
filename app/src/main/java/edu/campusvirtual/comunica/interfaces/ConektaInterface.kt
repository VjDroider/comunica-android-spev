package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.conekta.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by jonathan on 3/1/18.
 */
interface ConektaInterface {

    @Headers("Content-Type: application/json")
    @POST(("/m/conekta/orders"))
    fun generateOrder(@Body body: Request) : Call<Response>

    @GET("/m/conekta/paymentsources")
    fun getPaymentSources() : Call<PaymentSourceResponse>

    @Headers("Content-Type: application/json")
    @POST(("/m/conekta/paymentsources"))
    fun saveCard(@Body body: CardRequest) : Call<CardResponse>

    @DELETE("/m/conekta/paymentsources/{sourceId}")
    fun deleteCard(@Path("sourceId") id: String) : Call<ResponseBody>

}