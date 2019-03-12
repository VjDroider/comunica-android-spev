package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Log
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.GeofenceInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by jonathan on 3/12/18.
 */
fun Service.geofenceNotify(context: Context, geofenceId: String, geoId: String, completion: () -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(GeofenceInterface::class.java)

    service.geofence(geofenceId, geoId).enqueue(object: Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        // Util.showAlert(context, "Error", "No se pudo enviar la geootificacion")
                    }
                    Service.serverError -> {
                        // Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

    })
}

fun Service.geofenceNotifyCOM(context: Context, transition: Int, geoId: Int, name: String, completion: () -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(GeofenceInterface::class.java)
    var session = SessionManager(context)

    service.geofenceCOM(Constants.collegeId, session.getEmail()!!, session.getDeviceToken()!!, transition, geoId, name).enqueue(object: Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        // Util.showAlert(context, "Error", "No se pudo enviar la geootificacion")
                    }
                    Service.serverError -> {
                        // Util.showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }
        }

        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

    })
}