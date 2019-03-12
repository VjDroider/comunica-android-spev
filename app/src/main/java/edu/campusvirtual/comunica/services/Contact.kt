package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.ContactInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.contact.Contact
import edu.campusvirtual.comunica.models.contact.Response
import retrofit2.Call
import retrofit2.Callback

/**
 * Created by jonathan on 2/15/18.
 */

fun Service.getContactInfo(context: Context, completion: (Contact?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create<ContactInterface>(ContactInterface::class.java)

    service.getContact(Constants.collegeId).enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response?.body()

                completion(body.contact)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener informacion de contacto de la institución")
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
