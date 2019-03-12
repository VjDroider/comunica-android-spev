package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.SlideInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.slides.Response
import edu.campusvirtual.comunica.models.slides.Slide
import retrofit2.Call
import retrofit2.Callback

fun Service.getSlides(context: Context, completion: (ArrayList<Slide>?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create<SlideInterface>(SlideInterface::class.java)

    service.getSlides(Constants.collegeId).enqueue(object: Callback<Response> {
        override fun onFailure(call: Call<Response>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response?.body()

                completion(body.elements)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al obetener los slides")
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