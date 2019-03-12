package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Log
import edu.campusvirtual.comunica.interfaces.ProfileInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.models.profile.Request
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback


fun Service.updateProfile(context: Context, email: String, phone: String, password: String = "", completion: () -> Unit, failure: () -> Unit) {
    val sessionManager = SessionManager(context)
    var retrofit = Service.prepare(context)
    var service = retrofit.create(ProfileInterface::class.java)

    var request = Request()
    request.email = email
    request.phone = phone
    request.password = password

    service.updateProfile(request).enqueue(object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                val token = response?.headers()?.get("Token")

                sessionManager.updateAccessToken(token!!)
                sessionManager.updateEmail(email)
                sessionManager.updatePhone(phone)
                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al actualizar tu perfil, intentalo nuevamente")
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
