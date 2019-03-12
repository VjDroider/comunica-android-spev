package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.interfaces.KardexInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.kardex.Kardex
import edu.campusvirtual.comunica.models.kardex.Response
import retrofit2.Call
import retrofit2.Callback

fun Service.getKardex(context: Context, completion: (ArrayList<Kardex>?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(KardexInterface::class.java)

    service.getKardex().enqueue(object: Callback<Response> {
        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.elements)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Hubo un error al obtener tu kardex, vuelve a intentarlo mas tarde")
                    }
                    Service.serverError -> {
                        Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

    })
}
