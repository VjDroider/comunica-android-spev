package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Log
import edu.campusvirtual.comunica.interfaces.AccountInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.account.Account
import edu.campusvirtual.comunica.models.account.Response
import retrofit2.Call
import retrofit2.Callback

fun Service.getAccounts(context: Context, completion: (ArrayList<Account>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(AccountInterface::class.java)

    service.getAccounts().enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data?.accounts!!)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al obtener las cuentas")
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