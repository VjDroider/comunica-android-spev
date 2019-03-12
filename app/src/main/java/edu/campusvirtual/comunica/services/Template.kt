package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.interfaces.TemplateInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.template.Response
import edu.campusvirtual.comunica.models.template.Template
import retrofit2.Call
import retrofit2.Callback


fun Service.getTemplates(context: Context, completion: (ArrayList<Template>?) -> Unit, failure: () -> Unit) {
    var retrofit = Service.prepare(context)
    var service = retrofit.create(TemplateInterface::class.java)

    service.getTemplates().enqueue(object: Callback<Response> {
        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.templates)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al obetenr los templates")
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