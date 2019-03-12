package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.interfaces.HistoryInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.history.History
import edu.campusvirtual.comunica.models.history.Response
import retrofit2.Call
import retrofit2.Callback

fun Service.getHistory(context: Context, completion: (ArrayList<History>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    var service = retrofit.create(HistoryInterface::class.java)

    service.getHistory().enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obetener el historial de tus pagos")
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