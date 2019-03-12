package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.ReceiverInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.receiver.Receiver
import edu.campusvirtual.comunica.models.receiver.ReceiverCOM
import edu.campusvirtual.comunica.models.receiver.Response
import edu.campusvirtual.comunica.models.receiver.ResponseCOM
import retrofit2.Call
import retrofit2.Callback


fun Service.getReceivers(context: Context, completion: (ArrayList<Receiver>?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(ReceiverInterface::class.java)

    service.getReceivers().enqueue(object: Callback<Response> {
        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al obtener tus destinatarios disponibles")
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

fun Service.getReceiversCOM(context: Context, completion: (ArrayList<ReceiverCOM>?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(ReceiverInterface::class.java)
    var session = SessionManager(context)

    service.getReceiversCOM(session.getId(), Constants.collegeId, session.getEmail()!!).enqueue(object: Callback<ArrayList<ReceiverCOM>> {
        override fun onFailure(call: Call<ArrayList<ReceiverCOM>>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ArrayList<ReceiverCOM>>?, response: retrofit2.Response<ArrayList<ReceiverCOM>>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al obtener tus destinatarios disponibles")
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
