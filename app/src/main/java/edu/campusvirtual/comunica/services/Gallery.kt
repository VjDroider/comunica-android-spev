package edu.campusvirtual.comunica.services

import android.content.Context
import edu.campusvirtual.comunica.interfaces.GalleryInterface
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.gallery.Gallery
import edu.campusvirtual.comunica.models.gallery.Response
import retrofit2.Call
import retrofit2.Callback

fun Service.getGallery(context: Context, completion: (Gallery?) -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(GalleryInterface::class.java)

    service.getGallery().enqueue(object: Callback<Response> {
        override fun onResponse(call: Call<Response>?, response: retrofit2.Response<Response>?) {
            if(response?.isSuccessful!!) {
                val body = response.body()

                completion(body.data)
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "No se pudo obtener tu galeria")
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
