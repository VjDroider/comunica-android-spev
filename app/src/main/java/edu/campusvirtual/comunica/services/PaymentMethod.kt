package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.PaymentMethodInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.paymentMethod.PaymentMethod
import edu.campusvirtual.comunica.models.paymentMethod.Response
import retrofit2.Call
import retrofit2.Callback

fun Service.getPaymentMethods(context: Context, completion: (ArrayList<PaymentMethod>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(PaymentMethodInterface::class.java)

    service.getPaymentMethods(Constants.collegeId).enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener los metodos de pago disponibles")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

    })
}
