package edu.campusvirtual.comunica.interfaces

import edu.campusvirtual.comunica.models.paymentMethod.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PaymentMethodInterface {

    @GET("/m/public/paymentmethods/{college}")
    fun getPaymentMethods(@Path("college") collegeId: Int): Call<Response>

}
