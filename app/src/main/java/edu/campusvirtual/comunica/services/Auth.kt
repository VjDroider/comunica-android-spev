package edu.campusvirtual.comunica.services

import android.content.Context
import android.util.Base64
import android.util.Log
import edu.campusvirtual.comunica.models.Constants
import edu.campusvirtual.comunica.interfaces.AuthInterface
import edu.campusvirtual.comunica.library.SessionManager
import edu.campusvirtual.comunica.library.Util
import edu.campusvirtual.comunica.library.Util.Companion.showAlert
import edu.campusvirtual.comunica.models.auth.AuthResponse
import edu.campusvirtual.comunica.models.auth.AuthResponseCOM
import edu.campusvirtual.comunica.models.auth.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by jonathan on 2/13/18.
 */

fun Service.login(context: Context, username: String, password: String, deviceToken: String, completion: () -> Unit, failure: () -> Unit) {
    val sessionManager = SessionManager(context)
    val retrofit = Service.prepare(context)
    val service = retrofit.create<AuthInterface>(AuthInterface::class.java)

    service.login(username, android.util.Base64.encodeToString(password.toString().toByteArray(), Base64.DEFAULT), deviceToken, Constants.collegeId.toString()).enqueue(object: Callback<AuthResponse> {
        override fun onFailure(call: Call<AuthResponse>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<AuthResponse>?, response: Response<AuthResponse>?) {
            if(response?.isSuccessful!!) {
                val token = response?.headers()?.get("Token")
                val body = response?.body()

                sessionManager.createSession(body.id!!, body.fullname!!, body.email!!, body.phone!!, token!!)
                completion()
            } else {
                when(response.code()) {
                    400 -> {
                        showAlert(context, "Error", "username o password incorrectos")
                    }
                    500 -> {
                        showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }

        }

    })

}

fun Service.loginCOM(context: Context, username: String, password: String, deviceToken: String, completion: () -> Unit, failure: () -> Unit) {
    val sessionManager = SessionManager(context)
    val retrofit = Service.prepare(context)
    val service = retrofit.create<AuthInterface>(AuthInterface::class.java)

    service.loginCOM(username, password, deviceToken, deviceToken, Constants.collegeId.toString()).enqueue(object: Callback<AuthResponseCOM> {
        override fun onFailure(call: Call<AuthResponseCOM>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<AuthResponseCOM>?, response: Response<AuthResponseCOM>?) {
            if(response?.isSuccessful!!) {
                val body = response?.body()

                if(body.id_persona == null) {
                    showAlert(context, "Error", "username o password incorrectos")
                    failure()
                    return
                }

                sessionManager.createSession(body.id_persona!!, body.fullname!!, body.email!!, "", "")
                completion()
            } else {
                when(response.code()) {
                    400 -> {
                        showAlert(context, "Error", "username o password incorrectos")
                    }
                    500 -> {
                        showAlert(context, "Error", "Hubo un error con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }

        }

    })

}



fun Service.loginWithProvider(context: Context, provider: String, accessToken: String, deviceToken: String, completion: () -> Unit, failure: () -> Unit) {
    val sessionManager = SessionManager(context)
    val retrofit = Service.prepare(context)
    val service = retrofit.create<AuthInterface>(AuthInterface::class.java)

    service.loginProvider(provider, accessToken, deviceToken, Constants.collegeId.toString()).enqueue(object: Callback<AuthResponse> {
        override fun onFailure(call: Call<AuthResponse>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<AuthResponse>?, response: Response<AuthResponse>?) {
            if(response?.isSuccessful!!) {
                val token = response?.headers()?.get("Token")
                val body = response?.body()

                sessionManager.createSession(body.id!!, body.fullname!!, body.email!!, body.phone!!, token!!)
                completion()
            } else {
                when(response.code()) {
                    400 -> {
                        showAlert(context, "Error", "No puedes acceder a esta aplicación")
                    }
                    500 -> {
                        showAlert(context, "Error", "Hubo un erorr con el servidor, vuelve a intentarlo mas tarde")
                    }
                }
                failure()
            }

        }

    })

}

fun Service.updateDeviceToken(context: Context, version: String, deviceToken: String, completion: () -> Unit, failure: () -> Unit) {
    val retrofit = Service.prepare(context)
    val service = retrofit.create(AuthInterface::class.java)

    val body = Request()
    body.device_token = deviceToken
    body.app_version = version

    service.updateDeviceToken(body).enqueue(object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            failure()
        }

        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
            if(response?.isSuccessful!!) {
                completion()
            } else {
                when(response.code()) {
                    Service.badRequest -> {
                        Util.showAlert(context, "Error", "Error al actualizar su device token")
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