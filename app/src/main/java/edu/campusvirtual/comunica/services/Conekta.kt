package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Log
import edu.campusvirtual.comunica.interfaces.ConektaInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.conekta.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

fun Service.createOrder(context: Context, accountId: Int, amount: Double, type: String, payment_source_id: String?, completion: (Order?) -> Unit, failure: () -> Unit) {
    val sessionManager = SessionManager(context)
    val retrofit = Service.prepare(context)
    val service = retrofit.create(ConektaInterface::class.java)

    val body = Request()
    body.accountId = accountId
    body.amount = amount
    body.personId = sessionManager.getId()
    body.type = type

    if(payment_source_id != null) {
        body.payment_source_id = payment_source_id
    }

    service.generateOrder(body).enqueue(object: Callback<Response> {
        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            Log.d("FAILL", "FAAA " + t?.message)
            failure()
        }

        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            Log.d("SUCE", "ESS")
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo generar la orden, vuelve a intentarlo más tarde")
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

fun Service.getPaymentSources(context: Context, completion: (ArrayList<PaymentSource>?) -> Unit, failure: () -> Unit) {
    var retrodit = Service.prepare(context)
    val service = retrodit.create(ConektaInterface::class.java)

    service.getPaymentSources().enqueue(object: Callback<PaymentSourceResponse> {
        override fun onFailure(call: Call<PaymentSourceResponse>?, t: Throwable?) {
            Log.d("si", "no " + t?.message)
            failure()
        }

        override fun onResponse(call: Call<PaymentSourceResponse>?, response: retrofit2.Response<PaymentSourceResponse>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obetener tus tarjetas")
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

fun Service.saveCard(context: Context, token: String, completion: (PaymentSource?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(ConektaInterface::class.java)

    val body = CardRequest()
    body.token = token

    service.saveCard(body).enqueue(object: Callback<CardResponse> {
        override fun onFailure(call: Call<CardResponse>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<CardResponse>?, response: retrofit2.Response<CardResponse>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.card)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo guardar la tarjeta")
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

fun Service.deleteCard(context: Context, id: String, completion: () -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    var service = retrofit.create(ConektaInterface::class.java)

    service.deleteCard(id).enqueue(object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo eliminar la tarjeta")
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
